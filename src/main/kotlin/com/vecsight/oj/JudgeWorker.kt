package com.vecsight.oj

import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.Volume
import com.vecsight.oj.config.ConfigHelper
import com.vecsight.oj.config.MainConfig
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object JudgeWorker {
    val executor = Executors.newFixedThreadPool(2)!!

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
            val problem = context.getProblemModel().getById(record.problemId ?: continue) ?: continue
            val problemPackPath = mainConfig.packRoot + problem.id
            val recordPackPath = mainConfig.packRoot + "records/" + problem.id
            FileUtils.copyDirectory(File(problemPackPath), File(recordPackPath))
            FileUtils.write(File(recordPackPath + "/source.cpp"), record.source, "UTF-8")
            val dockerClient = context.getDockerClient()
            val volume = Volume(recordPackPath)
            val container = dockerClient.createContainerCmd("judge:1")
                    .withBinds(Bind("/src", volume))
                    .withNetworkDisabled(true)
                    .withWorkingDir("/src")
                    .exec()
            dockerClient.startContainerCmd(container.id).exec()
            // TODO: check if result exists and process
        }
    }

    fun startLoop() {
        executor.execute(loop)
        executor.execute(loop)
    }

    fun stopLoop() {
        executor.shutdown()
        executor.awaitTermination(30, TimeUnit.SECONDS)
    }
}