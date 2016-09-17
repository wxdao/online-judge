package com.vecsight.oj.resource

import com.vecsight.oj.Application
import com.vecsight.oj.config.ConfigHelper
import com.vecsight.oj.config.MainConfig
import com.vecsight.oj.pojo.Problem
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.glassfish.jersey.media.multipart.FormDataParam
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("manage")
class Manage {
    val logger = LoggerFactory.getLogger("Manage Resource")!!

    val mainConfig = ConfigHelper.retrieve(MainConfig::class.java)!!

    data class ProblemResponse(val id: String)

    @Path("problem")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun problem(@FormDataParam("meta") meta: Problem?, @FormDataParam("pack") pack: InputStream?): ProblemResponse {
        if (meta == null || pack == null) {
            throw WebException(ErrorEntity.INVALID_REQUEST, 400)
        }
        val problem = Application.context!!.getProblemModel().update(meta) ?: throw WebException(ErrorEntity.INTERNAL, 500)
        val archive = ZipInputStream(pack)
        val packPath = mainConfig.packRoot + problem.id
        try {
            FileUtils.deleteDirectory(File(packPath))
        } catch (e: Exception) {
            logger.error(e.message)
            throw WebException(ErrorEntity.INTERNAL, 500)
        }
        File(packPath).mkdir()
        var entry = archive.nextEntry
        while (entry != null) {
            val f = File(packPath + File.separator + entry.name)
            if (entry.isDirectory) {
                f.mkdirs()
                continue
            }
            File(f.parent).mkdirs()
            val os = FileOutputStream(f)
            IOUtils.copy(archive, os)
            os.flush()
            os.close()
            entry = archive.nextEntry
        }
        archive.closeEntry()
        archive.close()
        return ProblemResponse(problem.id!!)
    }
}