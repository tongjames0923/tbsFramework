package tbs.framework.auth.interfaces.debounce.impls;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import tbs.framework.auth.exceptions.DebounceException;
import tbs.framework.auth.interfaces.debounce.IDebounce;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.model.TokenModel;
import tbs.framework.auth.model.UserModel;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.utils.AESUtil;
import tbs.framework.utils.UuidUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AESTokenDebounce implements IDebounce {

    private String tokenField;

    private String key = "DEBOUNCE_KEY~!@#";

    @AutoLogger
    ILogger logger;

    Map<String, DebounceInfo> tokenMap = new HashMap<>();

    public String makeToken(UserModel user, String url) throws Exception {

        DebounceInfo debounceInfo = new DebounceInfo(user.getUserId(), url);
        String token = AESUtil.encrypt(key, JSON.toJSONString(debounceInfo));
        tokenMap.put(debounceInfo.id, debounceInfo);
        return token;
    }

    /**
     * 默认100毫秒冷却
     */
    private int expireTime = 3 * 1000;

    @Data
    private static final class DebounceInfo {
        private String user;
        private long createTimeStamp;
        private String url;
        private String id;

        public DebounceInfo(String user, String url) {
            this.user = user;
            this.createTimeStamp = System.currentTimeMillis();
            this.id = UuidUtil.getUuid();
            this.url = url;
        }
    }

    public AESTokenDebounce(String tokenField, int expireTime) {
        this.tokenField = tokenField;
        this.expireTime = expireTime;
    }

    public String key() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void debounce(String url, UserModel user, Method method, Object target, Object[] args)
        throws DebounceException {
        boolean hasToken = false;
        DebounceInfo info = null;
        for (TokenModel tm : RuntimeData.getInstance().getTokenList()) {
            if (Objects.equals(tm.getField(), tokenField) && StrUtil.isNotEmpty(tm.getToken())) {
                long now = System.currentTimeMillis();
                try {
                    info = JSON.parseObject(AESUtil.decrypt(key, tm.getToken()), DebounceInfo.class);
                    hasToken = info != null && StrUtil.isNotEmpty(info.id) && tokenMap.containsKey(info.id);

                    if (hasToken &&
                        Objects.equals(info.url, url) &&
                        Objects.equals(info.user, user.getUserId()) &&
                        info.createTimeStamp + expireTime > now) {
                        logger.debug("防抖Token{}命中", tm.getToken());
                    } else {
                        throw new DebounceException("防抖Token未命中");
                    }
                } catch (DebounceException debounceException) {
                    throw debounceException;
                } catch (Exception e) {
                    logger.warn("错误的防抖Token{},错误信息：{}", tm.getToken(), e.getMessage());
                } finally {
                    if (hasToken) {
                        tokenMap.remove(info.id);
                    }
                }
            }
        }
    }
}
