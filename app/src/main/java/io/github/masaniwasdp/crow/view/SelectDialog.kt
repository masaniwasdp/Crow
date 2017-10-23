package io.github.masaniwasdp.crow.view

import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v7.app.AlertDialog.Builder

typealias OnSelect = (which: Int) -> Unit

class SelectDialog(private val resId: Int = 0, private val onSelect: OnSelect = {}) : DialogFragment() {
    init {
        require(resId > 0) { "The resId must be more than 0." }
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

fun FragmentManager.select(resId: Int, onSelect: OnSelect) {
    require(resId > 0) { "The resId must be more than 0." }

    SelectDialog(resId, onSelect).show(this, SELECT_TAG)
}

private const val SELECT_TAG = "Select"
