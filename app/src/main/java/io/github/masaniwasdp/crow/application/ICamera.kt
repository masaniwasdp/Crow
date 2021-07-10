package io.github.masaniwasdp.crow.application

import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Mat

interface ICamera {
    enum class Mode { None, Negative, Grayscale, Red, Green, Blue; }

    interface IView {
        fun notifySuccess()
        fun notifyFailed()
    }

    /**
     * Inicializas la fotilan kadron.
     *
     * @param w Larĝeco de la kadro.
     * @param h Alteco de la kadro.
     */
    fun initialize(w: Int, h: Int)

    /** Liberigas la fotilan kadron. */
    fun finalise()

    fun updateFrame(frame: CameraBridgeViewBase.CvCameraViewFrame): Mat

    /** Savas la fotilan kadron. */
    fun saveFrame()

    fun modeChange(mode: Mode)
}