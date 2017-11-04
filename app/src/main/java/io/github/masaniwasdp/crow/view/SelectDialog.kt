package io.github.masaniwasdp.crow.view

import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v7.app.AlertDialog.Builder

/**
 * Dialogo por elekti elementojn.
 *
 * @constructor Kreas dialogon.
 * @property resId ID de listo havanta elementojn kiu estos elektita.
 * @property onSelect Konduto kiam elemento estas elektita.
 */
class SelectDialog(private val resId: Int = 0, private val onSelect: OnSelect = {}) : DialogFragment() {
    init {
        require(resId > 0) { "The resId must be greater than 0." }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Builder(activity)
                .setItems(resId) { _, which -> onSelect(which) }
                .create()
    }

    override fun onPause() {
        super.onPause()

        dismiss()
    }
}

/**
 * Vidigas dialogon por elekti elementojn.
 *
 * @receiver Fragmenta direktisto.
 * @param resId ID de listo havanta elementojn kiu estos elektita.
 * @param onSelect Konduto kiam elemento estas elektita.
 */
fun FragmentManager.select(resId: Int, onSelect: OnSelect) {
    require(resId > 0) { "The resId must be greater than 0." }

    SelectDialog(resId, onSelect).show(this, SELECT_TAG)
}

/** La konduto kiam elemento estas elektita. */
private typealias OnSelect = (which: Int) -> Unit

/** La etikedo de dialogoj. */
private const val SELECT_TAG = "Select"
