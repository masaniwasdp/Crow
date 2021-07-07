package io.github.masaniwasdp.crow.present

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import io.github.masaniwasdp.crow.R

/**
 * Dialogo havanta unu butonon.
 *
 * @constructor Kreas dialogon.
 */
class AlertDialogEx : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        return AlertDialog.Builder(activity!!)
                .setMessage(getString(resId))
                .setPositiveButton(R.string.ok) { _, _ -> onClick() }
                .create()
    }

    override fun onPause() {
        super.onPause()

        dismiss()
    }

    /** ID de teksto kiu estos montrita. */
    var resId = 0

    /** Konduto kiam la butono estas puŝita. */
    var onClick: () -> Unit = {}
}

/**
 * Vidigas dialogon havantan unu butonon.
 *
 * @receiver Fragmenta direktisto.
 * @param resId ID de teksto kiu estos montrita.
 * @param onClick Konduto kiam la butono eatas puŝita.
 */
fun FragmentManager.alertEx(resId: Int, onClick: () -> Unit) {
    AlertDialogEx()
            .apply {
                this.resId = resId
                this.onClick = onClick
            }
            .show(this, ALERT_TAG)
}

/** La etikedo de dialogoj. */
private const val ALERT_TAG = "Alert"
