package io.github.masaniwasdp.crow

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import io.github.masaniwasdp.crow.lib.Filter
import io.github.masaniwasdp.crow.present.request
import io.github.masaniwasdp.crow.present.select
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

    /** Ĉefa Modelo de apliko. */
    private val model = MainModel {
        Toast.makeText(this, getString(it), Toast.LENGTH_SHORT).show()
    }

    /** Callback funkcio kiu estos invokita kiam OpenCV estas ŝarĝita. */
    private val loaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) = when (status) {
            LoaderCallbackInterface.SUCCESS -> camera_view.enableView()

            else -> super.onManagerConnected(status)
        }
    }

    /** Aŭskultanto de fotila vido. */
    private val cameraViewListener = object : CameraBridgeViewBase.CvCameraViewListener2 {
        override fun onCameraViewStarted(width: Int, height: Int) {
            model.initialize(width, height)
        }

        override fun onCameraViewStopped() {
            model.release()
        }

        override fun onCameraFrame(frame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
            model.update(frame)

            return model.frame!!
        }
    }

    /** Aŭskultanto de konservi butono. */
    private val saveButtonListener = View.OnClickListener {
        request(R.string.storage, Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE) {
            model.save(contentResolver)
        }
    }

    /** Aŭskultanto de butono por elekti efektojn. */
    private val selectButtonListener = View.OnClickListener {
        fragmentManager.select(R.array.filters) {
            model.filter = Filter.values()[it]
        }
    }
}

/** La ID por peti permeson de fotilo. */
private const val CAMERA = 0

/** La ID por peti permeson de stokado. */
private const val STORAGE = 1
