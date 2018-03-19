package io.github.masaniwasdp.crow.lib

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Savas bitmap-bildon kiel jpeg-bildo en stokado kaj registras ĝin.
 *
 * @receiver Content-resolver.
 * @param bitmap Bitmap-bildo kiu estos savita.
 * @param directory Bildo dosierujo.
 * @throws IOException Kiam malsukcesis savi la bildon.
 */
fun ContentResolver.save(bitmap: Bitmap, directory: String) {
    require(directory != "") { "The directory must not be empty." }

    directory.reserve().let {
        val name = SimpleDateFormat(FORMAT, Locale.US).format(Date()) + EXTENSION

        val path = it.absolutePath + File.pathSeparator + name

        bitmap.save(path)

        store(path, name)
    }
}

/**
 * Rezervas dosierujon en stokado por savi dosierojn.
 *
 * @receiver Vojo de dosierujo.
 * @return Dosierujo por savi dosierojn.
 * @throws IOException Kiam malsukcesis rezervi la dosierujon.
 */
private fun String.reserve(): File {
    require(this != "") { "The directory must not be empty." }

    return File(Environment.getExternalStorageDirectory().path + this).also {
        if (!it.exists() && !it.mkdir()) {
            throw IOException("Failed to make the directory.")
        }
    }
}

/**
 * Savas bitmap-bildon kiel jpeg-bildo.
 *
 * @receiver Bitmap-bildo kiu estos savita.
 * @throws IOException Kiam malsukcesis savi la bildon.
 */
private fun Bitmap.save(path: String) {
    require(path != "") { "The path must not be empty." }

    FileOutputStream(path).use {
        this.compress(Bitmap.CompressFormat.JPEG, QUALITY, it)
    }
}

/**
 * Registras jpeg-bildon al content-resolver.
 *
 * @receiver Content-resolver.
 * @param path Vojo de bildo.
 * @param name Nomo de bildo.
 */
private fun ContentResolver.store(path: String, name: String) {
    require(path != "") { "The path must not be empty." }
    require(name != "") { "The name must not be empty." }

    ContentValues()
            .apply {
                put(MediaStore.Images.Media.MIME_TYPE, TYPE)
                put(MediaStore.Images.Media.TITLE, name)
                put(DATA_KEY, path)
            }
            .let {
                insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it)
            }
}

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
