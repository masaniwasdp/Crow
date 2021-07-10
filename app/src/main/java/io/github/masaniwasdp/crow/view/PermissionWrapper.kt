package io.github.masaniwasdp.crow.view

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Envolva objekto de permeso.
 *
 * @constructor Kreas envolvan objekton.
 * @property resId ID de teksto kiu estos montrita.
 * @property permission Permeso kiu estos petita.
 */
class PermissionWrapper(
    private val activity: FragmentActivity,
    private val resId: Int,
    private val permission: String
) {
    /**
     * Vidigas dialogon kaj petas permeson.
     *
     * @param onGranted Konduto kiam ricevis permeson.
     */
    fun request(onGranted: () -> Unit) {
        when (ContextCompat.checkSelfPermission(activity, permission)) {
            PackageManager.PERMISSION_GRANTED -> onGranted()

            else -> NotifyDialog(resId) {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), 0)
            }.show(activity.supportFragmentManager, TAG_REQUEST_PERMISSION)
        }
    }
}

private const val TAG_REQUEST_PERMISSION = "RequestPermission"