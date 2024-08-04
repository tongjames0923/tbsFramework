package tbs.framework.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

/**
 * 权限数据
 *
 * @author abstergo
 */
@Data
public class PermissionModel implements Serializable {

    /**
     * 权限验证结果
     */
    @Data
    public static class VerificationResult implements Serializable {
        private static final long serialVersionUID = -3077326548884655564L;

        /**
         * 验证成功
         */
        public static final Integer SUCCESS = 1;
        /**
         * 验证失败
         */
        public static final Integer FAIL = -1;

        /**
         * 结果代号
         */
        private Integer code;
        /**
         * 信息
         */
        private String message;

        /**
         * 验证异常
         */
        private RuntimeException error;

        /**
         * @return true:验证成功
         */
        public boolean success() {
            return VerificationResult.SUCCESS.equals(this.code);
        }

        /**
         * @return true:存在异常
         */
        public boolean hasError() {
            return !success() && null != error;
        }

        /**
         * 产生验证成功的结果
         * @param message
         * @return
         */

        public static VerificationResult success(final String message) {
            return new VerificationResult(VerificationResult.SUCCESS, message, null);
        }

        /**
         * 产生异常的处理结果
         * @param message 异常消息，若error不为空则覆盖
         * @param error 异常
         * @return
         */
        public static VerificationResult error(final String message, final RuntimeException error) {
            final RuntimeException exception = Optional.ofNullable(error).orElse(new RuntimeException(message));
            return new VerificationResult(VerificationResult.FAIL, exception.getMessage(), exception);
        }

        /**
         * 拒绝的验证结果
         * @param message 拒绝信息
         * @return
         */
        public static VerificationResult reject(final String message) {
            return new VerificationResult(VerificationResult.FAIL, message, null);
        }


        public VerificationResult(final Integer code, final String message, final RuntimeException error) {
            this.code = code;
            this.message = message;
            this.error = error;
        }
    }

    private static final long serialVersionUID = 8057596043664751534L;

    /**
     * 权限
     */
    private String role;

    /**
     * 携带的参数
     */
    private Object parameter;

    /**
     * 权限生效的路径
     */
    private String url;
}
