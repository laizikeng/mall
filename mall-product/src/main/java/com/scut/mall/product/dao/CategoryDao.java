package com.scut.mall.product.dao;

import com.scut.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author lzk
 * @email 2601665132@qq.com
 * @date 2021-08-04 21:37:24
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
