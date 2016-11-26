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

/**
 * 入力画像をネガポジ反転した結果を表示したり、それをjpegでストレージに保存するクラス
 */
final class NegativeCamera
{
    private static final String slash = "/";         /** ファイルパス区切り文字 */
    private static final String extension = ".jpg";  /** ファイル拡張子 */
    private static final String type = "image/jpeg"; /** ファイルのMIMEタイプ */
    private static final String dataKey = "_data";   /** コンテンツURIのデータを示すキー */

    private final int quality;              /** 画像の保存品質 */
    private final File file;                /** ファイルシステムへのアクセス */
    private final SimpleDateFormat format;  /** ファイル名に付加する日時のフォーマット */
    private final ContentResolver resolver; /** コンテンツを管理するオブジェクトへのアクセス */

    private Mat frame; /** ネガポジ反転した画像 */

    /**
     * ディレクトリが無い場合にディレクトリを作成する。
     * @param file ファイルシステムへのアクセス
     */
    private static void makeDirectory(final File file)
    {
        if(!file.exists())
        {
            file.mkdir();
        }
    }

    /**
     * Mat型データからBitmap画像を生成する。
     * @param  frame Bitmap画像の生成元となるデータ
     * @return 生成したBitmap画像
     */
    private static Bitmap takeBitmap(final Mat frame)
    {
        final Bitmap bitmap = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(frame, bitmap);

        return bitmap;
    }

    /**
     * Bitmap画像をjpegでストレージに保存する。
     * @param  path        保存先ファイルパス
     * @param  bitmap      保存するビットマップ画像
     * @param  quality     画像の保存品質 (0-100)
     * @throws IOException 指定されたパスに画像を保存できなかった場合
     */
    private static void savePicture(final String path, final Bitmap bitmap, final int quality) throws IOException
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

    /**
     * コンテンツ管理オブジェクトにインデックスを登録する。
     * @param resolver コンテンツ管理オブジェクト
     * @param name     登録するファイルの名前
     * @param path     登録するファイルパス
     */
    private static void saveIndex(final ContentResolver resolver, final String name, final String path)
    {
        final ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, type);
        values.put(MediaStore.Images.Media.TITLE, name);
        values.put(dataKey, path);

        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * コンストラクタ。
     * @param resolver  コンテンツ管理オブジェクト
     * @param quality   画像を保存するときの品質
     * @param directory 画像を保存するディレクトリ
     * @param format    保存した画像のファイル名に付加する日時のフォーマット (例: "yyyyMMdd" = 20161126)
     */
    NegativeCamera(final ContentResolver resolver, final int quality, final String directory, final String format)
    {
        this.quality = quality;
        file = new File(Environment.getExternalStorageDirectory().getPath() + directory);
        this.format = new SimpleDateFormat(format);
        this.resolver = resolver;
    }

    /**
     * オブジェクトの初期化処理をする。
     * @param width  利用する画像の幅
     * @param height 利用する画像の高さ
     */
    void Initialize(final int width, final int height)
    {
        Release();

        frame = new Mat(height, width, CvType.CV_8UC3);
    }

    /**
     * オブジェクトの解放処理をする。
     */
    void Release()
    {
        if(frame != null)
        {
            frame.release();
        }
    }

    /**
     * 画像をネガポジ反転して保持する。
     * @param frame 入力画像
     */
    void Input(final Mat frame)
    {
        Core.bitwise_not(frame, this.frame);
    }

    /**
     * 保持しているネガポジ反転画像を返す。
     * @return ネガポジ反転画像
     */
    Mat Output()
    {
        return frame;
    }

    /**
     * 保持している画像データをストレージに保存する。
     * @throws IOException 画像を保存できなかった場合
     */
    void Save() throws IOException
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
