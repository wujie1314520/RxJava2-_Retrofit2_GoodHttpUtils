package com.wj.goodhttp.map;


import com.wj.goodhttp.exception.ApiException;
import com.wj.goodhttp.vo.HttpResult;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 作者：wujie on 2018/11/25 15:45
 * 邮箱：705030268@qq.com
 * 功能：主要是捕捉服务端api返回的异常信息——这个需要根据自己的服务端返回协议做响应处理
 *  比如请求不成功，返回 {"ok":false, "errorcode":"usercode_invalid, "errormsg":"用户无效"}
 *  请求成功，返回  {"ok":false, "data":...}
 */

public class BooleanResultFunc<T> implements Function<HttpResult<T>, Boolean> {
    @Override
    public Boolean apply(@NonNull HttpResult<T> tHttpResult) throws Exception {
        if (!tHttpResult.isOk()) {
            throw new ApiException(tHttpResult.getErrormsg(), tHttpResult.getErrorcode(), tHttpResult.getData());
        }
        return tHttpResult.isOk();
    }
}
