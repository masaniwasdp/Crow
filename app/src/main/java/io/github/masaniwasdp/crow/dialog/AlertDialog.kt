package io.github.masaniwasdp.crow.dialog

import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v7.app.AlertDialog.Builder
import io.github.masaniwasdp.crow.R.string.ok

/**
 * Dialogo havanta unu butonon.
 *
 * @constructor Kreas dialogon.
 */
class AlertDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        return Builder(activity)
                .setMessage(getString(resId))
                .setPositiveButton(ok) { _, _ -> onClick() }
                .create()
    }

    override fun onPause() {
        super.onPause()

        dismiss()
    }

    /** ID de teksto kiu estos montrita. */
    var resId = 0

    /** Konduto kiam la butono estas puŝita. */
    var onClick: OnClick = {}
}

/**
 * Vidigas dialogon havantan unu butonon.
 *
 * @receiver Fragmenta direktisto.
 * @param resId ID de teksto kiu estos montrita.
 * @param onClick Konduto kiam la butono eatas puŝita.
 */
fun FragmentManager.alert(resId: Int, onClick: OnClick) {
    AlertDialog().apply {
        this.resId = resId
        this.onClick = onClick
    }.show(this, ALERT_TAG)
}

/** La konduto kiam la butono estas puŝita. */
private typealias OnClick = () -> Unit

/** La etikedo de dialogoj. */
private const val ALERT_TAG = "Alert"
