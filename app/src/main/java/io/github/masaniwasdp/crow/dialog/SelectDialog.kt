package io.github.masaniwasdp.crow.dialog

import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v7.app.AlertDialog

/**
 * Dialogo por elekti elementojn.
 *
 * @constructor Kreas dialogon.
 */
class SelectDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setItems(resId) { _, which -> onSelect(which) }
                .create()
    }

    override fun onPause() {
        super.onPause()

        dismiss()
    }

    /** ID de teksto kiu estos montrita. */
    var resId = 0

    /** Konduto kiam elemento estas elektita. */
    var onSelect: (which: Int) -> Unit = {}
}

/**
 * Vidigas dialogon por elekti elementojn.
 *
 * @receiver Fragmenta direktisto.
 * @param resId ID de listo havanta elementojn kiu estos elektita.
 * @param onSelect Konduto kiam elemento estas elektita.
 */
fun FragmentManager.select(resId: Int, onSelect: (which: Int) -> Unit) {
    val dialog = SelectDialog().apply {
        this.resId = resId
        this.onSelect = onSelect
    }

    dialog.show(this, SELECT_TAG)
}

/** La etikedo de dialogoj. */
private const val SELECT_TAG = "Select"
