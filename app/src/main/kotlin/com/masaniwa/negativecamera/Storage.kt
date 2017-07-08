package com.masaniwa.negativecamera

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
 * Bitmap画像をjpegでストレージに保存してコンテンツリゾルバに登録する。
 * @param  bitmap      Bitmap画像。
 * @param  directory   保存先ディレクトリ。
 * @param  resolver    コンテンツリゾルバ。
 * @throws IOException 画像を保存できなかった場合。
 */
fun save(bitmap: Bitmap, directory: String, resolver: ContentResolver) {
    val file = File(Environment.getExternalStorageDirectory().path + directory)

    if (!file.exists()) {
        file.mkdir()
    }

    val name = SimpleDateFormat(format, Locale.US).format(Date()) + extension
    val path = file.absolutePath + slash + name

    savePicture(bitmap, path)
    saveIndex(resolver, name, path)
}

/**
 * Bitmap画像をjpegでストレージに保存する。
 * @param  bitmap      Bitmap画像。
 * @param  path        保存先ファイルパス。
 * @throws IOException 画像を保存できなかった場合。
 */
private fun savePicture(bitmap: Bitmap, path: String) {
    val stream = FileOutputStream(path)

    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)

    stream.flush()
    stream.close()
}

/**
 * コンテンツリゾルバにファイル情報を登録する。
 * @param resolver コンテンツリゾルバ。
 * @param name     ファイルの名前。
 * @param path     ファイルパス。
 */
private fun saveIndex(resolver: ContentResolver, name: String, path: String) {
    val values = ContentValues()

    values.put(MediaStore.Images.Media.MIME_TYPE, type)
    values.put(MediaStore.Images.Media.TITLE, name)
    values.put(dataKey, path)

    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
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
