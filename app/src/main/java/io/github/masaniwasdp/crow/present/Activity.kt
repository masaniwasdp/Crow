package io.github.masaniwasdp.crow.present

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Vidigas dialogon kaj petas permeson.
 *
 * @receiver Aktiveco uzata.
 * @param resId ID de teksto kiu estos montrita.
 * @param perm Permeso kiu estos petita.
 * @param permId ID de la permeso.
 * @param behavior Konduto kiam ricevis permeson.
 */
fun Activity.request(resId: Int, perm: String, permId: Int, behavior: () -> Unit) {
    when (ContextCompat.checkSelfPermission(this, perm)) {
        PackageManager.PERMISSION_GRANTED -> behavior()

        else -> fragmentManager.alert(resId) {
            ActivityCompat.requestPermissions(this, arrayOf(perm), permId)
        }
    }
}
