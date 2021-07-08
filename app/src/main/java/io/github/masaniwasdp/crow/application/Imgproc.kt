package io.github.masaniwasdp.crow.application

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

fun negate(src: Mat, dst: Mat) {
    require(src.type() == CvType.CV_8UC4) { "The type of src must be CV_8UC4." }

    Core.bitwise_not(src, dst)
}

fun grayscale(src: Mat, dst: Mat) {
    require(src.type() == CvType.CV_8UC4) { "The type of src must be CV_8UC4." }

    Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGBA2GRAY)
}

fun redFilter(src: Mat, dst: Mat) {
    require(0.until(src.channels()).contains(0)) { "The index out of bounds." }

    pickChannel(src, dst, 0)
}

fun greenFilter(src: Mat, dst: Mat) {
    require(0.until(src.channels()).contains(1)) { "The index out of bounds." }

    pickChannel(src, dst, 1)
}

fun blueFilter(src: Mat, dst: Mat) {
    require(0.until(src.channels()).contains(2)) { "The index out of bounds." }

    pickChannel(src, dst, 2)
}

/**
 * Konvertas Mat-bildon en Bitmap-bildo.
 *
 * @param frame Bildo kiu estos konvertita.
 * @return Konvertita Bitmap-bildo.
 */
fun toBitmap(frame: Mat): Bitmap {
    require(frame.cols() > 0) { "The cols must be greater than 0." }
    require(frame.rows() > 0) { "The rows must be greater than 0." }

    return Bitmap
        .createBitmap(frame.cols(), frame.rows(), Bitmap.Config.ARGB_8888)
        .also { Utils.matToBitmap(frame, it) }
}

/**
 * Elektas specifitan kanalon de la bildo.
 *
 * @param src Bildo por elekti kanalon.
 * @param dst Destino de elektata kanalo.
 * @param index Nombro de kanalo.
 */
private fun pickChannel(src: Mat, dst: Mat, index: Int) {
    require(0.until(src.channels()).contains(index)) { "The index out of bounds." }

    (List(src.channels()) { null } as List<Mat?>)
        .let {
            Core.split(src, it)

            it[index]!!.copyTo(dst)

            it.forEach { x -> x!!.release() }
        }
}
