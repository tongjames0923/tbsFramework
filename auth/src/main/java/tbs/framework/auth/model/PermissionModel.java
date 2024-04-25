package tbs.framework.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

@Data
public class PermissionModel implements Serializable {
    @Data
    public static class VerificationResult implements Serializable {
        private static final long serialVersionUID = -3077326548884655564L;

        public static final Integer SUCCESS = 1, FAIL = -1;

        private Integer code;
        private String message;
        private RuntimeException error;

        public boolean success() {
            return SUCCESS.equals(code);
        }

        public boolean hasError() {
            return error != null;
        }

        public static VerificationResult success(String message) {
            return new VerificationResult(SUCCESS, message, null);
        }

        public static VerificationResult error(String message, RuntimeException error) {
            RuntimeException exception = Optional.ofNullable(error).orElse(new RuntimeException(message));
            return new VerificationResult(FAIL, exception.getMessage(), exception);
        }

        public static VerificationResult reject(String message) {
            return new VerificationResult(FAIL, message, null);
        }

        public VerificationResult(Integer code, String message, RuntimeException error) {
            this.code = code;
            this.message = message;
            this.error = error;
        }
    }

    private static final long serialVersionUID = 8057596043664751534L;
    private String role;
    private Object parameter;
    private String url;
}
