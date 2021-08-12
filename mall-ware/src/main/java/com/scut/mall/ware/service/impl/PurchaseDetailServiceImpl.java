package com.scut.mall.ware.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scut.common.utils.PageUtils;
import com.scut.common.utils.Query;

import com.scut.mall.ware.dao.PurchaseDetailDao;
import com.scut.mall.ware.entity.PurchaseDetailEntity;
import com.scut.mall.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w-> w.eq("purchase_id",key).or().eq("sku_id",key));
        }

        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("status", status);
        }
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id", wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据采购单id 改变采购项
     */
    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {
        // 获取所有采购项
        List<PurchaseDetailEntity> entities = this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
        return entities;
    }

}