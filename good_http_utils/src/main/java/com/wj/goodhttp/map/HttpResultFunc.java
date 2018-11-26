package com.wj.goodhttp.map;

/**
 * 作者：wujie on 2018/11/25 15:41
 * 邮箱：705030268@qq.com
 * 功能：
 */

import com.wj.goodhttp.exception.ApiException;
import com.wj.goodhttp.vo.HttpResult;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 功能：主要是捕捉服务端api返回的data——这个需要根据自己的服务端返回协议做响应处理
 *  比如请求不成功，返回 {"ok":false, "errorcode":"usercode_invalid, "errormsg":"用户无效"}
 *  请求成功，返回  {"ok":false, "data":...}
 */
public class HttpResultFunc<T> implements Function<HttpResult<T>, T> {
    @Override
    public T apply(@NonNull HttpResult<T> tHttpResult) throws Exception {
        if (!tHttpResult.isOk()) {
            throw new ApiException(tHttpResult.getErrormsg(), tHttpResult.getErrorcode(), tHttpResult.getData());
        }
        return tHttpResult.getData();
    }
}
