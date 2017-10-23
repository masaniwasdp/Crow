package io.github.masaniwasdp.crow.model

import android.content.ContentResolver
import io.github.masaniwasdp.crow.R.string.saving_failed
import io.github.masaniwasdp.crow.R.string.saving_success
import io.github.masaniwasdp.crow.model.CameraType.*
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.core.Core.bitwise_not
import org.opencv.core.CvType.CV_8UC3
import org.opencv.core.Mat

typealias Notifier = (resId: Int) -> Unit

enum class CameraType {
    Normal,
    Inverse,
    Gray,
    Red,
    Green,
    Blue
}

class MainModel(private val notifier: Notifier) {
    fun initializeFrame(width: Int, height: Int) {
        require(width > 0) { "The width must be more than 0." }
        require(height > 0) { "The height must be more than 0." }

        frame?.release()

        frame = Mat(height, width, CV_8UC3)
    }

    fun releaseFrame() {
        frame?.release()
    }

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

    fun saveFrame(resolver: ContentResolver) {
        checkNotNull(frame) { "The frame is not initialized." }

        try {
            save(frame!!.toBitmap(), DIRECTORY, resolver)

            notifier(saving_success)
        } catch (e: Exception) {
            notifier(saving_failed)
        }
    }

    var type = Normal

    var frame: Mat? = null
        private set
}

private const val DIRECTORY = "/Crow/"
