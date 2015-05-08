package com.zanelove.photoapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.zanelove.photoapp.utils.File_Tool;
import com.zanelove.photoapp.view.CircleProgressBar;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 上传图片
 */
public class UploadImage_Activity extends Activity {

    private int len = 0;
    private NotificationManager manager;
    private Notification notif;
    private ArrayList<String> id_list;
    private TextView title_info;
    private CircleProgressBar circleProgressBar;
    private MyPhotoApp_Data_Application myapp;
    private ArrayList<String> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image);
        myapp = (MyPhotoApp_Data_Application) getApplication();
        init_PutImg();
    }
    private void init_PutImg() {

        title_info = (TextView) findViewById(R.id.title_info);

        PendingIntent pIntent = PendingIntent.getActivity(UploadImage_Activity.this, 0, new Intent(), 0);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notif = new Notification();
        notif.icon = R.drawable.ic_launcher;
        notif.tickerText = "正在上传作答文件";
        notif.flags = Notification.FLAG_AUTO_CANCEL;

        // 通知栏显示所用到的布局文件
        notif.contentView = new RemoteViews(getPackageName(), R.layout.content_view);
        notif.contentIntent = pIntent;
        manager.notify(0, notif);
        manager.cancel(0);

        circleProgressBar = (CircleProgressBar) findViewById(R.id.circleProgressbar);
        circleProgressBar.setProgress(0);

        new UploadImageThread().start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    notif.contentView.setTextViewText(R.id.content_view_text1, len + " %");
                    notif.contentView.setProgressBar(R.id.content_view_progress, 100, len, false);
                    manager.notify(0, notif);
                    circleProgressBar.setProgress(len);
                    break;
                case 1:
                    title_info.setText("作答上传完成");
                    manager.cancel(0);
                    break;
                default:
                    break;
            }
        }
    };

    private class UploadImageThread extends Thread {
        private Timer timer = new Timer();

        @Override
        public void run() {
            super.run();
            id_list = getIntent().getStringArrayListExtra("id_list");
            lists = getIntent().getStringArrayListExtra("lists");
            int img_sum = getIntent().getIntExtra("img_sum", 1), num = 0;
            int d = 100 / img_sum;

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = len;
                    handler.sendMessage(msg);

                    if (len == 100) {
                        timer.cancel();
                        handler.sendEmptyMessage(1);
                    } else {
                        if (len < 95) {
                            len++;
                        }
                    }

                }
            }, 0, 1000);
            for (String img_path : lists) {
                int n = 0;
                for (String id : id_list) {
                    upload(img_path, "zanelove" + "_" + id + "_" + n);
                    n++;
                    num++;
                    len = num * d;
                }
            }

            len = 100;
        }
    }

    private void upload(String pathToOurFile, String name) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        //TODO
        String urlServer = myapp.getUrl_host() + "port&a=uploadPic&id=" + name + "&ueid=" + "*" + "&tcid=" + "*" + "&sid=" + myapp.getSessionid();
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {

            FileInputStream fileInputStream = new FileInputStream(new File(upload_ys_img(pathToOurFile)));

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs &amp; Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\"" + pathToOurFile + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			//取得Response内容
            InputStream is = connection.getInputStream();

            int ch;
            StringBuffer sbf = new StringBuffer();
            while ((ch = is.read()) != -1) {
                sbf.append((char) ch);
            }

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
            // Exception handling
        }
    }

    /**
     * 压缩用于上传的图片
     */

    private String upload_ys_img(String img_path) {

        String[] path_arr = img_path.split("/");
        String[] name_arr = path_arr[path_arr.length - 1].split("\\.");
        String path = File_Tool.SDPATH + "image/" + name_arr[0] + ".png";

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(img_path, options); // 此时返回bm为空
        options.inJustDecodeBounds = false;
        // 计算缩放比
        int be = (int) (options.outHeight / 1280);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be;
        // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
        bitmap = BitmapFactory.decodeFile(img_path, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        bitmap = compressImage(Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false));
        // 这样我们就可以读取较大的图片而不会内存溢出了。如果你想把压缩后的图片保存在Sdcard上的话就很简单了：
        File file = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100 && options > 30) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            options -= 10;// 每次都减少10
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
}
