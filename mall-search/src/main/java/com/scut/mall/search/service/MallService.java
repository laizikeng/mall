package com.scut.mall.search.service;


import com.scut.mall.search.vo.SearchParam;
import com.scut.mall.search.vo.SearchResult;

import java.io.IOException;


public interface MallService {

	/**
	 * 检索所有参数
	 */
	SearchResult search(SearchParam Param);
}
