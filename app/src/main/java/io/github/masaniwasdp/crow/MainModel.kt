package io.github.masaniwasdp.crow

import android.content.ContentResolver
import io.github.masaniwasdp.crow.CameraType.*
import io.github.masaniwasdp.crow.R.string.saving_failed
import io.github.masaniwasdp.crow.R.string.saving_success
import io.github.masaniwasdp.crow.lib.pickChannel
import io.github.masaniwasdp.crow.lib.save
import io.github.masaniwasdp.crow.lib.toBitmap
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.core.Core.bitwise_not
import org.opencv.core.CvType.CV_8UC3
import org.opencv.core.Mat

/** La tipoj de la fotilo. */
enum class CameraType { Normal, Inverse, Gray, Red, Green, Blue }

/**
 * Ĉefa Modelo de apliko.
 *
 * @constructor Kreas la modelon.
 * @property notifier Informanto kiam io okazis en la modelo.
 */
class MainModel(private val notifier: Notifier) {
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

        frame = Mat(height, width, CV_8UC3)
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
    fun updateFrame(newFrame: CvCameraViewFrame) {
        checkNotNull(frame) { "The frame is not initialized." }

        when (type) {
            Normal -> newFrame.rgba().copyTo(frame)

            Inverse -> bitwise_not(newFrame.rgba(), frame)

            Gray -> newFrame.gray().copyTo(frame)

            Red -> newFrame.rgba().pickChannel(0, frame!!)

            Green -> newFrame.rgba().pickChannel(1, frame!!)

            Blue -> newFrame.rgba().pickChannel(2, frame!!)
        }
    }

    /**
     * Savas la fotilan kadron kiel jpeg-bildo.
     *
     * @param resolver Content-resolver.
     */
    fun saveFrame(resolver: ContentResolver) {
        checkNotNull(frame) { "The frame is not initialized." }

        try {
            save(frame!!.toBitmap(), DIRECTORY, resolver)

            notifier(saving_success)
        } catch (e: Exception) {
            notifier(saving_failed)
        }
    }

    /** Tipo de la fotilo en la modelo. */
    var type = Normal

    /** Fotila kadro. */
    var frame: Mat? = null
        private set
}

/** La informanto kiam io okazis. */
private typealias Notifier = (resId: Int) -> Unit

/** La dosierujo por savi bildojn. */
private const val DIRECTORY = "/Crow/"
