package com.masaniwa.negativecamera;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.IOException;

import static org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import static org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

public final class MainActivity extends AppCompatActivity implements CvCameraViewListener2
{
    private static final int quality = 100;
    private static final String directory = "/NegativeCamera/";
    private static final String format = "yyyyMMdd.hhmmss";

    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(final int status)
        {
            switch(status)
            {
                case LoaderCallbackInterface.SUCCESS:
                    cameraView.enableView();
                    break;

                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private NegativeCamera negativeCamera;
    private CameraBridgeViewBase cameraView;

    private void save()
    {
        final StringBuffer text = new StringBuffer();

        try
        {
            negativeCamera.Save();
            text.append(getString(R.string.saving_success));
        }
        catch(IOException e)
        {
            text.append(getString(R.string.saving_failed));
        }
        finally
        {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        negativeCamera = new NegativeCamera(getContentResolver(), quality, directory, format);

        cameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        cameraView.setCvCameraViewListener(this);

        final FloatingActionButton saveButton = (FloatingActionButton) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                save();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, loaderCallback);
    }

    @Override
    public void onPause()
    {
        if(cameraView != null)
        {
            cameraView.disableView();
        }

        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(cameraView != null)
        {
            cameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(final int width, final int height)
    {
        negativeCamera.Initialize(width, height);
    }

    @Override
    public void onCameraViewStopped()
    {
        negativeCamera.Release();
    }

    @Override
    public Mat onCameraFrame(final CvCameraViewFrame frame)
    {
        negativeCamera.Input(frame.rgba());

        return negativeCamera.Output();
    }
}
