package com.suct.mall.thirdparty.controller;


import com.scut.common.exception.BizCodeEnum;
import com.scut.common.utils.R;
import com.suct.mall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/sms")
public class SmsSendController {

	@Autowired
	private SmsComponent smsComponent;

	@GetMapping("/sendcode")
	public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){
		if(!"fail".equals(smsComponent.sendSmsCode(phone, code))){
			System.out.println("a");
			return R.ok();
		}
		return R.error(BizCodeEnum.SMS_SEND_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_SEND_CODE_EXCEPTION.getMsg());
	}
}
