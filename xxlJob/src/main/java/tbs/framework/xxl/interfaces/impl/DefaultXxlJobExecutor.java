package tbs.framework.xxl.interfaces.impl;

import com.alibaba.fastjson2.JSON;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.context.ApplicationContext;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.xxl.interfaces.IJsonJobHandler;
import tbs.framework.xxl.model.ExecuteInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class DefaultXxlJobExecutor {

    @AutoLogger
    private ILogger log;



    Map<String, IJsonJobHandler> jsonJobHandlerMap = new HashMap<>();

    public DefaultXxlJobExecutor(final ApplicationContext applicationContext) {
        this.jsonJobHandlerMap = applicationContext.getBeansOfType(IJsonJobHandler.class);
    }

    @XxlJob("jsonJobHandler")
    public void function() {
        try {
            final ExecuteInfo executeInfo = JSON.parseObject(XxlJobHelper.getJobParam(), ExecuteInfo.class);
            if (!this.jsonJobHandlerMap.containsKey(executeInfo.getMethod())) {
                XxlJobHelper.handleFail("can not find any method name as " + executeInfo.getMethod());
            } else {
                final IJsonJobHandler jsonJobHandler = this.jsonJobHandlerMap.get(executeInfo.getMethod());
                if (null == jsonJobHandler) {
                    XxlJobHelper.handleFail("no json Job handler");
                    return;
                }
                XxlJobHelper.handleSuccess(jsonJobHandler.handle(jsonJobHandler.paramConvert(executeInfo.getParams())));
            }

        } catch (final Exception e) {
            log.error(e, "xxl-job error");
            XxlJobHelper.handleFail("error:" + e.getMessage());
        }
    }

    @XxlJob("available")
    public void availables() {
        final List<Map> ls = new LinkedList<>();
        for (final Map.Entry<String, IJsonJobHandler> entry : this.jsonJobHandlerMap.entrySet()) {
            final Map<String, String> map = new HashMap<>();
            map.put("methodName", entry.getKey());
            map.put("helpText", entry.getValue().help());
            ls.add(map);
        }
        XxlJobHelper.handleSuccess(JSON.toJSONString(ls));
    }

    @XxlJob("help_json")
    public void help() {
        final ExecuteInfo executeInfo = new ExecuteInfo();
        executeInfo.setMethod("demoMethodName");
        final Map m = new HashMap();
        m.put("intType", 1);
        m.put("stringType", "can be anything you need");
        executeInfo.setParams(m);
        XxlJobHelper.handleSuccess(JSON.toJSONString(executeInfo));
    }

}
