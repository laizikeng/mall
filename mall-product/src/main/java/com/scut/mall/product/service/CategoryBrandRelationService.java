package com.scut.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scut.common.utils.PageUtils;
import com.scut.mall.product.entity.BrandEntity;
import com.scut.mall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author lzk
 * @email 2601665132@qq.com
 * @date 2021-08-04 21:37:24
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(Long brandId, String name);

    void updateCategory(Long catId, String name);

    List<BrandEntity> getBrandsByCatId(Long catId);
}

