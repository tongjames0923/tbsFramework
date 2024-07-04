package tbs.framework.sql.interfaces.impls.convertor;

import tbs.framework.base.interfaces.impls.chain.AbstractChain;
import tbs.framework.utils.TimeUtil;

import java.time.temporal.Temporal;
import java.util.Date;

/**
 * 日期转换 Temporal 和Date
 * @author Abstergo
 */
public class DateValueConvertor extends AbstractChain<Object, String> {
    @Override
    public void doChain(Object value) {
        if (value instanceof Temporal) {
            final Temporal instant = (Temporal)value;
            this.done(TimeUtil.DATE_FORMAT_TEMPORAL_yyyyMMddHHmmss.format(instant));
            return;
        }
        if (value instanceof Date) {
            final Date date = (Date)value;
            done(TimeUtil.DATE_FORMAT_yyyyMMddHHmmss.format(date));
        }
    }
}
