package io.github.masaniwasdp.crow.application

import io.github.masaniwasdp.crow.R
import io.github.masaniwasdp.crow.contract.ICameraStorage
import io.github.masaniwasdp.crow.contract.ICameraView
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.CvType
import org.opencv.core.Mat

class Camera(private val view: ICameraView, private val storage: ICameraStorage) {
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
            apply(filter, source.rgba(), it)
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

    /** Filtrilo de la fotilo en la modelo. */
    var filter = CameraFilter.None

    /** Fotila kadro. */
    var frame: Mat? = null
        private set
}

/** Filtriloj de bildoj. */
enum class CameraFilter { None, Negative, Grayscale, Red, Green, Blue; }

/**
 * Filtras bildon.
 *
 * @param filter Filtrilo.
 * @param src Fonta bildo.
 * @param dst Destino de filtrita bildo.
 */
private fun apply(filter: CameraFilter, src: Mat, dst: Mat) {
    require(src.type() == CvType.CV_8UC4) { "The type of src must be CV_8UC4." }

    when (filter) {
        CameraFilter.None -> src.copyTo(dst)
        CameraFilter.Negative -> negate(src, dst)
        CameraFilter.Grayscale -> grayscale(src, dst)
        CameraFilter.Red -> redFilter(src, dst)
        CameraFilter.Green -> greenFilter(src, dst)
        CameraFilter.Blue -> blueFilter(src, dst)
    }
}