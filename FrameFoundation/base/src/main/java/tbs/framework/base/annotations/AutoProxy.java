package tbs.framework.base.annotations;

import org.springframework.stereotype.Component;
import tbs.framework.proxy.IAutoProxy;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface AutoProxy {
    Class<? extends IAutoProxy> proxyImpl();
}
