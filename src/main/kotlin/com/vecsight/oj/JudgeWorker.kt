package com.vecsight.oj

import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.Volume
import com.vecsight.oj.config.ConfigHelper
import com.vecsight.oj.config.MainConfig
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object JudgeWorker {
    val logger = LoggerFactory.getLogger("JudgeLogger")

    val executor = Executors.newFixedThreadPool(1)!!

    val loop = Runnable {
        while (!Thread.currentThread().isInterrupted) {
            val context = Application.context!!
            val mainConfig = ConfigHelper.retrieve(MainConfig::class.java) ?: continue
            val recordId = context.getJudgeQueueModel().remove()
            if (recordId == null) {
                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                }
                continue
            }
            val record = context.getRecordModel().getById(recordId) ?: continue
            val problemPackPath = mainConfig.packRoot + record.problemId
            val recordPackPath = mainConfig.packRoot + "records/" + record.problemId
            FileUtils.copyDirectory(File(problemPackPath), File(recordPackPath))
            File(recordPackPath + "/meta").mkdir()
            FileUtils.write(File(recordPackPath + "/source.cpp"), record.source, "UTF-8")
            val dockerClient = context.getDockerClient()
            val volume = Volume(recordPackPath)
            val container = dockerClient.createContainerCmd("judge:1")
                    .withBinds(Bind("/src", volume))
                    .withNetworkDisabled(true)
                    .withWorkingDir("/src")
                    .exec()
            dockerClient.startContainerCmd(container.id).exec()
            while (!File(recordPackPath + "/meta/result").exists()) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    dockerClient.removeContainerCmd(container.id).exec()
                    break
                }
            }
            try {
                val result = FileUtils.readFileToString(File(recordPackPath + "/meta/result"), "UTF-8")
                val message = FileUtils.readFileToString(File(recordPackPath + "/meta/message"), "UTF-8")
                val input = FileUtils.readFileToString(File(recordPackPath + "/meta/input.txt"), "UTF-8")
                val expect = FileUtils.readFileToString(File(recordPackPath + "/meta/expect.txt"), "UTF-8")
                val output = FileUtils.readFileToString(File(recordPackPath + "/meta/output.txt"), "UTF-8")
                val compilerError = FileUtils.readFileToString(File(recordPackPath + "/meta/compiler.err"), "UTF-8")
                val newRecord = record.copy(result = result, message = message, compilerError = compilerError, input = input, expect = expect, output = output)
                context.getRecordModel().update(newRecord)
            } catch (e: IOException) {
                logger.error(e.message)
                val newRecord = record.copy(result = "IE", message = "Internal error occurred")
                context.getRecordModel().update(newRecord)
            } finally {
                dockerClient.removeContainerCmd(container.id).exec()
            }
        }
    }

    fun startLoop() {
        executor.execute(loop)
        //executor.execute(loop)
    }

    fun stopLoop() {
        executor.shutdown()
        executor.awaitTermination(30, TimeUnit.SECONDS)
    }
}