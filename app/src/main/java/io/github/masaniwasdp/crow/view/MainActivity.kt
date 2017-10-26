package io.github.masaniwasdp.crow.view

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AppCompatActivity
import android.view.View.OnClickListener
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import io.github.masaniwasdp.crow.R
import io.github.masaniwasdp.crow.R.array.camera_types
import io.github.masaniwasdp.crow.R.string.camera_request
import io.github.masaniwasdp.crow.R.string.storage_request
import io.github.masaniwasdp.crow.model.CameraType.values
import io.github.masaniwasdp.crow.model.MainModel
import kotlinx.android.synthetic.main.main_activity.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader.OPENCV_VERSION_3_3_0
import org.opencv.android.OpenCVLoader.initAsync
import org.opencv.core.Mat

/**
 * Ĉefa aktiveco de apliko.
 *
 * @constructor Kreas aktiveco.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(FLAG_FULLSCREEN)

        setContentView(R.layout.main_activity)

        camera_view.setCvCameraViewListener(cameraViewListener)

        save_button.setOnClickListener(saveButtonListener)

        save_button.isEnabled = false

        select_button.setOnClickListener(selectButtonListener)
    }

    override fun onResume() {
        super.onResume()

        when (checkSelfPermission(this, CAMERA)) {
            PERMISSION_GRANTED -> initAsync(OPENCV_VERSION_3_3_0, this, loaderCallback)

            else -> fragmentManager.alert(camera_request) {
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

    /** Aŭskultanto de fotila vido. */
    private val cameraViewListener = object : CvCameraViewListener2 {
        override fun onCameraViewStarted(width: Int, height: Int) {
            assert(width > 0)
            assert(height > 0)

            model.initializeFrame(width, height)
        }

        override fun onCameraViewStopped() {
            model.releaseFrame()
        }

        override fun onCameraFrame(frame: CvCameraViewFrame?): Mat {
            assert(frame is CvCameraViewFrame)

            model.updateFrame(frame!!)

            assert(model.frame is Mat)

            return model.frame!!
        }
    }

    /** Aŭskultanto de konservi butono. */
    private val saveButtonListener = OnClickListener {
        when (checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)) {
            PERMISSION_GRANTED -> model.saveFrame(contentResolver)

            else -> fragmentManager.alert(storage_request) {
                requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE)
            }
        }
    }

    /** Aŭskultanto de butono por elekti efektojn. */
    private val selectButtonListener = OnClickListener {
        fragmentManager.select(camera_types) { model.type = values()[it] }
    }

    /** Ĉefa Modelo de apliko. */
    private val model = MainModel { makeText(this, getString(it), LENGTH_SHORT).show() }
}

/** La ID por peti permeson de fotilo. */
private const val REQUEST_CAMERA = 0

/** La ID por peti permeson de stokado. */
private const val REQUEST_STORAGE = 1
