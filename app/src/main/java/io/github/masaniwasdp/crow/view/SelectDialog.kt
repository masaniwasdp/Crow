package io.github.masaniwasdp.crow.view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * Dialogo por elekti elementojn.
 *
 * @constructor Kreas dialogon.
 * @property resId ID de listo havanta elementojn kiu estos elektita.
 * @property onSelect Konduto kiam elemento estas elektita. La argumento estas nombro de la elemento.
 */
class SelectDialog(private val resId: Int, private val onSelect: (Int) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setItems(resId) { _, which -> onSelect(which) }
            .create()
    }

    override fun onPause() {
        super.onPause()

        dismiss()
    }
}
