package io.github.masaniwasdp.crow.presentation

import android.graphics.Bitmap
import io.github.masaniwasdp.crow.contract.ICameraFragment
import io.github.masaniwasdp.crow.contract.ICameraPresenter
import io.github.masaniwasdp.crow.contract.IMediaStore
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat

class CameraPresenter(
    private val fragment: ICameraFragment, private val store: IMediaStore) : ICameraPresenter {
    override fun initialize(w: Int, h: Int) {
        frame?.release()

        frame = Mat(h, w, CvType.CV_8UC4)
    }

    override fun finalise() {
        frame?.release()

        frame = null
    }

    override fun processFrame(frame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        this.frame?.let {
            when (mode) {
                ICameraPresenter.Mode.None -> frame.rgba().copyTo(it)
                ICameraPresenter.Mode.Negative -> negate(frame.rgba(), it)
                ICameraPresenter.Mode.Grayscale -> grayscale(frame.rgba(), it)
                ICameraPresenter.Mode.Red -> redFilter(frame.rgba(), it)
                ICameraPresenter.Mode.Green -> greenFilter(frame.rgba(), it)
                ICameraPresenter.Mode.Blue -> blueFilter(frame.rgba(), it)
            }

            return it
        }

        return Mat()
    }

    override fun saveFrame() {
        frame?.let {
            try {
                val bitmap = Bitmap
                    .createBitmap(it.cols(), it.rows(), Bitmap.Config.ARGB_8888)
                    .also { x -> Utils.matToBitmap(it, x) }

                store.saveImage(bitmap)

                fragment.notifySuccess()
            } catch (e: Exception) {
                fragment.notifyFailed()
            }
        }
    }

    override fun modeChange(mode: ICameraPresenter.Mode) {
        this.mode = mode
    }

    private var mode = ICameraPresenter.Mode.None

    /** Fotila kadro. */
    private var frame: Mat? = null
}
