package io.github.masaniwasdp.crow.infrastructure

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import io.github.masaniwasdp.crow.contract.ICameraStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * La ekstera stokado.
 *
 * @property resolver Content-resolver.
 */
class ExternalStorage(private val resolver: ContentResolver) : ICameraStorage {
    override fun save(bitmap: Bitmap) {
        reserveDirectory().let {
            val name = SimpleDateFormat(FORMAT, Locale.US).format(Date()) + EXTENSION
            val path = it.absolutePath + File.pathSeparator + name

            FileOutputStream(path).use { x ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, x)
            }

            store(resolver, path, name)
        }
    }
}

/**
 * Rezervas dosierujon en stokado por savi dosierojn.
 *
 * @return Dosierujo por savi dosierojn.
 * @throws IOException Kiam malsukcesis rezervi la dosierujon.
 */
private fun reserveDirectory(): File {
    return File(Environment.getExternalStorageDirectory().path + DIRECTORY).also {
        if (!it.exists() && !it.mkdir()) {
            throw IOException("Failed to make the directory.")
        }
    }
}

/**
 * Registras jpeg-bildon al content-resolver.
 *
 * @param resolver Content-resolver.
 * @param path Vojo de bildo.
 * @param name Nomo de bildo.
 */
private fun store(resolver: ContentResolver, path: String, name: String) {
    require(path != "") { "The path must not be empty." }
    require(name != "") { "The name must not be empty." }

    ContentValues()
            .apply {
                put(MediaStore.Images.Media.MIME_TYPE, TYPE)
                put(MediaStore.Images.Media.TITLE, name)
                put(DATA_KEY, path)
            }
            .let {
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it)
            }
}

/** La dosierujo por savi bildojn. */
private const val DIRECTORY = "/Crow"

/** La dosiero etendo. */
private const val EXTENSION = ".jpg"

/** La MIME-tipo de bildoj dosieroj. */
private const val TYPE = "image/jpeg"

/** La ŝlosilo indikanta datumojn de content-URI. */
private const val DATA_KEY = "_data"

/** La formato de dato kaj tempo de dosiernomoj kiu estos savita. */
private const val FORMAT = "yyyyMMdd_hhmmss"

/** La kvalito de savi bildojn. */
private const val QUALITY = 95
