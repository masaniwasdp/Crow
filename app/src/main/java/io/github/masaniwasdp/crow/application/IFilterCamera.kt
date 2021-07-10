package io.github.masaniwasdp.crow.application

import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Mat

interface IFilterCamera {
    enum class Filter { None, Negative, Grayscale, Red, Green, Blue; }

    /**
     * Inicializas la fotilan kadron.
     *
     * @param w Larĝeco de la kadro.
     * @param h Alteco de la kadro.
     */
    fun initializeFrame(w: Int, h: Int)

    /** Liberigas la fotilan kadron. */
    fun finaliseFrame()

    fun updateFrame(frame: CameraBridgeViewBase.CvCameraViewFrame): Mat

    /** Savas la fotilan kadron. */
    fun saveFrame()

    fun useFilter(filter: Filter)
}