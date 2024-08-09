package tbs.framework.auth.model;

import lombok.Data;
import tbs.framework.base.model.BaseEntity;
import tbs.framework.utils.SingletonHolder;
import tbs.framework.utils.UuidUtil;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author abstergo
 */

@Data
public class RuntimeData extends BaseEntity<String> {

    public static RuntimeData getInstance() {
        return SingletonHolder.getInstance(RuntimeData.class);
    }

    private static final long serialVersionUID = 3944172100933159385L;

    public RuntimeData() {
        this.setId(UuidUtil.getUuid());
        systemDataCreateTime = LocalDateTime.now();
    }


    /**
     * 获取当前用户是否登录
     *
     * @return
     */
    public static final boolean userLogined() {
        return RuntimeData.getInstance().getUserModel() != null;
    }

    /**
     * 当前运行数据生成时间
     */
    private LocalDateTime systemDataCreateTime;

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
    private Set<TokenModel> tokenList = new HashSet<>();

}
