package net.redstonecraft.kotlinmcbuilder

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import net.redstonecraft.redstoneapi.core.enums.MimeType
import net.redstonecraft.redstoneapi.core.http.HttpHeader
import net.redstonecraft.redstoneapi.core.http.HttpResponseCode
import net.redstonecraft.redstoneapi.webserver.obj.HttpHeaders
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse
import java.io.File
import java.io.FileInputStream

object Handlers {

    @JvmStatic
    fun handler(version: String, file: String): WebResponse {
        return if (version in Utils.availableVersions) {
            if (version !in Utils.dist.list()!!) {
                try {
                    Utils.generate(version)
                } catch (e: Throwable) {
                    return Main.errorHandler.handleError(HttpResponseCode.INTERNAL_SERVER_ERROR, "", emptyMap(), HttpHeaders(emptyList()))
                }
            }
            val dir = File(Utils.dist, version)
            val targetFile = File(dir, file)
            return try {
                if (!targetFile.canonicalPath.startsWith(dir.canonicalPath)) {
                    throw Error()
                }
                val builder = WebResponse.create().setContent(FileInputStream(targetFile)).setResponseCode(HttpResponseCode.OK)
                if (targetFile.extension == "jar") {
                    builder.addHeader(HttpHeader("Content-Type", MimeType.DEFAULT.mimetype))
                    builder.addHeader(HttpHeader("Content-Disposition", "inline; filename=\"${targetFile.name}\""))
                }
                builder.build()
            } catch (e: Throwable) {
                Main.errorHandler.handleError(HttpResponseCode.NOT_FOUND, "", emptyMap(), HttpHeaders(emptyList()))
            }
        } else {
            Main.errorHandler.handleError(HttpResponseCode.NOT_FOUND, "", emptyMap(), HttpHeaders(emptyList()))
        }
    }

    @JvmStatic
    fun manifest(): String {
        val metadata = Utils.kotlinVersionMetadata
        metadata.groupId = "net.redstonecraft"
        metadata.artifactId = "kotlinmc"
        return XmlMapper().enable(SerializationFeature.INDENT_OUTPUT).writer().withRootName("metadata").writeValueAsString(metadata)
    }

}
