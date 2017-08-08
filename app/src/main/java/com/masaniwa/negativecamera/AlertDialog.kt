package com.masaniwa.negativecamera

import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.masaniwa.negativecamera.R.string.ok

/** ボタンを押したときの動作の型。 */
typealias OnClick = () -> Unit

/**
 * ボタンを一つ持つアラートダイアログ。
 *
 * @constructor ダイアログを生成する。
 */
class AlertDialog() : DialogFragment() {
    /**
     * 表示する文字列とボタンを押したときの動作を与えてダイアログを生成する。
     *
     * @param resId 表示する文字列のリソースID。
     * @param onClick ボタンを押したときの動作。
     */
    constructor(resId: Int, onClick: OnClick?) : this() {
        this.resId = resId
        this.onClick = onClick
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        return AlertDialog.Builder(activity)
                .setMessage(getString(resId))
                .setPositiveButton(ok) { _, _ -> onClick?.invoke() }
                .create()
    }

    override fun onPause() {
        super.onPause()

        dismiss()
    }

    /** 表示する文字列のリソースID。 */
    private var resId = 0

    /** ボタンを押したときの動作。 */
    private var onClick: OnClick? = null
}

/**
 * アラートダイアログを表示する。
 *
 * @param manager フラグメントマネージャ。
 * @param resId 表示する文字列のリソースID。
 * @param onClick ボタンを押したときの動作。
 */
fun alert(manager: FragmentManager, resId: Int, onClick: OnClick?) {
    AlertDialog(resId, onClick).show(manager, alertTag)
}

/** アラートダイアログのタグ。 */
private const val alertTag = "Alert"
