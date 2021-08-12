package com.scut.mall.ware.vo;

import lombok.Data;
/**
 * Description：采购项
 */
@Data
public class PurchaseItemDoneVo {
	/**
	 * "itemId":1,"status":3,"reason":"",
	 * "itemId":3,"status":4,"reason":"无货"
	 */
	private Long itemId;

    private Integer status;

    private String reason;
}
