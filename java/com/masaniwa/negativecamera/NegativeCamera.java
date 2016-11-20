package com.masaniwa.negativecamera;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NegativeCamera
{
    private static final String slash = "/";
    private static final String extension = ".jpg";
    private static final String type = "image/jpeg";
    private static final String dataKey = "_data";

    private final int quality;
    private final File file;
    private final SimpleDateFormat format;
    private final ContentResolver resolver;
    private final Mat frame;

    protected static void makeDirectory(final File file)
    {
        if(!file.exists())
        {
            file.mkdir();
        }
    }

    protected static Bitmap takeBitmap(final Mat frame)
    {
        final Bitmap bitmap = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(frame, bitmap);

        return bitmap;
    }

    protected static void savePicture(final String path, final Bitmap bitmap, final int quality) throws IOException
    {
        try
        {
            final FileOutputStream stream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            stream.flush();
            stream.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    protected static void saveIndex(final ContentResolver resolver, final String name, final String path)
    {
        final ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.MIME_TYPE, type);
        values.put(MediaStore.Images.Media.TITLE, name);
        values.put(dataKey, path);

        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public NegativeCamera(final ContentResolver resolver, final int width, final int height, final int quality, final String directory, final String format)
    {
        this.quality = quality;
        file = new File(Environment.getExternalStorageDirectory().getPath() + directory);
        this.format = new SimpleDateFormat(format);
        this.resolver = resolver;
        frame = new Mat(height, width, CvType.CV_8UC3);
    }

    public void Release()
    {
        frame.release();
    }

    public void Input(final Mat frame)
    {
        Core.bitwise_not(frame, this.frame);
    }

    public Mat Output()
    {
        return frame;
    }

    public void Save() throws IOException
    {
        makeDirectory(file);

        final String name = format.format(new Date()) + extension;
        final String path = file.getAbsolutePath() + slash + name;

        try
        {
            savePicture(path, takeBitmap(frame), quality);
            saveIndex(resolver, name, path);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}
