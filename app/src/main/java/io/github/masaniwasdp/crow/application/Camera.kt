package io.github.masaniwasdp.crow.application

import android.graphics.Bitmap
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat

class Camera(
    private val view: ICamera.IView, private val store: IMediaStore
) : ICamera {
    override fun initialize(w: Int, h: Int) {
        frame?.release()

        frame = Mat(h, w, CvType.CV_8UC4)
    }

    override fun finalise() {
        frame?.release()

        frame = null
    }

    override fun updateFrame(frame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        this.frame?.let {
            when (mode) {
                ICamera.Mode.None -> frame.rgba().copyTo(it)
                ICamera.Mode.Negative -> negate(frame.rgba(), it)
                ICamera.Mode.Grayscale -> grayscale(frame.rgba(), it)
                ICamera.Mode.Red -> redFilter(frame.rgba(), it)
                ICamera.Mode.Green -> greenFilter(frame.rgba(), it)
                ICamera.Mode.Blue -> blueFilter(frame.rgba(), it)
            }

            return it
        }

        return frame.rgba()
    }

    override fun saveFrame() {
        frame?.let {
            try {
                Bitmap.createBitmap(it.cols(), it.rows(), Bitmap.Config.ARGB_8888)
                    .let { x ->
                        Utils.matToBitmap(it, x)

                        store.saveImage(x)
                    }

                view.notifySuccess()
            } catch (e: Exception) {
                view.notifyFailed()
            }
        }
    }

    override fun modeChange(mode: ICamera.Mode) {
        this.mode = mode
    }

    private var mode = ICamera.Mode.None

    /** Fotila kadro. */
    private var frame: Mat? = null
}
