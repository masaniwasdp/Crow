package io.github.masaniwasdp.crow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.masaniwasdp.crow.application.FilterCamera
import io.github.masaniwasdp.crow.infrastructure.MediaStore
import io.github.masaniwasdp.crow.view.CameraFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            CameraFragment().let {
                it.filterCamera = FilterCamera(it, MediaStore(contentResolver))

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

private const val LIBNAME_OPENCV = "opencv_java3"
