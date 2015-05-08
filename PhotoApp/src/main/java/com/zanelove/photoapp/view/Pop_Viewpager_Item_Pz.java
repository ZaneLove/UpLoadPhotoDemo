package com.zanelove.photoapp.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.zanelove.photoapp.R;
import com.zanelove.photoapp.utils.SelectPhotoUtil;

/**
 *
 * 设置获取图片还是照片
 * Created by zanelove on 15-5-5.
 *
 */
public class Pop_Viewpager_Item_Pz {

    private static final int CAMERA_WITH_DATA = 1001;
    private static final int PHOTO_PICKED_WITH_DATA = 1002;
    private Activity activity;
    private TextView viewpager_pop_pz_pz, viewpager_pop_pz_xc, viewpager_pop_pz_back;

    public Dialog mDialog;

    public Dialog getmDialog() {
        return mDialog;
    }

    public void setmDialog(Dialog mDialog) {
        this.mDialog = mDialog;
    }

    public Pop_Viewpager_Item_Pz(Activity context) {
        activity = context;
        mDialog = new Dialog(context, R.style.Setting_Question_Submit_Dialog_Style);

        mDialog.setContentView(R.layout.viewpager_item_pop_pz);
        Window window = mDialog.getWindow();
        WindowManager wm = context.getWindowManager();
        Display d = wm.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = (int) (d.getWidth() * 0.85);
        window.setAttributes(p);
        mDialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
        window.setWindowAnimations(android.R.anim.fade_in);

        viewpager_pop_pz_pz = (TextView) mDialog.findViewById(R.id.viewpager_pop_pz_pz);
        viewpager_pop_pz_pz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                doTakePhoto();
            }
        });
        viewpager_pop_pz_xc = (TextView) mDialog.findViewById(R.id.viewpager_pop_pz_xc);
        viewpager_pop_pz_xc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                doSelectImageFromLoacal();
            }
        });

        viewpager_pop_pz_back = (TextView) mDialog.findViewById(R.id.viewpager_pop_pz_back);
        viewpager_pop_pz_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dismiss();
            }
        });
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    /**
     * 拍照获取图片
     */
    private void doTakePhoto() {
        SelectPhotoUtil.takePhoto(activity);
        mDialog.dismiss();
    }

    /**
     * 从本地手机中选择图片
     */
    private void doSelectImageFromLoacal() {
        Intent localIntent = new Intent();
        localIntent.setType("image/*");
        localIntent.setAction(Intent.ACTION_GET_CONTENT);
        localIntent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent localIntent2 = Intent.createChooser(localIntent, "选择图片");
        activity.startActivityForResult(localIntent2, PHOTO_PICKED_WITH_DATA);

        mDialog.dismiss();
    }
}
