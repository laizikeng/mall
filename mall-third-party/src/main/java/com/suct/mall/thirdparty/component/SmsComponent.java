package com.suct.mall.thirdparty.component;

import com.suct.mall.thirdparty.util.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: SmsComponent</p>
 * Description：
 * date：2020/6/25 14:23
 */
@Data
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Component
public class SmsComponent {

	private String host;

	private String path;

	private String smsSignId;

	private String templateId;

	private String appCode;

	private String clientId;

	private String clientSecret;

	public String sendSmsCode(String phone, String code){
		String host = "https://gyytz.market.alicloudapi.com";
		String path = "/sms/smsSend";
		String method = "POST";
		String appcode = "1a8f802d505447e2a8aaf96357cc3a00";
		Map<String, String> headers = new HashMap<String, String>();
		//最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		Map<String, String> querys = new HashMap<String, String>();
		querys.put("mobile", phone);
		querys.put("param", "**code**:"+code+",**minute**:5");
		querys.put("smsSignId", smsSignId);
		querys.put("templateId", templateId);
		Map<String, String> bodys = new HashMap<String, String>();


		try {
			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
//			System.out.println(response.toString());
			//获取response的body
			String s = EntityUtils.toString(response.getEntity());
			String[] strs = s.split(":");
			if("\"0\"".equals(strs[strs.length-1].replaceAll("}",""))){
				return "true";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "fail";
	}
}
