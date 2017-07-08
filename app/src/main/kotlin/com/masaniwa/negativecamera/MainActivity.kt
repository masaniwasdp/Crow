package com.masaniwa.negativecamera

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader.OPENCV_VERSION_3_2_0
import org.opencv.android.OpenCVLoader.initAsync
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.IOException

/** アプリケーションのメインとなるアクティビティ。 */
class MainActivity : AppCompatActivity(), CvCameraViewListener2 {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        camera_view.setCvCameraViewListener(this)

        save_button.setOnClickListener { save() }
    }

    override fun onResume() {
        super.onResume()

        if (!initAsync(OPENCV_VERSION_3_2_0, this, loaderCallback)) {
            Toast.makeText(this, getString(R.string.loading_failed), Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        camera_view.disableView()

        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        camera_view.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        camera = Mat(height, width, CvType.CV_8UC3)
    }

    override fun onCameraViewStopped() {
        camera.release()
    }

    override fun onCameraFrame(frame: CvCameraViewFrame): Mat {
        Core.bitwise_not(frame.rgba(), camera)

        return camera
    }

    /**
     * ネガポジ反転した画像をストレージに保存する。
     */
    private fun save() {
        try {
            save(takeBitmap(camera), directory, contentResolver)

            Toast.makeText(this, getString(R.string.saving_success), Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(this, getString(R.string.saving_failed), Toast.LENGTH_LONG).show()
        }
    }

    /** OpenCVのライブラリが読み込まれたときのコールバック関数。 */
    private val loaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> camera_view.enableView()
                else -> super.onManagerConnected(status)
            }
        }
    }

    /** カメラの画像。 */
    private lateinit var camera: Mat
}

/**
 * MatからBitmapを生成する。
 * @param  frame Bitmapの生成元となるMat。
 * @return 生成したBitmap。
 */
private fun takeBitmap(frame: Mat): Bitmap {
    val bitmap = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.ARGB_8888)

    Utils.matToBitmap(frame, bitmap)

    return bitmap
}

/** 画像を保存するディレクトリ。 */
private val directory = "/NegativeCamera/"
