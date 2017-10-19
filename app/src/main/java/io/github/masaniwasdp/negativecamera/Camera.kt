package io.github.masaniwasdp.negativecamera

import android.graphics.Bitmap
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat

enum class CameraType {
    Normal,
    Negative,
    Gray,
    Red,
    Green,
    Blue
}

class Camera : CameraBridgeViewBase.CvCameraViewListener2 {
    override fun onCameraViewStarted(width: Int, height: Int) {
        require(width > 0) { "The width must be more than 0." }
        require(height > 0) { "The height must be more than 0." }

        frame = Mat(height, width, CvType.CV_8UC3)
    }

    override fun onCameraViewStopped() {
        frame.release()
    }

    override fun onCameraFrame(newFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        when (type) {
            CameraType.Normal -> newFrame.rgba().copyTo(frame)

            CameraType.Negative -> Core.bitwise_not(newFrame.rgba(), frame)

            else -> {
            }
        }

        return frame
    }

    /**
     * Akiras bitmap-bildon de fotilo.
     *
     * @return Bitmap-bildo de fotilo.
     */
    fun getBitmap(): Bitmap {
        check(frame.width() > 0) { "The width must be more than 0." }
        check(frame.height() > 0) { "The height must be more than 0." }

        val bitmap = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.ARGB_8888)

        Utils.matToBitmap(frame, bitmap)

        return bitmap
    }

    var type = CameraType.Normal

    private lateinit var frame: Mat
}
