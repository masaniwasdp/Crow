package io.github.masaniwasdp.crow

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.masaniwasdp.crow.infrastructure.ExternalStorage
import io.github.masaniwasdp.crow.application.Camera
import io.github.masaniwasdp.crow.application.CameraFilter
import io.github.masaniwasdp.crow.contract.ICameraView
import io.github.masaniwasdp.crow.view.PermissionWrapper
import io.github.masaniwasdp.crow.view.SelectDialog
import kotlinx.android.synthetic.main.main_activity.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.core.Mat

/**
 * Ĉefa aktiveco de apliko.
 *
 * @constructor Kreas aktiveco.
 */
class MainActivity : AppCompatActivity(), ICameraView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        camera_view.setCvCameraViewListener(cameraViewListener)
        save_button.setOnClickListener(saveButtonListener)
        select_button.setOnClickListener(selectButtonListener)

        camera = Camera(this, ExternalStorage(contentResolver))
    }

    override fun onResume() {
        super.onResume()

        PermissionWrapper(this, R.string.camera, Manifest.permission.CAMERA)
            .request { loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS) }
    }

    override fun onPause() {
        super.onPause()

        camera_view.disableView()
    }

    override fun onStop() {
        super.onStop()

        camera_view.disableView()
    }

    override fun notifyMessage(resId: Int) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show()
    }

    companion object {
        init {
            System.loadLibrary(LIBNAME_OPENCV)
        }
    }

    private var camera: Camera? = null

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
            camera?.initialize(width, height)
        }

        override fun onCameraViewStopped() {
            camera?.release()
        }

        override fun onCameraFrame(frame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
            camera?.update(frame)

            return camera?.frame!!
        }
    }

    /** Aŭskultanto de konservi butono. */
    private val saveButtonListener = View.OnClickListener {
        PermissionWrapper(this, R.string.storage, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { camera?.save() }
    }

    /** Aŭskultanto de butono por elekti efektojn. */
    private val selectButtonListener = View.OnClickListener {
        SelectDialog(R.array.filters) { camera?.filter = CameraFilter.values()[it] }
            .show(supportFragmentManager, TAG_SELECT_FILTER)
    }
}

private const val LIBNAME_OPENCV = "opencv_java3"

private const val TAG_SELECT_FILTER = "SelectFilter"
