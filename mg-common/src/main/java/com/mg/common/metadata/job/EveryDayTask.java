package com.mg.common.metadata.job;

import com.mg.common.metadata.service.MObjectScriptService;
import com.mg.framework.entity.metadata.MObjectScriptEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 每天需要执行的元数据，更新任务
 */
@Component
@Lazy(value=false)
public class EveryDayTask {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private MObjectScriptService mObjectScriptService;
	@Scheduled(cron="0 58 23 * * ?")
	public void task(){
		logger.debug("excute EveryDayTask....");
		List<MObjectScriptEntity> list = mObjectScriptService.findAll();
		for (MObjectScriptEntity scriptEntity:list){
			mObjectScriptService.execTask(scriptEntity);
		}

		logger.debug("EveryDayTask has done!");
	}
}
