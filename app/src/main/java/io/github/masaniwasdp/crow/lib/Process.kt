package io.github.masaniwasdp.crow.lib

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

/** Filtriloj de bildoj. */
enum class Filter {
    None, Nega, Gray, R, G, B;

    /**
     * Aplikas la filtrilon al bildo.
     *
     * @param src Fonta bildo.
     * @param dst Destino de filtrita bildo.
     */
    fun apply(src: Mat, dst: Mat) {
        require(src.type() == CvType.CV_8UC4) { "The type of src must be CV_8UC4." }

        filter(src, dst)
    }

    /**
     * Filtras bildon.
     *
     * @param src Fonta bildo.
     * @param dst Destino de filtrita bildo.
     */
    private fun filter(src: Mat, dst: Mat) = when (this) {
        None -> src.copyTo(dst)

        Nega -> Core.bitwise_not(src, dst)

        Gray -> Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGBA2GRAY)

        R -> src.pickChannel(0, dst)

        G -> src.pickChannel(1, dst)

        B -> src.pickChannel(2, dst)
    }
}

/**
 * Konvertas Mat-bildon en Bitmap-bildo.
 *
 * @receiver Bildo kiu estos konvertita.
 * @return Konvertita Bitmap-bildo.
 */
fun Mat.toBitmap(): Bitmap {
    check(cols() > 0) { "The cols must be greater than 0." }
    check(rows() > 0) { "The rows must be greater than 0." }

    return Bitmap
            .createBitmap(cols(), rows(), Bitmap.Config.ARGB_8888)
            .also { Utils.matToBitmap(this, it) }
}

/**
 * Elektas specifitan kanalon de la bildo.
 *
 * @receiver Bildo por elekti kanalon.
 * @param index Nombro de kanalo.
 * @param frame Destino de elektata kanalo.
 */
private fun Mat.pickChannel(index: Int, frame: Mat) {
    require(0.until(channels()).contains(index)) { "The index out of bounds." }

    List(channels(), { null as Mat? }).let {
        Core.split(this, it)

        it[index]!!.copyTo(frame)

        it.forEach { it!!.release() }
    }
}
