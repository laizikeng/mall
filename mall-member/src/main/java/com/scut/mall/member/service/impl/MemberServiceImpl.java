package com.scut.mall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scut.common.utils.HttpUtils;
import com.scut.mall.member.dao.MemberLevelDao;
import com.scut.mall.member.entity.MemberLevelEntity;
import com.scut.mall.member.exception.PhoneExistException;
import com.scut.mall.member.exception.UserNameExistException;
import com.scut.mall.member.vo.MemberLoginVo;
import com.scut.mall.member.vo.SocialUser;
import com.scut.mall.member.vo.UserRegisterVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scut.common.utils.PageUtils;
import com.scut.common.utils.Query;

import com.scut.mall.member.dao.MemberDao;
import com.scut.mall.member.entity.MemberEntity;
import com.scut.mall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(UserRegisterVo userRegisterVo) {

        MemberEntity entity = new MemberEntity();
        // 设置默认等级
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        entity.setLevelId(memberLevelEntity.getId());

        // 检查手机号 用户名是否唯一
        checkPhone(userRegisterVo.getPhone());
        checkUserName(userRegisterVo.getUserName());

        entity.setMobile(userRegisterVo.getPhone());
        entity.setUsername(userRegisterVo.getUserName());

        // 密码要加密存储
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        entity.setPassword(bCryptPasswordEncoder.encode(userRegisterVo.getPassword()));
        // 其他的默认信息
        entity.setCity("湖南 长沙");
        entity.setCreateTime(new Date());
        entity.setStatus(0);
        entity.setNickname(userRegisterVo.getUserName());
        entity.setBirth(new Date());
        entity.setEmail("xxx@gmail.com");
        entity.setGender(1);
        entity.setJob("JAVA");

        baseMapper.insert(entity);
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // 去数据库查询
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("mobile", loginacct));
        if(entity == null){
            // 登录失败
            return null;
        }else{
            // 前面传一个明文密码 后面传一个编码后的密码
            boolean matches = bCryptPasswordEncoder.matches(vo.getPassword(), entity.getPassword());
            if (matches){
                entity.setPassword(null);
                return entity;
            }else {
                return null;
            }
        }
    }

    @Override
    public MemberEntity login(SocialUser socialUser) {
        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", socialUser.getAccessToken());
        JSONObject jsonObject = null;
        try {
            HttpResponse response = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap<>(), map);
            // 查询当前社交用户账号信息(昵称、性别等)
            if(response.getStatusLine().getStatusCode() == 200) {
                // 查询成功
                String json = EntityUtils.toString(response.getEntity());
                // 这个JSON对象什么样的数据都可以直接获取
                jsonObject = JSON.parseObject(json);
            } else{
                log.warn("社交登录时远程调用出错 [尝试修复]");
            }
        } catch (Exception e) {
            log.warn("社交登录时远程调用出错 [尝试修复]");
        }

        // 社交账号的id
        String uid = jsonObject.getString("id");

        // 1.判断社交用户登录过系统
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        MemberEntity memberEntity = new MemberEntity();
        if(entity != null) {
            // 说明这个用户注册过, 修改它的资料
            memberEntity.setId(entity.getId());
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpiresIn(socialUser.getExpiresIn());
            // 更新
            this.baseMapper.updateById(memberEntity);
            entity.setAccessToken(socialUser.getAccessToken());
            entity.setExpiresIn(socialUser.getExpiresIn());
            entity.setPassword(null);
            return entity;
        }else{
            // 没有查到当前社交用户对应的记录 我们就需要注册一个
            memberEntity.setNickname(jsonObject.getString("name"));
            memberEntity.setUsername(jsonObject.getString("name"));
            memberEntity.setGender(1);
            memberEntity.setCity("广州");
            memberEntity.setJob("自媒体");
            memberEntity.setEmail(jsonObject.getString("email"));
            memberEntity.setStatus(0);
            memberEntity.setCreateTime(new Date());
            memberEntity.setBirth(new Date());
            memberEntity.setLevelId(1L);
            memberEntity.setSocialUid(uid);
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpiresIn(socialUser.getExpiresIn());

            // 注册 -- 登录成功
            this.baseMapper.insert(memberEntity);
            memberEntity.setPassword(null);
            return memberEntity;
        }
    }

    public void checkPhone(String phone) throws PhoneExistException {
        if(this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone)) > 0){
            throw new PhoneExistException();
        }
    }

    public void checkUserName(String username) throws UserNameExistException {
        if(this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username)) > 0){
            throw new UserNameExistException();
        }
    }

}