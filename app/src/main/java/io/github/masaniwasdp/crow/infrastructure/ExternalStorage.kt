package io.github.masaniwasdp.crow.infrastructure

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.provider.MediaStore
import io.github.masaniwasdp.crow.presentation.ICameraStorage
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * La ekstera stokado.
 *
 * @property resolver Content-resolver.
 */
class ExternalStorage(private val resolver: ContentResolver) : ICameraStorage {
    override fun save(bitmap: Bitmap) {
        val name = SimpleDateFormat(FORMAT, Locale.US).format(Date()) + EXTENSION

        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val newValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, TYPE)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val item = resolver.insert(collection, newValues)!!

        resolver.openFileDescriptor(item, "w", null).use {
            FileOutputStream(it!!.fileDescriptor).use { s ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, s)
            }
        }

        val finalValue = ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }

        resolver.update(item, finalValue, null, null)
    }
}

/** La dosiero etendo. */
private const val EXTENSION = ".jpg"

/** La MIME-tipo de bildoj dosieroj. */
private const val TYPE = "image/jpeg"

/** La formato de dato kaj tempo de dosiernomoj kiu estos savita. */
private const val FORMAT = "yyyyMMdd_hhmmss"

/** La kvalito de savi bildojn. */
private const val QUALITY = 95
