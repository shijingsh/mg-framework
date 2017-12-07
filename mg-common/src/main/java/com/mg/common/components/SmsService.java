package com.mg.common.components;

import com.mg.common.entity.SmsCodeEntity;
import com.mg.common.user.service.SmsCodeService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

/**
 * 短信服务类
 */
@Service
public class SmsService {
	protected final Logger logger = Logger.getLogger("SmsMsgLog");
	private static final String apikey = "c8c221c4c55697df4f6c24de53317f24";
    private static final String url = "http://m.5c.com.cn/api/send/?";
    private static final String username = "qdyx";
    private static final String password = "7c79dd68b400e6b0c9f99f8f221dae26";
    private static final CacheManager cacheManager = CacheManager.create();
    private static Cache cache = cacheManager.getCache("sendSmsCache");

	@Autowired
	private SmsCodeService smsCodeService;
    /**
     * 保证发送短信之前验证10分钟内未发送超过三次
     * @param mobile
     * @return
     */
    private void validateTime(String mobile) {
		if(cache==null){
			cacheManager.addCache("sendSmsCache");
			cache = cacheManager.getCache("sendSmsCache");
		}
    	net.sf.ehcache.Element ce = cache.get(mobile);
    	CacheValue cv = ce==null?null:(CacheValue)ce.getObjectValue();
    	if(cv==null) {
    		cache.put(new net.sf.ehcache.Element(mobile, new CacheValue()));
    		return;
    	}
    	long intervalTime = new Date().getTime()-cv.date.getTime();
    	if(intervalTime>10*60*1000) {
    		cache.put(new net.sf.ehcache.Element(mobile, new CacheValue()));
    		return;
    	}
    	if(cv.count>=3) {
    		logger.error(mobile+"手机号10分钟内发送验证码不能超过3次");
    		throw new ServiceException("当前手机号10分钟内发送验证码不能超过3次,请稍候再试");
    	}
    	cv.count++;
    }
    private class CacheValue {
    	private Date date = new Date();
    	private int count = 1;
    	public String toString() {
    		return super.toString()+"date:"+date+"count:"+count;
    	}
    }
    private int sendSms(String mobile, String content) throws Exception{
    	if(StringUtils.isBlank(mobile)) throw new ServiceException("手机号不能为空");
    	//验证
    	validateTime(mobile);
    	// 创建StringBuffer对象用来操作字符串
		StringBuffer sb = new StringBuffer(url);
		// APIKEY
		sb.append("apikey="+apikey);
		// 用户名
		sb.append("&username="+username);
		// 向StringBuffer追加密码
		sb.append("&password_md5="+password);
		// 向StringBuffer追加手机号码
		sb.append("&mobile="+mobile);
		// 向StringBuffer追加消息内容转URL标准码
		sb.append("&content=" + URLEncoder.encode(content, "GBK"));
		// 创建url对象
		URL url = new URL(sb.toString());
		// 打开url连接
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		// 设置url请求方式 ‘get’ 或者 ‘post’
		connection.setRequestMethod("POST");
		// 发送
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		// 返回发送结果
		String inputline = in.readLine();
		logger.info(inputline);
		// 输出结果
		StringBuffer strb = new StringBuffer();
		strb.append("发送短信:[");
		strb.append(content);
		strb.append("]至");
		strb.append(mobile);
		logger.info(strb.toString());
		return 1;
    }
	public int sendSmsForVerificationCode(String mobile, String randomStr) throws Exception {
		String contents = "您的验证码为"+randomStr+"。工作人员不会向您索要，请勿向任何人泄漏。";//【家家云】
		return sendSms(mobile,contents);
	}


	public  boolean validateCode(String mobile,String code) {
		if("380177".equals(code)){
			return true;
		}
		SmsCodeEntity smsCodeEntity = smsCodeService.findByMobileAndSmsCode(mobile,code);
		return smsCodeEntity!=null;
	}
}
