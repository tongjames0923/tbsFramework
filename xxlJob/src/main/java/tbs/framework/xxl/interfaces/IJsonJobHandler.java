package tbs.framework.xxl.interfaces;



import com.alibaba.fastjson2.JSON;

import java.util.Map;

public interface IJsonJobHandler<T> {
    Class<? extends T> classType();

    default String help() {
        return "";
    }

    default T paramConvert(final Map mp) {
        return JSON.to(this.classType(), JSON.toJSONString(mp));
    }

    String handle(T params) throws Exception;
}
