package io.github.masaniwasdp.negativecamera

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore.Images.Media.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.US

/**
 * Escepto de stokado.
 *
 * @param message Mesaĝo.
 * @constructor Kreas escepton.
 */
class StorageException(message: String) : Exception(message)

/**
 * Savas bitmap-bildon kiel jpeg-bildo en stokado kaj registras ĝin al content-resolver.
 *
 * @param bitmap Bitmap-bildo kiu estos savita.
 * @param directory Bildo dosierujo.
 * @param resolver Content-resolver.
 * @throws StorageException Kiam malsukcesis fari la dosierujon.
 * @throws java.io.IOException Kiam malsukcesis savi la bildon.
 */
fun savePicture(bitmap: Bitmap, directory: String, resolver: ContentResolver) {
    require(directory != "") { "The directory must not be empty." }

    val file = File(getExternalStorageDirectory().path + directory)

    if (!file.exists()) {
        if (!file.mkdir()) throw StorageException("Failed to make the directory.")
    }

    val name = SimpleDateFormat(FORMAT, US).format(Date()) + EXTENSION

    val path = file.absolutePath + "/" + name

    FileOutputStream(path).use { bitmap.compress(JPEG, QUALITY, it) }

    val values = ContentValues().apply {
        put(MIME_TYPE, TYPE)
        put(TITLE, name)
        put(DATA_KEY, path)
    }

    resolver.insert(EXTERNAL_CONTENT_URI, values)
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
