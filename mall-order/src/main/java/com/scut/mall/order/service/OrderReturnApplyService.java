package com.scut.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scut.common.utils.PageUtils;
import com.scut.mall.order.entity.OrderReturnApplyEntity;

import java.util.Map;

/**
 * 订单退货申请
 *
 * @author lzk
 * @email 2601665132@qq.com
 * @date 2021-08-05 15:04:06
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

