package com.scut.mall.product.feign;


import com.scut.common.to.SkuReductionTO;
import com.scut.common.to.SpuBoundTO;
import com.scut.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mall-coupon")
public interface CouponFeignService {

	/**
	 *  @RequestBody 将对象转换为json 将上一步的json放在请求体位置 发送请求
	 *
	 *  对方服务收到请求。 收到的是请求体里的json数据  那边用 @RequestBody 对SpuBoundsEntity进行封装
	 *
	 *  只要 JSON 数据模型是兼容的 双方服务无需使用同一个 TO 对象
	 */
	@PostMapping("/coupon/spubounds/save")
	R saveSpuBounds(@RequestBody SpuBoundTO spuBoundTo);

	@PostMapping("/coupon/skufullreduction/saveinfo")
	R saveSkuReduction(@RequestBody SkuReductionTO skuReductionTo);
}
