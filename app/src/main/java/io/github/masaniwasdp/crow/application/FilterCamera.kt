package io.github.masaniwasdp.crow.application

import android.graphics.Bitmap
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat

class FilterCamera(
    private val view: IFilterCameraView, private val store: IMediaStore
) : IFilterCamera {
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
                IFilterCamera.Mode.None -> frame.rgba().copyTo(it)
                IFilterCamera.Mode.Negative -> negate(frame.rgba(), it)
                IFilterCamera.Mode.Grayscale -> grayscale(frame.rgba(), it)
                IFilterCamera.Mode.Red -> redFilter(frame.rgba(), it)
                IFilterCamera.Mode.Green -> greenFilter(frame.rgba(), it)
                IFilterCamera.Mode.Blue -> blueFilter(frame.rgba(), it)
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

    override fun modeChange(mode: IFilterCamera.Mode) {
        this.mode = mode
    }

    private var mode = IFilterCamera.Mode.None

    /** Fotila kadro. */
    private var frame: Mat? = null
}
