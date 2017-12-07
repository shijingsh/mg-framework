package com.mg.common.user.controller;

import com.mg.common.components.SmsService;
import com.mg.common.entity.SmsCodeEntity;
import com.mg.common.user.service.SmsCodeService;
import com.mg.framework.utils.JsonResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Random;

@Controller
@RequestMapping(value = "/",
		produces = "application/json; charset=UTF-8")
public class SmsCodeController {
	@Autowired
	private SmsService smsService;
	@Autowired
	private SmsCodeService smsCodeService;

	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * 发送短信
	 * @param mobile
     * @return
     */
	@ResponseBody
    @RequestMapping("verifyCode")
    public String verifyCode(String mobile) {
    	if(StringUtils.isBlank(mobile)) {
			return JsonResponse.error(100000, "请输入手机号码");
    	}
    	if(mobile.length()!=11) {
			return JsonResponse.error(100000, "电话号码长度应为11位");
    	}
    	try {
        	//发送短信验证码并保存验证码数据
        	String randomStr = getRandomStr();
    		smsService.sendSmsForVerificationCode(mobile, randomStr);
        	System.out.println("短信验证码为："+randomStr);

			SmsCodeEntity smsCodeEntity = new SmsCodeEntity();
			smsCodeEntity.setMobile(mobile);
			smsCodeEntity.setSmsCode(randomStr);
			smsCodeEntity.setSendTime(new Date());
			smsCodeService.save(smsCodeEntity);
    	} catch (ServiceException e) {
    		e.printStackTrace();
			return JsonResponse.error(100000, e.getMessage());
    	} catch (Exception e) {
    		e.printStackTrace();
			return JsonResponse.error(100000, "发送失败，请稍后重试！");
    	}
		return JsonResponse.success();
    }

	private static String getRandomStr() {
		Random random = new Random();
		StringBuffer buf = new StringBuffer(16);
		for (int i = 0; i < 6; i++) {
			int charOrNum = random.nextInt(10);
			buf.append(charOrNum);
		}
		return buf.toString();
	}


}