package io.github.masaniwasdp.crow.view

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.github.masaniwasdp.crow.R
import io.github.masaniwasdp.crow.contract.ICameraFragment
import io.github.masaniwasdp.crow.contract.ICameraPresenter
import io.github.masaniwasdp.crow.databinding.FragmentCameraBinding
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.core.Mat

class CameraFragment : Fragment(), ICameraFragment {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentCameraBinding
            .inflate(inflater, container, false)
            .apply {
                cameraView.setCvCameraViewListener(cameraViewListener)
                cameraView.setOnClickListener(cameraViewListener)
                selectButton.setOnClickListener(selectButtonListener)
            }

        loaderCallback = object : BaseLoaderCallback(requireActivity()) {
            override fun onManagerConnected(status: Int) = when (status) {
                LoaderCallbackInterface.SUCCESS -> binding.cameraView.enableView()

                else -> super.onManagerConnected(status)
            }
        }

        this.binding = binding

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
    }

    override fun onResume() {
        super.onResume()

        PermissionWrapper(requireActivity(), R.string.camera, Manifest.permission.CAMERA)
            .request {
                loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
            }
    }

    override fun onPause() {
        super.onPause()

        binding?.cameraView?.disableView()
    }

    override fun onStop() {
        super.onStop()

        binding?.cameraView?.disableView()
    }

    override fun notifySuccess() {
        Toast.makeText(requireActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show()
    }

    override fun notifyFailed() {
        Toast.makeText(requireActivity(), getString(R.string.failed), Toast.LENGTH_SHORT).show()
    }

    var presenter: ICameraPresenter? = null

    private var binding: FragmentCameraBinding? = null

    private lateinit var loaderCallback: BaseLoaderCallback

    private val cameraViewListener = object
        : CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener {
        override fun onCameraViewStarted(width: Int, height: Int) {
            presenter?.initialize(width, height)
        }

        override fun onCameraViewStopped() {
            presenter?.finalise()
        }

        override fun onCameraFrame(frame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
            presenter?.apply { return processFrame(frame) }

            return frame.rgba()
        }

        override fun onClick(v: View?) {
            presenter?.saveFrame()
        }
    }

    private val selectButtonListener = View.OnClickListener {
        SelectDialog(R.array.filters) {
            presenter?.modeChange(ICameraPresenter.Mode.values()[it])
        }.show(requireActivity().supportFragmentManager, TAG_SELECT_FILTER)
    }
}

private const val TAG_SELECT_FILTER = "SelectFilter"