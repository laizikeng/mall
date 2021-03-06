package com.scut.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scut.common.utils.PageUtils;
import com.scut.mall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author lzk
 * @email 2601665132@qq.com
 * @date 2021-08-04 21:37:24
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);

    List<BrandEntity> getBrandsByIds(List<Long> brandIds);
}

