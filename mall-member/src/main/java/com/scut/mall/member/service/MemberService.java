package com.scut.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scut.common.utils.PageUtils;
import com.scut.mall.member.entity.MemberEntity;
import com.scut.mall.member.vo.MemberLoginVo;
import com.scut.mall.member.vo.SocialUser;
import com.scut.mall.member.vo.UserRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author lzk
 * @email 2601665132@qq.com
 * @date 2021-08-05 14:53:20
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(UserRegisterVo userRegisterVo);

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser socialUser);
}

