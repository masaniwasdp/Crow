package io.github.masaniwasdp.crow.contract

import android.graphics.Bitmap

/** Interfaco de la stokado de amaskomunikilaroj. */
interface IMediaStore {
    /**
     * Savas bitmap-bildon en stokado kaj registras ĝin.
     *
     * @param bitmap Bitmap-bildo kiu estos savita.
     */
    fun saveImage(bitmap: Bitmap)
}