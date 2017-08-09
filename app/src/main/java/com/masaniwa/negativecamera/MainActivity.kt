package com.masaniwa.negativecamera

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
import com.masaniwa.negativecamera.R.string.*
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
 * アプリケーションのメインとなるアクティビティ。
 *
 * @constructor アクティビティを作成する。
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
                requestPermissions(this, arrayOf(CAMERA), cameraRequest)
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
        require(width > 0)
        require(height > 0)

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

    /** 保存ボタンを押したときの動作。 */
    private fun onButtonClick() {
        when (checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)) {
            PERMISSION_GRANTED -> save()

            else -> alert(fragmentManager, storage_request) {
                requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), storageRequest)
            }
        }
    }

    /**
     * トーストで通知する。
     *
     * @param resId 表示する文字列のリソースID。
     */
    private fun notice(resId: Int) {
        makeText(this, getString(resId), LENGTH_SHORT).show()
    }

    /** カメラの画像をストレージに保存する。 */
    private fun save() {
        try {
            savePicture(camera.toBitmap(), directory, contentResolver)

            notice(saving_success)
        } catch (e: StorageException) {
            alert(fragmentManager, saving_failed, null)
        }
    }

    /** OpenCVのライブラリが読み込まれたときのコールバック関数。 */
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

    /** カメラの画像。 */
    private lateinit var camera: Mat
}

/**
 * 自身をBitmapに変換する。
 *
 * @receiver 変換元のMat。
 * @return 変換したBitmap。
 */
private fun Mat.toBitmap(): Bitmap {
    check(width() > 0)
    check(height() > 0)

    val bitmap = createBitmap(width(), height(), ARGB_8888)

    matToBitmap(this, bitmap)

    return bitmap
}

/** カメラの権限をリクエストするID。 */
private const val cameraRequest = 0

/** ストレージの権限をリクエストするID。 */
private const val storageRequest = 1

/** 画像を保存するディレクトリ。 */
private const val directory = "/NegativeCamera/"
