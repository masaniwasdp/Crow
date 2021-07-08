package io.github.masaniwasdp.crow.presentation

import android.graphics.Bitmap

/** Interfaco de la fotila stokado. */
interface ICameraStorage {
    /**
     * Savas bitmap-bildon kiel jpeg-bildo en stokado kaj registras ĝin.
     *
     * @param bitmap Bitmap-bildo kiu estos savita.
     */
    fun save(bitmap: Bitmap)
}