package io.github.masaniwasdp.crow

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.createBitmap
import org.opencv.android.Utils.matToBitmap
import org.opencv.core.Core.split
import org.opencv.core.Mat

/**
 * Elektas specifitan kanalon de la bildo.
 *
 * @receiver Bildo por elekti kanalon.
 * @param index Nombro de kanalo.
 * @param frame Destino de elektata kanalo.
 */
fun Mat.pickChannel(index: Int, frame: Mat) {
    require(index >= 0) { "The index must not be negative." }
    require(channels() > index) { "The index was out of bounds." }

    List<Mat?>(channels(), { null }).let {
        split(this, it)

        assert(it.all { it is Mat })

        it[index]!!.copyTo(frame)

        it.forEach { it!!.release() }
    }
}

/**
 * Konvertas Mat-bildon en Bitmap-bildo.
 *
 * @receiver Bildo kiu estos konvertita.
 * @return Konvertita Bitmap-bildo.
 */
fun Mat.toBitmap(): Bitmap {
    check(width() > 0) { "The width must be greater than 0." }
    check(height() > 0) { "The height must be greater than 0." }

    return createBitmap(width(), height(), ARGB_8888).also { matToBitmap(this, it) }
}
