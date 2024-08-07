package tbs.framework.auth.interfaces.debounce.impls;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import tbs.framework.auth.exceptions.DebounceException;
import tbs.framework.auth.interfaces.debounce.AbstractTokenDebounce;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.model.TokenModel;
import tbs.framework.auth.model.UserModel;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.utils.AESUtil;
import tbs.framework.utils.UuidUtil;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author abstergo
 */
public class AESTokenDebounce extends AbstractTokenDebounce<AESTokenDebounce.DebounceInfo> {

    private String tokenField;

    private String key = "DEBOUNCE_KEY~!@#";

    @AutoLogger
    ILogger logger;

    Map<String, DebounceInfo> tokenMap = new HashMap<>();

    //    @Override
    //    protected String genToken(TokenFactor fac) {
    //        DebounceInfo debounceInfo = new DebounceInfo(fac.user.getUserId(), fac.url);
    //        String token = null;
    //        try {
    //            token = AESUtil.encrypt(key, JSON.toJSONString(debounceInfo));
    //        } catch (Exception e) {
    //            logger.error(e, "生成token失败");
    //        }
    //        return token;
    //    }
    //
    //    @Override
    //    protected void consumeToken(TokenFactor token) throws DebounceException {
    //
    //    }
    //
    //    @Override
    //    protected void tokenRemove(TokenFactor token) {
    //
    //    }
    //
    //    @Override
    //    protected TokenFactor toFactor(UserModel user, Method method, Object[] args) {
    //        return null;
    //    }
    //
    //    @Override
    //    protected void tokenApply(String token, TokenFactor fac) {
    //        tokenMap.put(token, fac);
    //    }
    //
    //    @Data
    //    @AllArgsConstructor
    //    @NoArgsConstructor
    //    public static class TokenFactor {
    //        UserModel user;
    //        String url;
    //    }


    /**
     * 默认100毫秒冷却
     */
    private int expireTime = 3 * 1000;

    @Data
    public static final class DebounceInfo {
        private String user;
        private long createTimeStamp;
        private String url;
        private String id;

        public DebounceInfo(String user, String url) {
            this.user = user;
            this.createTimeStamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
            this.id = UuidUtil.getUuid();
            this.url = url;
        }
    }

    public AESTokenDebounce(String tokenField, int expireTime) {
        this.tokenField = tokenField;
        this.expireTime = expireTime;
    }

    @Override
    protected String genToken(DebounceInfo fac) {
        String token = null;
        try {
            token = AESUtil.encrypt(key, JSON.toJSONString(fac));
        } catch (Exception e) {
            logger.error(e, "生成token失败");
        }
        return token;
    }

    @Override
    protected void tokenApply(String token, DebounceInfo fac) {
        tokenMap.put(fac.id, fac);
    }

    @Override
    protected void consumeToken(DebounceInfo info) throws DebounceException {
        long requestBegin =
            RuntimeData.getInstance().getSystemDataCreateTime().toInstant(ZoneOffset.UTC).toEpochMilli();
        DebounceInfo inside = tokenMap.get(info.id);
        if (Objects.equals(info.url, inside.url) &&
            Objects.equals(info.user, inside.user) &&
            info.createTimeStamp + expireTime > requestBegin) {
            logger.debug("防抖Token{}命中", info);
        } else {
            throw new DebounceException("防抖Token未命中");
        }
    }

    @Override
    protected void tokenRemove(DebounceInfo token) {
        tokenMap.remove(token.id);
    }

    @Override
    protected DebounceInfo toFactor(UserModel user, Method method, Object[] args) {
        DebounceInfo info = null;
        for (TokenModel tm : RuntimeData.getInstance().getTokenList()) {
            if (Objects.equals(tm.getField(), tokenField) && StrUtil.isNotEmpty(tm.getToken())) {
                try {
                    info = JSON.parseObject(AESUtil.decrypt(key, tm.getToken()), DebounceInfo.class);
                } catch (Exception e) {
                    logger.error(e, "解析token失败");
                }
                break;
            }
        }

        if (info != null && StrUtil.isNotEmpty(info.id) && tokenMap.containsKey(info.id)) {
            return info;
        } else {
            return null;
        }
    }
}
