package io.github.masaniwasdp.negativecamera

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.createBitmap
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import io.github.masaniwasdp.negativecamera.R.string.*
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader.OPENCV_VERSION_3_2_0
import org.opencv.android.OpenCVLoader.initAsync
import org.opencv.android.Utils.matToBitmap
import org.opencv.core.Core.bitwise_not
import org.opencv.core.CvType.CV_8UC3
import org.opencv.core.Mat

/**
 * Ĉefa activeco de apliko.
 *
 * @constructor Kreas activeco.
 */
class MainActivity : AppCompatActivity(), CvCameraViewListener2 {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        camera_view.setCvCameraViewListener(this)

        save_button.setOnClickListener { onButtonClick() }

        save_button.isEnabled = false
    }

    override fun onResume() {
        super.onResume()

        when (checkSelfPermission(this, CAMERA)) {
            PERMISSION_GRANTED -> initAsync(OPENCV_VERSION_3_2_0, this, loaderCallback)

            else -> alert(fragmentManager, camera_request) {
                requestPermissions(this, arrayOf(CAMERA), REQUEST_CAMERA)
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

    override fun onCameraViewStarted(width: Int, height: Int) {
        require(width > 0) { "The width must be more than 0." }
        require(height > 0) { "The height must be more than 0." }

        camera = Mat(height, width, CV_8UC3)
    }

    override fun onCameraViewStopped() {
        camera.release()
    }

    override fun onCameraFrame(frame: CvCameraViewFrame): Mat {
        when {
            negative_switch.isChecked -> bitwise_not(frame.rgba(), camera)

            else -> frame.rgba().copyTo(camera)
        }

        return camera
    }

    /** Konduto kiam la butono por savi bildojn estas puŝita. */
    private fun onButtonClick() {
        when (checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)) {
            PERMISSION_GRANTED -> save()

            else -> alert(fragmentManager, storage_request) {
                requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE)
            }
        }
    }

    /**
     * Avizas per toast.
     *
     * @param resId ID de teksto kiu estos montrita.
     */
    private fun notice(resId: Int) {
        makeText(this, getString(resId), LENGTH_SHORT).show()
    }

    /** Savas bildon de fotilo al stokado. */
    private fun save() {
        try {
            savePicture(camera.toBitmap(), DIRECTORY, contentResolver)

            notice(saving_success)
        } catch (e: Exception) {
            alert(fragmentManager, saving_failed, null)
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

    /** Bildo de fotilo. */
    private lateinit var camera: Mat
}

/** La ID por peti aŭtoritaton de fotilo. */
private const val REQUEST_CAMERA = 0

/** La ID por peti aŭtoritaton de stokado. */
private const val REQUEST_STORAGE = 1

/** La bildo dosierujo. */
private const val DIRECTORY = "/NegativeCamera/"

/**
 * Konvertas sin al bitmap-bildo.
 *
 * @receiver Mat-bildo kiu estos konvertita.
 * @return Konvertita bitmap-bildo.
 */
private fun Mat.toBitmap(): Bitmap {
    check(width() > 0) { "The width must be more than 0." }
    check(height() > 0) { "The height must be more than 0." }

    val bitmap = createBitmap(width(), height(), ARGB_8888)

    matToBitmap(this, bitmap)

    return bitmap
}
