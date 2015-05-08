package com.zanelove.photoapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.zanelove.photoapp.utils.FileUtil;
import com.zanelove.photoapp.utils.File_Tool;
import com.zanelove.photoapp.utils.HttpUtil;
import com.zanelove.photoapp.utils.SelectPhotoUtil;
import com.zanelove.photoapp.view.Pop_Viewpager_Item_Pz;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.ConnectException;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {
    private int is_img = 0;
    private String oncline_image_tag;
    private Pop_Viewpager_Item_Pz isn;
    private LinearLayout viewpager_item_zp_ll;
    private Toast toast;
    private ArrayList<String> lists = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File_Tool.createDIRFrom("");
        File_Tool.createDIRFrom("photo");
        File_Tool.createDIRFrom("photo/image");

        toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        init();
    }

    private void init() {
        viewpager_item_zp_ll = (LinearLayout) this.findViewById(R.id.viewpager_item_zp_ll);

        ImageView viewpager_item_xj = (ImageView) this.findViewById(R.id.viewpager_item_xj);
        viewpager_item_xj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            if (viewpager_item_zp_ll.getVisibility() == View.VISIBLE) {
                viewpager_item_zp_ll.setVisibility(View.GONE);
            } else {
                viewpager_item_zp_ll.setVisibility(View.VISIBLE);
            }
            }
        });

        ImageView pz1 = (ImageView) this.findViewById(R.id.viewpager_item_zp_1);
        pz1.setTag("pz1");
        pz1.setOnClickListener(this);

        ImageView pz2 = (ImageView) this.findViewById(R.id.viewpager_item_zp_2);
        pz2.setTag("pz2");
        pz2.setOnClickListener(this);

        ImageView pz3 = (ImageView) this.findViewById(R.id.viewpager_item_zp_3);
        pz3.setTag("pz3");
        pz3.setOnClickListener(this);

        ImageView pz4 = (ImageView) this.findViewById(R.id.viewpager_item_zp_4);
        pz4.setTag("pz4");
        pz4.setOnClickListener(this);

        TextView next_tv = (TextView) this.findViewById(R.id.viewpager_item_next);
        next_tv.setText("提交");
        next_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Put_UserAnser_Task().execute();
            }
        });

        isn = new Pop_Viewpager_Item_Pz(this);
    }

    private static final int CAMERA_WITH_DATA = 1001;
    private static final int PHOTO_PICKED_WITH_DATA = 1002;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA: // 从本地选择图片
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    this.setImageViewByTask(FileUtil.getPath(this, data.getData()));
                }
                break;

            case CAMERA_WITH_DATA: // 拍照
                SelectPhotoUtil.doPhoto(this);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * 根据图片的绝对地址. 给指定Tag标签的图片赋值.
     *
     * @param img_path
     */
    public void setImageViewByTask(String img_path) {
        ImageView iv = (ImageView) viewpager_item_zp_ll.findViewWithTag(oncline_image_tag);

        String ys_path = ys_img(img_path);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(ys_path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        iv.setImageBitmap(bm);

        /**
         * 设置下一个增加图片的按钮.
         */
        int i = Integer.parseInt(oncline_image_tag.substring(oncline_image_tag.length() - 1, oncline_image_tag.length()));
        String tags = oncline_image_tag.substring(0, oncline_image_tag.length() - 1);
        ImageView next_iv = (ImageView) viewpager_item_zp_ll.findViewWithTag(tags + (i + 1));
        if (i != 4) {
            if (i == 3) {
                next_iv = (ImageView) viewpager_item_zp_ll.findViewWithTag(tags + "4");
            } else {
                next_iv = (ImageView) viewpager_item_zp_ll.findViewWithTag(tags + (i + 1));
            }
            is_img++;
            next_iv.setVisibility(View.VISIBLE);
        }
        lists.add(img_path);
    }

    /**
     * 压缩图片.
     */
    private String ys_img(String img_path) {

        String[] path_arr = img_path.split("/");
        String[] name_arr = path_arr[path_arr.length - 1].split("\\.");
        String path = File_Tool.SDPATH + "image/" + name_arr[0] + ".png";

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(img_path, options); // 此时返回bm为空
        options.inJustDecodeBounds = false;
        // 计算缩放比
        int be = (int) (options.outHeight / 60);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be;
        // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
        bitmap = BitmapFactory.decodeFile(img_path, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(bitmap);
        //bitmap = compressImage(Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false));
        // 这样我们就可以读取较大的图片而不会内存溢出了。如果你想把压缩后的图片保存在Sdcard上的话就很简单了：
        File file = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 50, out)) {
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

    /**
     * 提交用户文字答案接口数据.
     *
     * @author wanglei
     */
    class Put_UserAnser_Task extends AsyncTask<String, Integer, String> {
        ArrayList<String> id_list = new ArrayList<String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            MyPhotoApp_Data_Application myphoto = (MyPhotoApp_Data_Application) getApplication();
            //TODO
            String url = myphoto.getUrl_host() + "port&a=userPaperAnswer" + "&sid=" + myphoto.getSessionid(), resurt = "";
            ArrayList<NameValuePair> val_list = new ArrayList<NameValuePair>();
            try {
                val_list.add(new BasicNameValuePair("is_img", is_img > 0 ? "1" : "0")); // $is_img=1有照片,
                resurt = HttpUtil.getData(MainActivity.this, url, val_list, HttpUtil.METHOD_POST);
            } catch (ConnectException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resurt;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if ("1".equals(result)) {
                if (is_img > 0) {
                    int img_sum = 0;
                    Intent intent = new Intent(MainActivity.this, UploadImage_Activity.class);

                    for (int i=0;i<4;i++) {
                        img_sum++;
                        id_list.add(""+(i+1));
                    }
                    intent.putExtra("img_sum", img_sum);
                    intent.putStringArrayListExtra("id_list", id_list);
                    intent.putStringArrayListExtra("lists", lists);

                    MainActivity.this.startActivityForResult(intent, 101);
                    MainActivity.this.finish();
                } else {
                    toast.setText("提交成功");
                    toast.show();
                }
            } else {
                toast.setText("提交失败");
                toast.show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        oncline_image_tag = v.getTag().toString();
        isn.show();
    }
}