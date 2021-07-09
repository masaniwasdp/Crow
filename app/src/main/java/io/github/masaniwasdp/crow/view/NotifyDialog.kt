package io.github.masaniwasdp.crow.view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.github.masaniwasdp.crow.R

/**
 * Dialogo havanta unu butonon.
 *
 * @constructor Kreas dialogon.
 * @property resId ID de teksto kiu estos montrita.
 * @property onClick Konduto kiam la butono estas puŝita.
 */
class NotifyDialog(private val resId: Int, private val onClick: () -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        return AlertDialog.Builder(requireActivity())
                .setMessage(getString(resId))
                .setPositiveButton(R.string.ok) { _, _ -> onClick() }
                .create()
    }

    override fun onPause() {
        super.onPause()

        dismiss()
    }
}
