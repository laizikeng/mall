package com.scut.mall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
/**
 * Description：采购单
 */
@Data
public class PurchaseDoneVo {

	/**
	 * 采购单id
	 */
    @NotNull
    private Long id;

    private List<PurchaseItemDoneVo> items;
}
