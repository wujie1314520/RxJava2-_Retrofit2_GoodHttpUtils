package com.wj.example.api;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.blankj.utilcode.util.StringUtils;
import com.wj.goodhttp.creator.FileUploadCreator;
import com.wj.goodhttp.exception.ApiException;
import com.wj.goodhttp.exception.ErrorAction;
import com.wj.goodhttp.file.FileUploadObserver;
import com.wj.goodhttp.file.UploadFileRequestBody;

import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 作者：wujie on 2018/11/24 22:10
 * 邮箱：705030268@qq.com
 * 功能：文件上传
 */

public class FileUploadService {

    private final INetService mApiService;

    private FileUploadService() {
        mApiService = FileUploadCreator.getRetrofitClient().create(INetService.class);
    }

    //静态内部类创建单例
    public static class InstanceHolder {
        private static final FileUploadService INSTANCE = new FileUploadService();
    }

    public static FileUploadService getInstance() {
        return InstanceHolder.INSTANCE;
    }


    /**
     * 单上传文件
     * @param file   需要上传的文件
     * @param fileUploadObserver 上传回调
     */
    public void uploadAvatar(File file, FileUploadObserver<ResponseBody> fileUploadObserver) {
        UploadFileRequestBody uploadFileRequestBody = new UploadFileRequestBody(file, fileUploadObserver);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), uploadFileRequestBody);
        mApiService.uploadAvatar(part)
                .filter(responseBody -> handleResponse(responseBody))
                .compose(ApiService.io_main())
                .doOnError(new ErrorAction())
                .subscribe(fileUploadObserver);
    }


    public void uploadAvatar2(File file, String usercode, FileUploadObserver<ResponseBody> fileUploadObserver) {
        Map<String, RequestBody> uploadInfo = new ArrayMap<>();
        UploadFileRequestBody uploadFileRequestBody = new UploadFileRequestBody(file, fileUploadObserver);
        uploadInfo.put("image\"; filename=\"" + file.getName() + "", uploadFileRequestBody);
        if (!StringUtils.isEmpty(usercode)) {
            uploadInfo.put("usercode", RequestBody.create(MediaType.parse("text/plain"), usercode.trim()));
        }
        mApiService.uploadAvatar2(uploadInfo)
                .filter(responseBody -> handleResponse(responseBody))
                .compose(ApiService.io_main())
                .doOnError(new ErrorAction())
                .subscribe(fileUploadObserver);
    }

    //如果ok => false 需要过滤 否则 不需要
    private boolean handleResponse(ResponseBody responseBody) {
        BufferedSource source = responseBody.source();
        try {
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            String body = buffer.clone().readString(charset);
            JSONObject jsonObject = new JSONObject(body);
            boolean ok = jsonObject.getBoolean("ok");
            if(!ok) {
                throw new ApiException(jsonObject.getString("info"), jsonObject.getString("msg"), jsonObject.getString("data"));
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 多图片上传 : picture1 pictur2 pictur3
     */
    public void uploadIssueReport(List<File> fileList, String bikecode, String content, String remarks,
                                  FileUploadObserver<ResponseBody> fileUploadObserver) {
        Map<String, RequestBody> uploadInfo = new ArrayMap<>();
        for (int i = 0; i < fileList.size(); i++) {
            File file = fileList.get(i);
            UploadFileRequestBody uploadFileRequestBody = new UploadFileRequestBody(file, fileUploadObserver);
            uploadInfo.put("picture" + (i + 1) + "\"; filename=\"" + file.getName() + "", uploadFileRequestBody);
        }
        if (!TextUtils.isEmpty(bikecode)) {
            uploadInfo.put("bikecode", RequestBody.create(MediaType.parse("text/plain"), bikecode.trim()));
        }
        if (!TextUtils.isEmpty(content)) {
            uploadInfo.put("content", RequestBody.create(MediaType.parse("text/plain"), content.trim()));
        }
        if (!TextUtils.isEmpty(remarks)) {
            uploadInfo.put("remarks", RequestBody.create(MediaType.parse("text/plain"), remarks.trim()));
        }
        mApiService.uploadIssueReport(uploadInfo)
                .filter(responseBody -> handleResponse(responseBody))
                .compose(ApiService.io_main())
                .doOnError(new ErrorAction())
                .subscribe(fileUploadObserver);
    }


}
