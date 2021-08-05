package com.scut.mall.coupon.dao;

import com.scut.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author lzk
 * @email 2601665132@qq.com
 * @date 2021-08-05 14:48:24
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
