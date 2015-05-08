package com.zanelove.photoapp.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import com.zanelove.photoapp.MainActivity;

/**
 * Created by zanelove on 15-5-5.
 */
public class SelectPhotoUtil {
    /**
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1001;

    private static Uri photoUri;
    private static String picPath = null;

    /**
     * 拍照获取图片
     */
    public static void takePhoto(Activity activity) {

        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"

            ContentValues values = new ContentValues();
            photoUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);

            activity.startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            Toast.makeText(activity, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 选择图片后，获取图片的路径
     * @param activity
     */
    public static void doPhoto(Activity activity) {

        String[] pojo = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(photoUri, pojo, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);
            try {
                //4.0以上的版本会自动关闭 (4.0--14; ; 4.0.3--15)
                if (Integer.parseInt(Build.VERSION.SDK) < 14) {
                    cursor.close();
                }
            } catch (Exception e) {
            }
        }
        if (picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
            if(activity instanceof MainActivity) {
                ((MainActivity)activity).setImageViewByTask(picPath);
            }
        } else {
            Toast.makeText(activity, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
    }
}