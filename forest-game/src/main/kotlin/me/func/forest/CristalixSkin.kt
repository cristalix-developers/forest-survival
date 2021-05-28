package me.func.forest

import org.apache.commons.codec.digest.DigestUtils
import java.io.IOException
import java.net.URL
import java.util.*

class UrlSkinData(uuid: UUID) {
    val url: String = "https://webdata.c7x.dev/textures/skin/$uuid"
    val digest: String = getDigest(url)

    companion object {
        private fun getDigest(url: String): String {
            val modificationDate: String
            try {
                val connection = URL(url).openConnection()
                modificationDate = connection.getHeaderField("Last-Modified")
                if (modificationDate == null) {
                    throw IOException()
                }
            } catch (ignored: IOException) {
            }
            return DigestUtils.sha1Hex(url)
        }
    }
}