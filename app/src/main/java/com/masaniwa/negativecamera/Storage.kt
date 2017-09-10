package com.masaniwa.negativecamera

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore.Images.Media.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.US

/**
 * Escepto de stokado.
 *
 * @param message Mesaĝo.
 * @param cause Kaûzo de escepto.
 * @constructor Kreas escepton.
 */
class StorageException(message: String, cause: Throwable?) : IOException(message, cause)

/**
 * Savas bitmap-bildon kiel jpeg-bildo en stokado kaj registras ĝin al content-resolver.
 *
 * @param bitmap Bitmap-bildo kiu estos savita.
 * @param directory Bildo dosierujo.
 * @param resolver Content-resolver.
 * @throws StorageException Se malsukcesis savi la bildon.
 */
fun savePicture(bitmap: Bitmap, directory: String, resolver: ContentResolver) {
    require(directory != "")

    val file = File(getExternalStorageDirectory().path + directory)

    if (!file.exists()) {
        if (!file.mkdir()) throw StorageException("Failed to make the directory.", null)
    }

    val name = SimpleDateFormat(format, US).format(Date()) + extension

    val path = file.absolutePath + slash + name

    saveBitmap(bitmap, path)

    saveIndex(resolver, name, path)
}

/**
 * Savas bitmap-bildon kiel jpeg-bildo en stokado.
 *
 * @param bitmap Bitmap-bildo kiu estos savita.
 * @param path Vojo por savi bildon.
 * @throws StorageException Se malsukcesis savi la bildon.
 */
private fun saveBitmap(bitmap: Bitmap, path: String) {
    require(path != "")

    try {
        FileOutputStream(path).use { bitmap.compress(JPEG, quality, it) }
    } catch (e: IOException) {
        throw StorageException("Failed to save the image.", e)
    }
}

/**
 * Registras dosieran informon al content-resolver.
 *
 * @param resolver Content-resolver.
 * @param name Nomo kiu estos registrita.
 * @param path Vojo de dosiero.
 */
private fun saveIndex(resolver: ContentResolver, name: String, path: String) {
    require(name != "")
    require(path != "")

    val values = ContentValues().apply {
        put(MIME_TYPE, type)
        put(TITLE, name)
        put(dataKey, path)
    }

    resolver.insert(EXTERNAL_CONTENT_URI, values)
}

/** La vojo apartigilo. */
private const val slash = "/"

/** La dosiero etendo. */
private const val extension = ".jpg"

/** La MIME-tipo de bildoj dosieroj. */
private const val type = "image/jpeg"

/** La ŝlosilo indikanta datumojn de content-URI. */
private const val dataKey = "_data"

/** La formato de dato kaj tempo de dosiernomoj kiu estos savita. */
private const val format = "yyyyMMdd.hhmmss"

/** La kvalito de savi bildojn. */
private const val quality = 95
