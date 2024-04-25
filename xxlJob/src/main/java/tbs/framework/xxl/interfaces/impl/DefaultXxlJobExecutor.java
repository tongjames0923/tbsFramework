package tbs.framework.xxl.interfaces.impl;

import com.alibaba.fastjson2.JSON;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.context.ApplicationContext;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.xxl.interfaces.IJsonJobHandler;
import tbs.framework.xxl.model.ExecuteInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class DefaultXxlJobExecutor {

    private static ILogger log;



    Map<String, IJsonJobHandler> jsonJobHandlerMap = new HashMap<>();

    public DefaultXxlJobExecutor(ApplicationContext applicationContext, LogUtil logUtil) {
        jsonJobHandlerMap = applicationContext.getBeansOfType(IJsonJobHandler.class);
        if (null == DefaultXxlJobExecutor.log) {
            log = logUtil.getLogger(DefaultXxlJobExecutor.class.getName());
        }
    }

    @XxlJob("jsonJobHandler")
    public void function() {
        try {
            ExecuteInfo executeInfo = JSON.parseObject(XxlJobHelper.getJobParam(), ExecuteInfo.class);
            if (!jsonJobHandlerMap.containsKey(executeInfo.getMethod())) {
                XxlJobHelper.handleFail("can not find any method name as " + executeInfo.getMethod());
            } else {
                IJsonJobHandler jsonJobHandler = jsonJobHandlerMap.get(executeInfo.getMethod());
                if (null == jsonJobHandler) {
                    XxlJobHelper.handleFail("no json Job handler");
                    return;
                }
                XxlJobHelper.handleSuccess(jsonJobHandler.handle(jsonJobHandler.paramConvert(executeInfo.getParams())));
            }

        } catch (Exception e) {
            log.error(e, "xxl-job error");
            XxlJobHelper.handleFail("error:" + e.getMessage());
        }
    }

    @XxlJob("available")
    public void availables() {
        List<Map> ls = new LinkedList<>();
        for (Map.Entry<String, IJsonJobHandler> entry : jsonJobHandlerMap.entrySet()) {
            Map<String, String> map = new HashMap<>();
            map.put("methodName", entry.getKey());
            map.put("helpText", entry.getValue().help());
            ls.add(map);
        }
        XxlJobHelper.handleSuccess(JSON.toJSONString(ls));
    }

    @XxlJob("help_json")
    public void help() {
        ExecuteInfo executeInfo = new ExecuteInfo();
        executeInfo.setMethod("demoMethodName");
        Map m = new HashMap();
        m.put("intType", 1);
        m.put("stringType", "can be anything you need");
        executeInfo.setParams(m);
        XxlJobHelper.handleSuccess(JSON.toJSONString(executeInfo));
    }

}
