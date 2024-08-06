package tbs.framework.auth.model;

import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author abstergo
 */

@Data
public class RuntimeData implements Serializable {

    public static RuntimeData getInstance() {
        return SpringUtil.getBean(RuntimeData.class);
    }

    private static final long serialVersionUID = 3944172100933159385L;

    /**
     * 获取当前用户是否登录
     *
     * @return
     */
    public static final boolean userLogined() {
        return RuntimeData.getInstance().getUserModel() != null;
    }

    /**
     * 业务运行启动时间
     */
    private LocalDateTime invokeBegin;
    /**
     * 业务运行结束时间
     */
    private LocalDateTime invokeEnd;

    /**
     * 业务运行函数
     */
    private Method invokeMethod;
    /**
     * 业务运行参数
     */
    private Object[] invokeArgs;

    /**
     * 访问url路径
     */
    private String invokeUrl;

    /**
     * 访问用户数据
     */
    private UserModel userModel;

    /**
     * 访问token数据
     */
    public Set<TokenModel> tokenList = new HashSet<>();

}
