package com.masaniwa.negativecamera

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore.Images.Media
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Bitmap画像をjpegでストレージに保存してコンテンツリゾルバに登録する。
 * @param  bitmap    Bitmap画像。
 * @param  directory 保存先ディレクトリ。
 * @param  resolver  コンテンツリゾルバ。
 * @throws java.io.IOException 画像を保存できなかった場合。
 */
fun savePicture(bitmap: Bitmap, directory: String, resolver: ContentResolver) {
    val file = File(getExternalStorageDirectory().path + directory)

    if (!file.exists()) {
        file.mkdir()
    }

    val name = SimpleDateFormat(format, Locale.US).format(Date()) + extension
    val path = file.absolutePath + slash + name

    saveBitmap(bitmap, path)
    saveIndex(resolver, name, path)
}

/**
 * Bitmap画像をjpegでストレージに保存する。
 * @param  bitmap Bitmap画像。
 * @param  path   保存先ファイルパス。
 * @throws java.io.IOException 画像を保存できなかった場合。
 */
private fun saveBitmap(bitmap: Bitmap, path: String) {
    FileOutputStream(path).use { bitmap.compress(CompressFormat.JPEG, quality, it) }
}

/**
 * コンテンツリゾルバにファイル情報を登録する。
 * @param resolver コンテンツリゾルバ。
 * @param name     ファイルの名前。
 * @param path     ファイルパス。
 */
private fun saveIndex(resolver: ContentResolver, name: String, path: String) {
    val values = ContentValues().apply {
        put(Media.MIME_TYPE, type)
        put(Media.TITLE, name)
        put(dataKey, path)
    }

    resolver.insert(Media.EXTERNAL_CONTENT_URI, values)
}

/** ファイルパス区切り文字。 */
private val slash = "/"

/** ファイル拡張子。 */
private val extension = ".jpg"

/** ファイルのMIMEタイプ。 */
private val type = "image/jpeg"

/** コンテンツURIのデータを示すキー。 */
private val dataKey = "_data"

/** 保存する画像のファイル名に付加する日時のフォーマット。 */
private val format = "yyyyMMdd.hhmmss"

/** 画像を保存するときの品質。 */
private val quality = 95
