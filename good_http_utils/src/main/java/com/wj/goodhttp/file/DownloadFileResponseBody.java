package com.wj.goodhttp.file;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 扩展OkHttp的请求体，实现下载时的进度提示
 */
public class DownloadFileResponseBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private IDownloadListener mListener;

    // BufferedSource 是okio库中的输入流，这里就当作inputStream来使用。
    private BufferedSource mBufferedSource;

    public DownloadFileResponseBody(ResponseBody responseBody, IDownloadListener listener) {
        this.mResponseBody = responseBody;
        this.mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                if (mListener != null) {
                    if (bytesRead != -1) {
                        mListener.onProgress((int) (totalBytesRead * 100 / contentLength()));
                    }
                }
                return bytesRead;
            }
        };

    }
}
