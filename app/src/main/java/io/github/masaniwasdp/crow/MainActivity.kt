package io.github.masaniwasdp.crow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import io.github.masaniwasdp.crow.dialog.alert
import io.github.masaniwasdp.crow.dialog.select
import kotlinx.android.synthetic.main.main_activity.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat

/**
 * Ĉefa aktiveco de apliko.
 *
 * @constructor Kreas aktiveco.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        camera_view.setCvCameraViewListener(cameraViewListener)

        save_button.setOnClickListener(saveButtonListener)

        select_button.setOnClickListener(selectButtonListener)
    }

    override fun onResume() {
        super.onResume()

        request(R.string.camera, Manifest.permission.CAMERA, CAMERA) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, loaderCallback)
        }
    }

    override fun onPause() {
        super.onPause()

        camera_view.disableView()
    }

    override fun onStop() {
        super.onStop()

        camera_view.disableView()
    }

    /**
     * Vidigas dialogon kaj petas permeson.
     *
     * @param resId ID de teksto kiu estos montrita.
     * @param permission Permeso kiu estos petita.
     * @param permissionId ID de la permeso.
     * @param behavior Konduto kiam ricevis permeson.
     */
    private fun request(resId: Int, permission: String, permissionId: Int, behavior: () -> Unit) {
        when (ContextCompat.checkSelfPermission(this, permission)) {
            PackageManager.PERMISSION_GRANTED -> behavior()

            else -> fragmentManager.alert(resId) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), permissionId)
            }
        }
    }

    /** Callback funkcio kiu estos invokita kiam OpenCV estas ŝarĝita. */
    private val loaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> camera_view.enableView()

                else -> super.onManagerConnected(status)
            }
        }
    }

    /** Aŭskultanto de fotila vido. */
    private val cameraViewListener = object : CameraBridgeViewBase.CvCameraViewListener2 {
        override fun onCameraViewStarted(width: Int, height: Int) {
            model.initializeFrame(width, height)
        }

        override fun onCameraViewStopped() {
            model.releaseFrame()
        }

        override fun onCameraFrame(frame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
            model.updateFrame(frame)

            return model.frame!!
        }
    }

    /** Aŭskultanto de konservi butono. */
    private val saveButtonListener = View.OnClickListener {
        request(R.string.storage, Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE) {
            model.saveFrame(contentResolver)
        }
    }

    /** Aŭskultanto de butono por elekti efektojn. */
    private val selectButtonListener = View.OnClickListener {
        fragmentManager.select(R.array.camera_types) { model.type = CameraType.values()[it] }
    }

    /** Ĉefa Modelo de apliko. */
    private val model = MainModel { Toast.makeText(this, getString(it), Toast.LENGTH_SHORT).show() }
}

/** La ID por peti permeson de fotilo. */
private const val CAMERA = 0

/** La ID por peti permeson de stokado. */
private const val STORAGE = 1
