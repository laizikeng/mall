package com.scut.mall.auth.feign;

import com.scut.common.utils.R;
import com.scut.mall.auth.vo.SocialUser;
import com.scut.mall.auth.vo.UserLoginVo;
import com.scut.mall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mall-member")
public interface MemberFeignService {

	@PostMapping("/member/member/register")
	R register(@RequestBody UserRegisterVo userRegisterVo);

	@PostMapping("/member/member/login")
	R login(@RequestBody UserLoginVo vo);

	@PostMapping("/member/member/oauth2/login")
	R login(@RequestBody SocialUser socialUser);
}
