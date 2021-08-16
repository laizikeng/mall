package com.scut.mall.product.web;

import com.scut.mall.product.entity.CategoryEntity;
import com.scut.mall.product.service.CategoryService;
import com.scut.mall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
	@Autowired
	CategoryService categoryService;

	@RequestMapping({"/", "index", "/index.html"})
	public String indexPage(Model model) {
		List<CategoryEntity> categorys = categoryService.getLevel1Categorys();
		model.addAttribute("categorys", categorys);
		return "index";
	}

	@ResponseBody
	@RequestMapping("/index/catalog.json")
	public Map<String, List<Catelog2Vo>> getCatlogJson() {

		Map<String, List<Catelog2Vo>> map = categoryService.getCatelogJson();
		return map;
	}
}
