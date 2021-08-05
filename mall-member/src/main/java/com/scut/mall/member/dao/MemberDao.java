package com.scut.mall.member.dao;

import com.scut.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author lzk
 * @email 2601665132@qq.com
 * @date 2021-08-05 14:53:20
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
