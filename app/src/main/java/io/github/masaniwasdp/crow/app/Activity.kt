package io.github.masaniwasdp.crow.app

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import io.github.masaniwasdp.crow.R
import io.github.masaniwasdp.crow.R.string.camera_request
import io.github.masaniwasdp.crow.R.string.storage_request
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader.OPENCV_VERSION_3_1_0
import org.opencv.android.OpenCVLoader.initAsync
import org.opencv.core.Mat

/**
 * Ĉefa activeco de apliko.
 *
 * @constructor Kreas activeco.
 */
class Activity : AppCompatActivity(), CvCameraViewListener2, OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        camera_view.setCvCameraViewListener(this)

        save_button.setOnClickListener(this)

        save_button.isEnabled = false
    }

    override fun onResume() {
        super.onResume()

        when (checkSelfPermission(this, CAMERA)) {
            PERMISSION_GRANTED -> initAsync(OPENCV_VERSION_3_1_0, this, loaderCallback)

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

    override fun onClick(view: View?) {
        if (view == save_button) {
            when (checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)) {
                PERMISSION_GRANTED -> model.saveFrame(contentResolver)

                else -> fragmentManager.alert(storage_request) {
                    requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE)
                }
            }
        }
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
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

    private val model = Model { makeText(this, getString(it), LENGTH_SHORT).show() }
}

/** La ID por peti permeson de fotilo. */
private const val REQUEST_CAMERA = 0

/** La ID por peti permeson de stokado. */
private const val REQUEST_STORAGE = 1
