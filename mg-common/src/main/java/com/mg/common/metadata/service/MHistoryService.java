package com.mg.common.metadata.service;

import java.util.Map;

/**
 * Created by liukefu on 2015/10/19.
 */
public interface MHistoryService {

    public boolean createHistory(String mObjectName,String propertyName,String historyPropertyName,Map<String,Object> param);

    public boolean createHistory(String mObjectName,String propertyName,String historyPropertyName,String startDatePropertyName,Map<String,Object> param);
}
