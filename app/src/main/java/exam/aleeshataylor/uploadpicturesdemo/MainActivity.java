package exam.aleeshataylor.uploadpicturesdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_BIG_IMAGE_CUTTING = 3;
    private static final String IMAGE_FILE_NAME = "picture.jpg";
    /**
     * 添加淘宝号图片的按钮
     */
    @BindView(R.id.ll_add_tao_bao_num)
    public LinearLayout llAddTaoBaoNum;
    /**
     *  添加搜索图片的按钮
     */
    @BindView(R.id.ll_add_search)
    public LinearLayout llAddSearch;
    /**
     *  浏览详情的添加按钮
     */
    @BindView(R.id.ll_add_detail)
    public LinearLayout llAddDetail;
    /**
     *  浏览详情的查看样图按钮
     */
    @BindView(R.id.btn_add_detail)
    public LinearLayout btnAddDetail;
    /**
     *  浏览详情的添加图标
     */
    @BindView(R.id.iv_add_detail)
    public ImageView ivAddDetail;
    /**
     *  浏览详情下面的部分
     */
    @BindView(R.id.tv_add_detail)
    public TextView tvAddDetail;
    /**
     *  第一步截图上传完毕开始倒计时
     */
    @BindView(R.id.tv_time)
    public TextView tvTime;
    /**
     *  任务过期时间
     */
    @BindView(R.id.tv_timing)
    public TextView tvTiming;
    /**
     *  提交任务
     */
    @BindView(R.id.ll_submit)
    public LinearLayout llSubmit;
    /**
     *  放弃任务
     */
    @BindView(R.id.btn_abandon)
    public Button btnAbandon;

    private PhotoPopupWindow mPhotoPopupWindow;

    @OnClick({R.id.ll_add_tao_bao_num, R.id.ll_add_search, R.id.ll_add_detail, R.id.btn_add_detail, R.id.iv_add_detail, R.id.tv_add_detail, R.id.tv_time, R.id.tv_timing, R.id.ll_submit, R.id.btn_abandon})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.ll_add_tao_bao_num:
                showTypeDialog();
                mPhotoPopupWindow = new PhotoPopupWindow(MainActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 进入相册选择
                        mPhotoPopupWindow.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        // 判断系统中是否有处理该 Intent 的 Activity
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, REQUEST_IMAGE_GET);
                        } else {
                            Toast.makeText(MainActivity.this, "未找到图片查看器", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 拍照
                        mPhotoPopupWindow.dismiss();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                });
                View rootView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
                mPhotoPopupWindow.showAtLocation(rootView,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.ll_add_search:
                showTypeDialog();
                break;
            case R.id.ll_add_detail:
                showTypeDialog();
                break;
            case R.id.btn_add_detail:
                break;
            case R.id.iv_add_detail:
                break;
            case R.id.tv_add_detail:
                break;
            case R.id.tv_time:
                break;
            case R.id.tv_timing:
                break;
            case R.id.ll_submit:
                break;
            case R.id.btn_abandon:
                Intent intent = new Intent(this,MainActivity.class);
                Toast.makeText(this,"放弃任务",Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
        }
    }

    private void showTypeDialog() {
    }

    /**
     * 处理回调结果
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 回调成功
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 小图切割
                case REQUEST_SMALL_IMAGE_CUTTING:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;

                // 相册选取
                case REQUEST_IMAGE_GET:
                    try {
                        startSmallPhotoZoom(data.getData());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;

                // 拍照
                case REQUEST_IMAGE_CAPTURE:
                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                    startSmallPhotoZoom(Uri.fromFile(temp));
                    break;
            }
        }
    }

    /**
     * 小图模式切割图片
     * 此方式直接返回截图后的 bitmap，由于内存的限制，返回的图片会比较小
     */
    public void startSmallPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300); // 输出图片大小
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_SMALL_IMAGE_CUTTING);
    }

    /**
     * 小图模式中，保存图片后，设置到视图中
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setPicToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data"); // 直接获得内存中保存的 bitmap
            // 创建 smallIcon 文件夹
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String storage = Environment.getExternalStorageDirectory().getPath();
                File dirFile = new File(storage + "/smallIcon");
                if (!dirFile.exists()) {
                    if (!dirFile.mkdirs()) {
                        Log.e("TAG", "文件夹创建失败");
                    } else {
                        Log.e("TAG", "文件夹创建成功");
                    }
                }
                File file = new File(dirFile, System.currentTimeMillis() + ".jpg");
                // 保存图片
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 在视图中显示图片
            if (photo != null) {
                @SuppressWarnings("deprecation")
                Drawable drawable = new BitmapDrawable(photo);// 转换成drawable
                llAddTaoBaoNum.setForeground(drawable);
            }
//            // 在视图中显示图片
//            ivAddDetail.setImageBitmap(photo);
        }
    }

    /**
     * 大图模式切割图片
     * 直接创建一个文件将切割后的图片写入
     */
    public void startBigPhotoZoom(Uri uri) {
        // 创建大图文件夹
        Uri imageUri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(storage + "/bigIcon");
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    Log.e("TAG", "文件夹创建失败");
                } else {
                    Log.e("TAG", "文件夹创建成功");
                }
            }
            File file = new File(dirFile, System.currentTimeMillis() + ".jpg");
            imageUri = Uri.fromFile(file);
        }
        // 开始切割
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600); // 输出图片大小
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false); // 不直接返回数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 返回一个文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_BIG_IMAGE_CUTTING);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

}
