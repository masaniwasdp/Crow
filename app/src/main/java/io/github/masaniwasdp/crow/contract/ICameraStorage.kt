package io.github.masaniwasdp.crow.contract

import android.graphics.Bitmap
import java.io.IOException

/** Interfaco de la fotila stokado. */
interface ICameraStorage {
    /**
     * Savas bitmap-bildon kiel jpeg-bildo en stokado kaj registras ĝin.
     *
     * @param bitmap Bitmap-bildo kiu estos savita.
     * @throws IOException Kiam malsukcesis savi la bildon.
     */
    fun save(bitmap: Bitmap)
}