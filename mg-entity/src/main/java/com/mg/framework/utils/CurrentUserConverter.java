package com.mg.framework.utils;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class CurrentUserConverter extends ClassicConverter {
	
	@Override
	public String convert(ILoggingEvent event) {
		return UserHolder.getLoginUserTenantId()+":"+ UserHolder.getLoginUserName();
	}

}
