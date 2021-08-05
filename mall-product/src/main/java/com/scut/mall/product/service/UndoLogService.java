package com.scut.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scut.common.utils.PageUtils;
import com.scut.mall.product.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author lzk
 * @email 2601665132@qq.com
 * @date 2021-08-04 21:37:23
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

