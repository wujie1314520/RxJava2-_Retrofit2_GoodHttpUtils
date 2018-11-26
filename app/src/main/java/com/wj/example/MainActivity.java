package com.wj.example;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wj.example.api.ApiService;
import com.wj.example.api.FileUploadService;
import com.wj.example.image.PicassoImageLoader;
import com.wj.example.vo.ActivityArticle;
import com.wj.goodhttp.exception.ErrorAction;
import com.wj.goodhttp.file.FileDownloadService;
import com.wj.goodhttp.file.FileUploadObserver;
import com.wj.goodhttp.file.IDownloadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int BUTTON4_IMAGE_PICK = 0x01;
    private static final int BUTTON5_IMAGE_PICK = 0x02;
    private static final int BUTTON7_IMAGE_PICK = 0x03;

    private ImagePicker mImagePicker;

    /**
     * Get表单请求
     */
    @OnClick(R.id.button1)
    public void get() {
        ApiService.getInstance().getActivites(1, 20)
                .subscribe(activityArticles -> ToastUtils.showShort("请求成功"), new ErrorAction());
    }

    /**
     * Post表单请求
     */
    @OnClick(R.id.button2)
    public void post() {
        ApiService.getInstance().getLatestVersion()
                .subscribe(appVersion -> ToastUtils.showShort("请求成功"), new ErrorAction());
    }

    /**
     * Post复杂的Json对象
     */
    @OnClick(R.id.button3)
    public void postJson() {
        ActivityArticle article = new ActivityArticle();
        article.title = "ArchSummit全球架构师峰";
        article.articleurl = "https://www.oschina.net/event/2288817";
        article.image1url = "https://static.oschina.net/uploads/space/2018/1102/181140_cfy7_3843409.jpg";
        article.image2url = "https://static.oschina.net/uploads/space/2018/1102/181157_MYXH_3843409.jpg";
        ApiService.getInstance().addActivity(article)
                .subscribe(aBoolean -> ToastUtils.showShort("请求成功"), new ErrorAction());
    }

    /**
     * 单个文件上传监听进度
     */
    @OnClick(R.id.button4)
    public void upload() {
        //图片是单选的
        mImagePicker.setMultiMode(false);
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, BUTTON4_IMAGE_PICK);
    }

    private void uploadButton4(String localPath) {
        File uploadFile = new File(localPath);
        ProgressDialog dialog = getProgressDialog();
        dialog.show();
        FileUploadService.getInstance().uploadAvatar(uploadFile, new FileUploadObserver<ResponseBody>() {
            @Override
            public void onUpLoadSuccess(ResponseBody responseBody) {
                ToastUtils.showShort("文件上传成功");
                dialog.dismiss();
            }

            @Override
            public void onUpLoadFail(Throwable e) {
                ToastUtils.showShort("文件上传失败");
                dialog.dismiss();
            }

            @Override
            public void onProgress(int progress) {
                dialog.setProgress(progress * 100);
                Logger.d("下载进度为:%d", progress);
            }
        });
    }

    private ProgressDialog getProgressDialog() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setIcon(R.mipmap.ic_launcher);// 设置提示的title的图标，默认是没有的
        dialog.setTitle("文件上传");
        return dialog;
    }

    /**
     * 单个文件上传监听进度+其他表单参数
     */
    @OnClick(R.id.button5)
    public void uploadFile() {
        //图片是单选的
        mImagePicker.setMultiMode(false);
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, BUTTON5_IMAGE_PICK);
    }

    private void uploadButton5(String localpath) {
        File uploadFile = new File(localpath);
        Logger.d("图片地址：%s", localpath);
        ProgressDialog dialog = getProgressDialog();
        dialog.show();
        FileUploadService.getInstance().uploadAvatar2(uploadFile, "20180607090729115PHHBSWE", new FileUploadObserver<ResponseBody>() {
            @Override
            public void onUpLoadSuccess(ResponseBody responseBody) {
                ToastUtils.showShort("文件上传成功");
//                dialog.dismiss();
            }

            @Override
            public void onUpLoadFail(Throwable e) {
                ToastUtils.showShort("文件上传失败");
            }

            @Override
            public void onProgress(int progress) {
                dialog.setProgress(progress * 100);
                Logger.d("下载进度为:%d", progress);
            }
        });
    }

    /**
     * 多文件上传监听进度+其他表单参数
     */
    @OnClick(R.id.button7)
    public void uploadMultFileOthers() {
        //图片是多选，一次只能上传三张
        mImagePicker.setMultiMode(true);
        mImagePicker.setSelectLimit(3);
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, BUTTON7_IMAGE_PICK);
    }

    private void uploadButton7(List<File> files) {
        ProgressDialog dialog = getProgressDialog();
        dialog.show();
        FileUploadService.getInstance().uploadIssueReport(files, "00000001", "开不了了", "真的开不了了",
                new FileUploadObserver<ResponseBody>() {
                    @Override
                    public void onUpLoadSuccess(ResponseBody responseBody) {
                        ToastUtils.showShort("文件上传成功");
//                        dialog.dismiss();
                    }

                    @Override
                    public void onUpLoadFail(Throwable e) {
                        ToastUtils.showShort("文件上传失败");
                    }

                    @Override
                    public void onProgress(int progress) {
                        dialog.setProgress(progress * 100);
                        Logger.d("下载进度为:%d", progress);
                    }
                });
    }

    /**
     * 文件下载监听进度
     */
    @OnClick(R.id.button8)
    public void download() {
        String url = "http://192.168.1.8:8080/rent/assets/img/bikefault/20181125222314579QDUGWYOO1.png";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //sd卡已挂载
//            String fileDirPath = new StringBuilder().append(getExternalCacheDir().getAbsolutePath()) + "/imgs/";
            String fileDirPath = new StringBuilder().append(Environment.getExternalStorageDirectory().getAbsolutePath()) + "/AndFix/";
            File fileDir = new File(fileDirPath);
            try {
                if(null == fileDir || !fileDir.exists()) {
                    fileDir.mkdir();
                }
                Logger.d("存储位置:%s", fileDirPath.concat(System.currentTimeMillis() + ".").concat(FileUtils.getFileExtension(url)));
                FileDownloadService.getInstance(new IDownloadListener() {
                    @Override
                    public void onDownloadSuccess() {
                        ToastUtils.showShortSafe("文件下载成功");
                    }

                    @Override
                    public void onDownloadFail(Exception exception) {
                        ToastUtils.showShortSafe("文件下载失败");
                    }

                    @Override
                    public void onProgress(int progress) {
                        Logger.d("文件下载%d", progress);
                    }
                }).download(url, fileDirPath.concat(System.currentTimeMillis() + ".").concat(FileUtils.getFileExtension(url)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtils.showShortSafe("SD卡未挂载");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定初始化ButterKnife
        ButterKnife.bind(this);
        initImage();
        requestPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if(data != null) {
                if (requestCode == BUTTON4_IMAGE_PICK) {
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    uploadButton4(images.get(0).path);
                } else if(requestCode == BUTTON5_IMAGE_PICK) {
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    uploadButton5(images.get(0).path);
                } else if(requestCode == BUTTON7_IMAGE_PICK) {
                    //多选
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    List<File> files = new ArrayList<>();
                    Iterator<ImageItem> it = images.iterator();
                    while (it.hasNext()) {
                        String path = it.next().path;
                        files.add(new File(path));
                        Logger.d("图片地址：%s", path);
                    }
                    uploadButton7(files);
                }
            }
            else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initImage() {
        mImagePicker = ImagePicker.getInstance();
        mImagePicker.setImageLoader(new PicassoImageLoader());
        mImagePicker.setShowCamera(true);
        mImagePicker.setCrop(false);
        mImagePicker.setSaveRectangle(true);
    }

    private void requestPermissions() {
        //申请SD卡的读写权限
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (!aBoolean) {
                        ToastUtils.showLong("亲，没有SD卡读写权限很多功能可能会不正常哦！");
                    }
                });
    }
}
