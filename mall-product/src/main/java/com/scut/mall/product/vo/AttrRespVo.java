package com.scut.mall.product.vo;

import lombok.Data;

@Data
public class AttrRespVo extends AttrVo {
    /**
     * 所属分类名字
     */
    private String catelogName;
    /**
     * 所属分组名字
     */
    private String groupName;
    /**
     * 修改回显的完整分类名字
     */
    private Long[] catelogPath;
}
