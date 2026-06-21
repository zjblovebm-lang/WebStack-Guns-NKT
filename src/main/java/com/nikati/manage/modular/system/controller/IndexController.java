/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nikati.manage.modular.system.controller;

import cn.stylefeng.roses.core.base.controller.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nikati.manage.core.common.node.MenuNode;
import com.nikati.manage.modular.system.model.Category;
import com.nikati.manage.modular.system.service.INoticeService;
import com.nikati.manage.modular.system.service.impl.CategoryServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author jsnjfz
 * @Date 2019/7/25 21:23 首页控制器
 */
@Controller
public class IndexController extends BaseController {

	@Autowired
	private CategoryServiceImpl categoryService;

	@Autowired
	private INoticeService noticeService;

	/**
	 * 跳转到首页
	 */
	@RequestMapping("/")
	public String index(Model model) {
		List<MenuNode> menus = categoryService.getCatogryNode(new HashMap<>());
		List<MenuNode> titles = MenuNode.buildTitle(menus);
		List<Category> categorySiteList = categoryService.getCatogrySite(null);
		List<Map<String, Object>> notices = noticeService.list(null);
		model.addAttribute("categorySiteList", categorySiteList);
		model.addAttribute("titles", titles);
		model.addAttribute("serviceCards", buildServiceCards());
		model.addAttribute("stats", buildStats(categorySiteList));
		model.addAttribute("noticeList", notices);
		return "/index.html";
	}

	@RequestMapping("/search/{wd}")
	public String s(Model model, @PathVariable(value = "wd") String wd) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("title", wd);
		List<MenuNode> menus = categoryService.getCatogryNode(map);
		List<MenuNode> titles = MenuNode.buildTitle(menus);
		List<Category> categorySiteList = categoryService.getCatogrySiteByinfo(map);
		List<Category> resultList = new ArrayList<Category>();
		for (Category category : categorySiteList) {
			if (null != category.getSites() && category.getSites().size() != 0) {
				resultList.add(category);
			}
		}
		model.addAttribute("categorySiteList", resultList);
		model.addAttribute("titles", titles);
		model.addAttribute("serviceCards", buildServiceCards());
		model.addAttribute("stats", buildStats(resultList));
		model.addAttribute("noticeList", noticeService.list(null));
		return "/index.html";
	}

	/**
	 * 跳转到关于页面
	 */
	@RequestMapping("/about")
	public String about(Model model) {
		return "/about.html";
	}

	private List<Map<String, String>> buildServiceCards() {
		List<Map<String, String>> cards = new ArrayList<>();
		cards.add(serviceCard("fa-rocket", "TikTok 实战陪跑", "从账号搭建、内容策划到直播复盘，提供跨境团队全流程陪跑服务。", "立即咨询"));
		cards.add(serviceCard("fa-signal", "直播网络方案", "独享 IP、低延迟中转、直播间组网，保障海外推流稳定顺滑。", "查看方案"));
		cards.add(serviceCard("fa-shopping-cart", "跨境设备商城", "软路由、直播网络设备、运营工具等一站式采购与交付。", "浏览设备"));
		cards.add(serviceCard("fa-globe", "独立站搭建", "品牌官网、销售落地页、电商商城快速上线，支持多语言和广告追踪。", "开始搭建"));
		return cards;
	}

	private Map<String, String> serviceCard(String icon, String title, String description, String action) {
		Map<String, String> card = new HashMap<>();
		card.put("icon", icon);
		card.put("title", title);
		card.put("description", description);
		card.put("action", action);
		return card;
	}

	private Map<String, Integer> buildStats(List<Category> categories) {
		Map<String, Integer> stats = new HashMap<>();
		int siteCount = 0;
		int categoryCount = 0;
		if (categories != null) {
			for (Category category : categories) {
				if (category.getSites() != null && category.getSites().size() > 0) {
					categoryCount++;
					siteCount += category.getSites().size();
				}
			}
		}
		stats.put("categoryCount", categoryCount);
		stats.put("siteCount", siteCount);
		stats.put("serviceCount", 4);
		return stats;
	}

}
