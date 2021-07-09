package io.github.masaniwasdp.crow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.masaniwasdp.crow.infrastructure.MediaStore
import io.github.masaniwasdp.crow.presentation.CameraPresenter
import io.github.masaniwasdp.crow.view.CameraFragment

/** Ĉefa aktiveco de apliko. */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            val fragment = CameraFragment()

            fragment.presenter = CameraPresenter(fragment, MediaStore(contentResolver))

            supportFragmentManager
                .beginTransaction()
                .apply {
                    add(R.id.container, fragment)
                    commit()
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
