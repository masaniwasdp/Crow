package io.github.masaniwasdp.crow.view

import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v7.app.AlertDialog.Builder
import io.github.masaniwasdp.crow.R.string.ok

/** La konduto kiam la butono estas puŝita. */
typealias OnClick = () -> Unit

/**
 * Dialogo havanta unu butonon.
 *
 * @constructor Kreas dialogon.
 * @property resId ID de teksto kiu estos montrita.
 * @property onClick Konduto kiam la butono estas puŝita.
 */
class AlertDialog(private val resId: Int = 0, private val onClick: OnClick = {}) : DialogFragment() {
    init {
        require(resId > 0) { "The resId must be greater than 0." }
    }

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
}

/**
 * Vidigas dialogon havantan unu butonon.
 *
 * @receiver Fragmenta direktisto.
 * @param resId ID de teksto kiu estos montrita.
 * @param onClick Konduto kiam la butono eatas puŝita.
 */
fun FragmentManager.alert(resId: Int, onClick: OnClick) {
    require(resId > 0) { "The resId must be greater than 0." }

    AlertDialog(resId, onClick).show(this, ALERT_TAG)
}

/** La etikedo de dialogoj. */
private const val ALERT_TAG = "Alert"
