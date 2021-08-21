package com.scut.mall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.scut.common.constant.AuthServerConstant;
import com.scut.common.utils.HttpUtils;
import com.scut.common.utils.R;
import com.scut.common.vo.MemberRsepVo;
import com.scut.mall.auth.feign.MemberFeignService;
import com.scut.mall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/oauth2.0")
public class Oath2Controller {
	@Value("${oauth.client-secret}")
	String clientSecret;

	@Value("${oauth.client-id}")
	String clientId;

	@Autowired
	private MemberFeignService memberFeignService;

//	@GetMapping("/logout")
//	public String login(HttpSession session){
//		if(session.getAttribute(AuthServerConstant.LOGIN_USER) != null){
//			log.info("\n[" + ((MemberRsepVo)session.getAttribute(AuthServerConstant.LOGIN_USER)).getUsername() + "] 已下线");
//		}
//		session.invalidate();
//		return "redirect:http://auth.glmall.com/login.html";
//	}

	@GetMapping("/gitee/success")
	public String weiBo(@RequestParam("code") String code, HttpSession session) throws Exception {

		// 根据code换取 Access Token
		Map<String,String> map = new HashMap<>();
		map.put("grant_type", "authorization_code");
		map.put("code", code);
		map.put("client_id", clientId);
		map.put("redirect_uri", "http://auth.mall.com/oauth2.0/gitee/success");
		map.put("client_secret", clientSecret);
		Map<String, String> headers = new HashMap<>();
		HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", headers, null, map);

		if(response.getStatusLine().getStatusCode() == 200){
			// 获取到了 Access Token
			String json = EntityUtils.toString(response.getEntity());
			SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

			// 相当于我们知道了当前是那个用户
			// 1.如果用户是第一次进来 自动注册进来(为当前社交用户生成一个会员信息 以后这个账户就会关联这个账号)
			R login = memberFeignService.login(socialUser);
			if(login.getCode() == 0){
				MemberRsepVo rsepVo = login.getData("data" ,new TypeReference<MemberRsepVo>() {});

				log.info("\n欢迎 [" + rsepVo.getUsername() + "] 使用社交账号登录");
				// 第一次使用session 命令浏览器保存这个用户信息 JESSIONSEID 每次只要访问这个网站就会带上这个cookie
				// 在发卡的时候扩大session作用域 (指定域名为父域名)
				// TODO 1.默认发的当前域的session (需要解决子域session共享问题)
				// TODO 2.使用JSON的方式序列化到redis
//				new Cookie("JSESSIONID","").setDomain("mall.com");
				session.setAttribute(AuthServerConstant.LOGIN_USER, rsepVo);
				// 登录成功 跳回首页
				return "redirect:http://mall.com";
			}else{
				return "redirect:http://auth.mall.com/login.html";
			}
		}else{
			return "redirect:http://auth.mall.com/login.html";
		}
	}
}
