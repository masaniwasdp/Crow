package io.github.masaniwasdp.crow

import android.graphics.Bitmap
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat

enum class CameraType {
    Normal,
    Inverse,
    Gray,
    Red,
    Green,
    Blue
}

class Camera : CameraBridgeViewBase.CvCameraViewListener2 {
    override fun onCameraViewStarted(width: Int, height: Int) {
        assert(width > 0)
        assert(height > 0)

        frame = Mat(height, width, CvType.CV_8UC3)
    }

    override fun onCameraViewStopped() {
        frame.release()
    }

    override fun onCameraFrame(newFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        assert(frame.width() > 0)
        assert(frame.height() > 0)
        assert(frame.type() == CvType.CV_8UC3)

        when (type) {
            CameraType.Normal -> newFrame.rgba().copyTo(frame)

            CameraType.Inverse -> Core.bitwise_not(newFrame.rgba(), frame)

            CameraType.Gray -> newFrame.gray().copyTo(frame)

            CameraType.Red -> processWithSplit4(newFrame.rgba()) { it, _, _, _ -> it.copyTo(frame) }

            CameraType.Green -> processWithSplit4(newFrame.rgba()) { _, it, _, _ -> it.copyTo(frame) }

            CameraType.Blue -> processWithSplit4(newFrame.rgba()) { _, _, it, _ -> it.copyTo(frame) }
        }

        return frame
    }

    /**
     * Akiras bitmap-bildon de fotilo.
     *
     * @return Bitmap-bildo de fotilo.
     */
    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.ARGB_8888)

        Utils.matToBitmap(frame, bitmap)

        return bitmap
    }

    var type = CameraType.Blue

    private lateinit var frame: Mat
}

private fun <T> processWithSplit4(frame: Mat, process: (Mat, Mat, Mat, Mat) -> T): T {
    require(frame.channels() == 4) { "The number of channels must be 4." }

    val channels = List<Mat?>(4, { null })

    Core.split(frame, channels)

    assert(channels.all { it is Mat })

    val result = process(channels[0] as Mat, channels[1] as Mat, channels[2] as Mat, channels[3] as Mat)

    channels.forEach { it?.release() }

    return result
}
