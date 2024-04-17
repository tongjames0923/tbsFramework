package tbs.framework.xxl.interfaces.impl;

import com.alibaba.fastjson2.JSON;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tbs.framework.xxl.interfaces.IJsonJobHandler;
import tbs.framework.xxl.model.ExecuteInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component("DefaultXxlJobExecutor")
@ConditionalOnBean(XxlJobSpringExecutor.class)
@AutoConfigureAfter(IJsonJobHandler.class)
@Slf4j
public class DefaultXxlJobExecutor {

    Map<String, IJsonJobHandler> jsonJobHandlerMap = new HashMap<>();

    public DefaultXxlJobExecutor(ApplicationContext applicationContext) {
        jsonJobHandlerMap = applicationContext.getBeansOfType(IJsonJobHandler.class);

    }

    @XxlJob("jsonJobHandler")
    public void function() {
        try {
            ExecuteInfo executeInfo = JSON.parseObject(XxlJobHelper.getJobParam(), ExecuteInfo.class);
            if (!jsonJobHandlerMap.containsKey(executeInfo.getMethod())) {
                XxlJobHelper.handleFail("can not find any method name as " + executeInfo.getMethod());
            } else {
                IJsonJobHandler jsonJobHandler = jsonJobHandlerMap.get(executeInfo.getMethod());
                if (jsonJobHandler == null) {
                    XxlJobHelper.handleFail("no json Job handler");
                    return;
                }
                XxlJobHelper.handleSuccess(jsonJobHandler.handle(jsonJobHandler.paramConvert(executeInfo.getParams())));
            }

        } catch (Exception e) {
            log.error("xxl-job error", e);
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
