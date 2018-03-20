package io.github.masaniwasdp.crow

import android.content.ContentResolver
import io.github.masaniwasdp.crow.lib.Filter
import io.github.masaniwasdp.crow.lib.save
import io.github.masaniwasdp.crow.lib.toBitmap
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.CvType
import org.opencv.core.Mat

/**
 * Ĉefa Modelo de apliko.
 *
 * @constructor Kreas la modelon.
 * @property notifier Informanto kiam io okazis en la modelo. La argumento estas ID de kordo.
 */
class MainModel(private val notifier: (Int) -> Unit) {
    /**
     * Inicializas la fotilan kadron de la modelo.
     *
     * @param w Larĝeco de la kadro.
     * @param h Alteco de la kadro.
     */
    fun initialize(w: Int, h: Int) {
        require(w > 0) { "The w must be greater than 0." }
        require(h > 0) { "The h must be greater than 0." }

        frame?.release()

        frame = Mat(h, w, CvType.CV_8UC4)
    }

    /** Liberigas la fotilan kadron de la modelo. */
    fun release() {
        frame?.release()

        frame = null
    }

    /**
     * Ĝisdatigas la fotilan kadron de la modelo.
     *
     * @param source La fonta fotila bildo.
     */
    fun update(source: CameraBridgeViewBase.CvCameraViewFrame) {
        frame?.let {
            filter.apply(source.rgba(), it)
        }
    }

    /**
     * Savas la fotilan kadron kiel jpeg-bildo.
     *
     * @param resolver Content-resolver.
     */
    fun save(resolver: ContentResolver) {
        frame?.let {
            try {
                resolver.save(it.toBitmap(), DIRECTORY)

                notifier(R.string.success)
            } catch (e: Exception) {
                notifier(R.string.failed)
            }
        }
    }

    /** Filtrilo de la fotilo en la modelo. */
    var filter = Filter.None

    /** Fotila kadro. */
    var frame: Mat? = null
        private set
}

/** La dosierujo por savi bildojn. */
private const val DIRECTORY = "/Crow"
