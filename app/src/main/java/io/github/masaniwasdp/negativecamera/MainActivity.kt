package io.github.masaniwasdp.negativecamera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

/**
 * Ĉefa activeco de apliko.
 *
 * @constructor Kreas activeco.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        camera_view.setCvCameraViewListener(camera)

        save_button.setOnClickListener { save() }

        save_button.isEnabled = false
    }

    override fun onResume() {
        super.onResume()

        when (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, loaderCallback)
            }

            else -> alert(fragmentManager, R.string.camera_request) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CAMERA)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        camera_view.disableView()

        save_button.isEnabled = false
    }

    override fun onStop() {
        super.onStop()

        camera_view.disableView()

        save_button.isEnabled = false
    }

    /** Savas la fotilan bildon en la stokado kaj petas permeson kiel necese. */
    private fun save() {
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PackageManager.PERMISSION_GRANTED -> try {
                save(camera.getBitmap(), DIRECTORY, contentResolver)

                notify(this, R.string.saving_success)
            } catch (e: Exception) {
                notify(this, R.string.saving_failed)
            }

            else -> alert(fragmentManager, R.string.storage_request) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_STORAGE)
            }
        }
    }

    /** Callback funkcio kiu estos invokita kiam OpenCV estas ŝarĝita. */
    private val loaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    camera_view.enableView()

                    save_button.isEnabled = true
                }

                else -> super.onManagerConnected(status)
            }
        }
    }

    /** La fotilo de apliko. */
    private val camera = Camera()
}

/** La ID por peti aŭtoritaton de fotilo. */
private const val REQUEST_CAMERA = 0

/** La ID por peti aŭtoritaton de stokado. */
private const val REQUEST_STORAGE = 1

/** La bildo dosierujo. */
private const val DIRECTORY = "/NegativeCamera/"

/**
 * Montras tekston per toast.
 *
 * @param context Kunteksto de toast.
 * @param resId ID de teksto kiu estos montrita.
 */
private fun notify(context: Context, resId: Int) {
    Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
}
