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
 * ストレージに関する例外。
 *
 * @param message 例外メッセージ。
 * @param cause 原因となった例外。
 * @constructor 例外を作成する。
 */
class StorageException(message: String, cause: Throwable?) : IOException(message, cause)

/**
 * Bitmap画像をjpegでストレージに保存してコンテンツリゾルバに登録する。
 *
 * @param bitmap Bitmap画像。
 * @param directory 保存先ディレクトリ。
 * @param resolver コンテンツリゾルバ。
 * @throws StorageException 画像を保存できなかった場合。
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
 * Bitmap画像をjpegでストレージに保存する。
 *
 * @param bitmap Bitmap画像。
 * @param path 保存先ファイルパス。
 * @throws StorageException 画像を保存できなかった場合。
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
 * コンテンツリゾルバにファイル情報を登録する。
 *
 * @param resolver コンテンツリゾルバ。
 * @param name ファイルの名前。
 * @param path ファイルパス。
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

/** ファイルパス区切り文字。 */
private const val slash = "/"

/** ファイル拡張子。 */
private const val extension = ".jpg"

/** ファイルのMIMEタイプ。 */
private const val type = "image/jpeg"

/** コンテンツURIのデータを示すキー。 */
private const val dataKey = "_data"

/** 保存する画像のファイル名に付加する日時のフォーマット。 */
private const val format = "yyyyMMdd.hhmmss"

/** 画像を保存するときの品質。 */
private const val quality = 95
