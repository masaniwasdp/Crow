package io.github.masaniwasdp.crow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.masaniwasdp.crow.application.Camera
import io.github.masaniwasdp.crow.infrastructure.MediaStore
import io.github.masaniwasdp.crow.view.CameraFragment

/** Ĉefa aktiveco de apliko. */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            CameraFragment().let {
                it.camera = Camera(it, MediaStore(contentResolver))

                supportFragmentManager.beginTransaction().let { x ->
                    x.add(R.id.container, it)
                    x.commit()
                }
            }
        }
    }

    companion object {
        init {
            System.loadLibrary(LIBNAME_OPENCV)
        }
    }
}

/** Biblioteka nomo de OpenCV. */
private const val LIBNAME_OPENCV = "opencv_java3"
