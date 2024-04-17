package tbs.framework.xxl.interfaces.impl;

import tbs.framework.xxl.interfaces.IJsonJobHandler;

import java.util.Map;

public interface IMapJsonJobHandler extends IJsonJobHandler<Map> {
    @Override
    default Class<? extends Map> classType() {
        return Map.class;
    }
}
