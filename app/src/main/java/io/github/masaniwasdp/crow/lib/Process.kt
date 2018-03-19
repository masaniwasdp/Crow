package io.github.masaniwasdp.crow.lib

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

/**
 * Filtriloj de bildoj.
 *
 * La argumentoj estas fonta bildo kaj destino.
 */
enum class Filter : (Mat, Mat) -> Unit {
    Normal {
        override fun invoke(s: Mat, d: Mat) = s.copyTo(d)
    },

    Inverse {
        override fun invoke(s: Mat, d: Mat) = Core.bitwise_not(s, d)
    },

    Gray {
        override fun invoke(s: Mat, d: Mat) = Imgproc.cvtColor(s, d, Imgproc.COLOR_RGBA2GRAY)
    },

    Red {
        override fun invoke(s: Mat, d: Mat) = s.pickChannel(0, d)
    },

    Green {
        override fun invoke(s: Mat, d: Mat) = s.pickChannel(1, d)
    },

    Blue {
        override fun invoke(s: Mat, d: Mat) = s.pickChannel(2, d)
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
    require(index >= 0) { "The index must not be negative." }
    require(channels() > index) { "The index was out of bounds." }

    List(channels(), { null as Mat? }).let {
        Core.split(this, it)

        it[index]!!.copyTo(frame)

        it.forEach { it!!.release() }
    }
}
