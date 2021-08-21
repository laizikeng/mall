package com.scut.mall.product.vo;

import com.scut.mall.product.entity.SkuImagesEntity;
import com.scut.mall.product.entity.SkuInfoEntity;
import com.scut.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {

	/**
	 * 基本信息
	 */
	SkuInfoEntity info;

	boolean hasStock = true;

	/**
	 * 图片信息
	 */
	List<SkuImagesEntity> images;

	/**
	 * 销售属性组合
	 */
	List<ItemSaleAttrVo> saleAttr;

	/**
	 * 介绍
	 */
	SpuInfoDescEntity desc;

	/**
	 * 参数规格信息
	 */
	List<SpuItemAttrGroup> groupAttrs;

	/**
	 * 秒杀信息
	 */
	SeckillInfoVo seckillInfoVo;
}
