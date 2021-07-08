package io.github.masaniwasdp.crow.presentation

import io.github.masaniwasdp.crow.R
import io.github.masaniwasdp.crow.function.*
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.CvType
import org.opencv.core.Mat

class CameraPresenter(private val view: ICameraView, private val storage: ICameraStorage) {
    /** Filtrila reĝimoj. */
    enum class Mode { None, Negative, Grayscale, Red, Green, Blue; }

    /**
     * Inicializas la fotilan kadron de la modelo.
     *
     * @param w Larĝeco de la kadro.
     * @param h Alteco de la kadro.
     */
    fun initialize(w: Int, h: Int) {
        require(w > 0) { "The w must be greater than 0." }
        require(h > 0) { "The h must be greater than 0." }

        frame?.release()

        frame = Mat(h, w, CvType.CV_8UC4)
    }

    /** Liberigas la fotilan kadron de la modelo. */
    fun release() {
        frame?.release()

        frame = null
    }

    /**
     * Ĝisdatigas la fotilan kadron de la modelo.
     *
     * @param source La fonta fotila bildo.
     */
    fun update(source: CameraBridgeViewBase.CvCameraViewFrame) {
        frame?.let {
            when (mode) {
                Mode.None -> source.rgba().copyTo(it)
                Mode.Negative -> negate(source.rgba(), it)
                Mode.Grayscale -> grayscale(source.rgba(), it)
                Mode.Red -> redFilter(source.rgba(), it)
                Mode.Green -> greenFilter(source.rgba(), it)
                Mode.Blue -> blueFilter(source.rgba(), it)
            }
        }
    }

    /** Savas la fotilan kadron kiel jpeg-bildo. */
    fun save() {
        frame?.let {
            try {
                storage.save(toBitmap(it))

                view.notifyMessage(R.string.success)
            } catch (e: Exception) {
                view.notifyMessage(R.string.failed)
            }
        }
    }

    /** Filtrila reĝimo. */
    var mode = Mode.None

    /** Fotila kadro. */
    var frame: Mat? = null
        private set
}
