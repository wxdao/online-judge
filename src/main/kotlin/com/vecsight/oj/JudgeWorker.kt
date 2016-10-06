package com.vecsight.oj

import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.Volume
import com.vecsight.oj.config.ConfigHelper
import com.vecsight.oj.config.MainConfig
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object JudgeWorker {
    val logger = LoggerFactory.getLogger("Judge Worker")

    val executor = Executors.newFixedThreadPool(1)!!

    val mainConfig = ConfigHelper.retrieve(MainConfig::class.java)!!

    var stopFlag = false

    val loop = Runnable {
        mainLoop@ while (!Thread.currentThread().isInterrupted && !stopFlag) {
            val context = Application.context!!
            val recordId = context.getJudgeQueueModel().remove()
            if (recordId == null) {
                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                }
                continue
            }
            logger.info("judging $recordId")
            val record = context.getRecordModel().getById(recordId) ?: continue
            val problemPackPath = mainConfig.packRoot + record.problemId
            val recordPackPath = mainConfig.packRoot + "records/" + record.id
            try {
                FileUtils.copyDirectory(File(problemPackPath), File(recordPackPath))
                File(recordPackPath + "/meta").mkdir()
                FileUtils.write(File(recordPackPath + "/source.cpp"), record.source, "UTF-8")
            } catch (e: Exception) {
                logger.error(e.message)
                continue
            }
            val dockerClient = context.getDockerClient()
            val volume = Volume("/src")
            logger.info("$recordId - creating container")
            val container = dockerClient.createContainerCmd(mainConfig.image)
                    .withBinds(Bind(recordPackPath, volume))
                    .withNetworkDisabled(true)
                    .withMemory(1024 * 1024 * 128) // 128M
                    .withWorkingDir("/src")
                    .withCmd("bash", "process.sh")
                    .exec()
            logger.info("$recordId - starting container")
            dockerClient.startContainerCmd(container.id).exec()
            logger.info("$recordId - waiting for result")
            var timeout = 30
            while (!File(recordPackPath + "/meta/result").exists()) {
                try {
                    val inspect = dockerClient.inspectContainerCmd(container.id).exec()
                    if (inspect.state.running == false || timeout-- <= 0) {
                        try {
                            dockerClient.removeContainerCmd(container.id).withForce(true).exec()
                        } catch (e: Exception) {
                            logger.error(e.message)
                        }
                        continue@mainLoop
                    }
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    try {
                        dockerClient.removeContainerCmd(container.id).withForce(true).exec()
                    } catch (e: Exception) {
                        logger.error(e.message)
                    }
                    break@mainLoop
                }
            }
            try {
                logger.info("$recordId - recording result")
                val result = FileUtils.readFileToString(File(recordPackPath + "/meta/result"), "UTF-8")
                logger.info("$recordId - result: $result")
                val message = try {
                    FileUtils.readFileToString(File(recordPackPath + "/meta/message"), "UTF-8")
                } catch (e: Exception) {
                    null
                }
                val input = try {
                    FileUtils.readFileToString(File(recordPackPath + "/meta/input.txt"), "UTF-8")
                } catch (e: Exception) {
                    null
                }
                val expect = try {
                    FileUtils.readFileToString(File(recordPackPath + "/meta/expect.txt"), "UTF-8")
                } catch (e: Exception) {
                    null
                }
                val output = try {
                    if (FileUtils.sizeOf(File(recordPackPath + "/meta/output.txt")) > 10 * 1024 * 1024) {
                        FileUtils.readFileToString(File(recordPackPath + "/meta/output.txt"), "UTF-8")
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
                val compilerError = try {
                    if (FileUtils.sizeOf(File(recordPackPath + "/meta/compiler.err")) > 10 * 1024 * 1024) {
                        FileUtils.readFileToString(File(recordPackPath + "/meta/compiler.err"), "UTF-8")
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
                val newRecord = record.copy(result = result, message = message, compilerError = compilerError, input = input, expect = expect, output = output)
                context.getRecordModel().update(newRecord)
            } catch (e: Exception) {
                logger.error(e.message)
                val newRecord = record.copy(result = "IE", message = "Internal error occurred")
                context.getRecordModel().update(newRecord)
            } finally {
                logger.info("$recordId - removing container")
                try {
                    dockerClient.removeContainerCmd(container.id).withForce(true).exec()
                } catch (e: Exception) {
                    logger.error(e.message)
                }
                logger.info("$recordId - deleting files")
                try {
                    FileUtils.deleteDirectory(File(recordPackPath))
                } catch (e: Exception) {
                    logger.error(e.message)
                }
            }
        }
    }

    fun startLoop() {
        executor.execute(loop)
        //executor.execute(loop)
    }

    fun stopLoop() {
        logger.info("stopping judge worker loop")
        executor.shutdownNow()
        stopFlag = true
        executor.awaitTermination(30, TimeUnit.SECONDS)
        Application.context!!.cleanUp()
    }
}