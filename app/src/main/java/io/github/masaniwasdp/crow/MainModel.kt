package io.github.masaniwasdp.crow

import android.content.ContentResolver
import io.github.masaniwasdp.crow.lib.pickChannel
import io.github.masaniwasdp.crow.lib.save
import io.github.masaniwasdp.crow.lib.toBitmap
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat

/** La tipoj de la fotilo. */
enum class CameraType { Normal, Inverse, Gray, Red, Green, Blue }

/**
 * Ĉefa Modelo de apliko.
 *
 * @constructor Kreas la modelon.
 * @property notifier Informanto kiam io okazis en la modelo.
 */
class MainModel(private val notifier: (resId: Int) -> Unit) {
    /**
     * Inicializas la fotilan kadron de la modelo.
     *
     * @param width Larĝeco de la kadro.
     * @param height Alteco de la kadro.
     */
    fun initializeFrame(width: Int, height: Int) {
        require(width > 0) { "The width must be greater than 0." }
        require(height > 0) { "The height must be greater than 0." }

        frame?.release()

        frame = Mat(height, width, CvType.CV_8UC3)
    }

    /** Liberigas la fotilan kadron de la modelo. */
    fun releaseFrame() {
        frame?.release()

        frame = null
    }

    /**
     * Ĝisdatigas la fotilan kadron de la modelo.
     *
     * @param newFrame La fonta fotila bildo.
     */
    fun updateFrame(newFrame: CameraBridgeViewBase.CvCameraViewFrame) {
        frame?.let {
            when (type) {
                CameraType.Normal -> newFrame.rgba().copyTo(it)

                CameraType.Inverse -> Core.bitwise_not(newFrame.rgba(), it)

                CameraType.Gray -> newFrame.gray().copyTo(it)

                CameraType.Red -> newFrame.rgba().pickChannel(0, it)

                CameraType.Green -> newFrame.rgba().pickChannel(1, it)

                CameraType.Blue -> newFrame.rgba().pickChannel(2, it)
            }
        }
    }

    /**
     * Savas la fotilan kadron kiel jpeg-bildo.
     *
     * @param resolver Content-resolver.
     */
    fun saveFrame(resolver: ContentResolver) {
        frame?.let {
            try {
                save(it.toBitmap(), DIRECTORY, resolver)

                notifier(R.string.success)
            } catch (e: Exception) {
                notifier(R.string.failed)
            }
        }
    }

    /** Tipo de la fotilo en la modelo. */
    var type = CameraType.Normal

    /** Fotila kadro. */
    var frame: Mat? = null
        private set
}

/** La dosierujo por savi bildojn. */
private const val DIRECTORY = "/Crow/"
