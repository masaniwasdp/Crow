package com.masaniwa.negativecamera

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.createBitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.Toast.LENGTH_LONG
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
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        camera_view.setCvCameraViewListener(cameraListener)

        save_button.setOnClickListener { save() }
    }

    override fun onResume() {
        super.onResume()

        if (!initAsync(OPENCV_VERSION_3_2_0, this, loaderCallback)) notice(loading_failed)
    }

    override fun onPause() {
        camera_view.disableView()

        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        camera_view.disableView()
    }

    /**
     * トーストでテキストを表示する。
     *
     * @param id 表示するテキストのリソースID。
     */
    private fun notice(id: Int) {
        makeText(this, getString(id), LENGTH_LONG).show()
    }

    /** ネガポジ反転した画像をストレージに保存する。 */
    private fun save() {
        try {
            savePicture(takeBitmap(camera), directory, contentResolver)

            notice(saving_success)
        } catch (e: StorageException) {
            notice(saving_failed)
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

    /** カメラビューのイベントリスナ。 */
    private val cameraListener = object : CvCameraViewListener2 {
        override fun onCameraViewStarted(width: Int, height: Int) {
            camera = Mat(height, width, CV_8UC3)
        }

        override fun onCameraViewStopped() {
            camera.release()
        }

        override fun onCameraFrame(frame: CvCameraViewFrame): Mat {
            when (negative_switch.isChecked) {
                true -> bitwise_not(frame.rgba(), camera)

                else -> frame.rgba().copyTo(camera)
            }

            return camera
        }
    }

    /** カメラの画像。 */
    private lateinit var camera: Mat
}

/**
 * MatからBitmapを生成する。
 *
 * @param frame Bitmapの生成元となるMat。
 * @return 生成したBitmap。
 */
private fun takeBitmap(frame: Mat): Bitmap {
    val bitmap = createBitmap(frame.width(), frame.height(), ARGB_8888)

    matToBitmap(frame, bitmap)

    return bitmap
}

/** 画像を保存するディレクトリ。 */
private const val directory = "/NegativeCamera/"
