package com.wj.goodhttp.file;

/**
 * 作者：wujie on 2018/11/26 00:53
 * 邮箱：705030268@qq.com
 * 功能：下载进度回调
 */

public interface IDownloadListener {

    //下载成功的回调
    void onDownloadSuccess();

    //下载失败回调
    void onDownloadFail(Exception exception);

    //下载进度回调
    //(int) (bytesReaded*100 / contentLength)
    void onProgress(int progress);
}
