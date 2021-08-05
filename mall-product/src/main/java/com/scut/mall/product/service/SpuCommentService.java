package com.scut.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scut.common.utils.PageUtils;
import com.scut.mall.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author lzk
 * @email 2601665132@qq.com
 * @date 2021-08-04 21:37:24
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

