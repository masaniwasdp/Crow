package com.masaniwa.negativecamera

import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.masaniwa.negativecamera.R.string.ok

/** Konduto kiam la butono estas puŝita. */
typealias OnClick = () -> Unit

/**
 * Dialogo havanta unu butonon.
 *
 * @constructor Kreas dialogon.
 */
class AlertDialog() : DialogFragment() {
    /**
     * Kreas dialogon kun teksto kiu estos montrita kaj konduto kiam la butono estas puŝita.
     *
     * @param resId ID de teksto kiu estos montrita.
     * @param onClick Konduto kiam la butono estas puŝita.
     */
    constructor(resId: Int, onClick: OnClick?) : this() {
        this.resId = resId
        this.onClick = onClick
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        return AlertDialog
                .Builder(activity)
                .setMessage(getString(resId))
                .setPositiveButton(ok) { _, _ -> onClick?.invoke() }
                .create()
    }

    override fun onPause() {
        super.onPause()

        dismiss()
    }

    /** ID de teksto kiu estos montrita. */
    var resId = 0

    /** Konduto kiam la butono estas puŝita. */
    var onClick: OnClick? = null
}

/**
 * Vidigas dialogon havantan unu butonon.
 *
 * @param manager Fragmenta direktisto.
 * @param resId ID de teksto kiu estos montrita.
 * @param onClick Konduto kiam la butono eatas puŝita.
 */
fun alert(manager: FragmentManager, resId: Int, onClick: OnClick?) {
    AlertDialog(resId, onClick).show(manager, alertTag)
}

/** La etikedo de dialogoj. */
private const val alertTag = "Alert"
