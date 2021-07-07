package io.github.masaniwasdp.crow.present

import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog

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

    /** Konduto kiam elemento estas elektita. La argumento estas nombro de la elemento. */
    var onSelect: (Int) -> Unit = {}
}

/**
 * Vidigas dialogon por elekti elementojn.
 *
 * @receiver Fragmenta direktisto.
 * @param resId ID de listo havanta elementojn kiu estos elektita.
 * @param onSelect Konduto kiam elemento estas elektita. La argumento estas nombro de la elemento.
 */
fun FragmentManager.select(resId: Int, onSelect: (Int) -> Unit) {
    SelectDialog()
            .apply {
                this.resId = resId
                this.onSelect = onSelect
            }
            .show(this, SELECT_TAG)
}

/** La etikedo de dialogoj. */
private const val SELECT_TAG = "Select"
