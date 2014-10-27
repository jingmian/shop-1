/*
 * Copyright 2005-2013 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package com.fhd.controller.admin;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import com.fhd.Message;
import com.fhd.entity.BaseEntity;
import com.fhd.entity.MemberAttribute;
import com.fhd.Pageable;
import com.fhd.entity.Member;
import com.fhd.service.MemberAttributeService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 会员注册项
 * 
 * @author SHOP++ Team
 * @version 3.0
 */
@Controller("adminMemberAttributeController")
@RequestMapping("/admin/member_attribute")
public class MemberAttributeController extends BaseController {

	@Resource(name = "memberAttributeServiceImpl")
	private MemberAttributeService memberAttributeService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model, RedirectAttributes redirectAttributes) {
		if (memberAttributeService.count() - 8 >= Member.ATTRIBUTE_VALUE_PROPERTY_COUNT) {
			addFlashMessage(redirectAttributes, Message.warn("admin.memberAttribute.addCountNotAllowed", Member.ATTRIBUTE_VALUE_PROPERTY_COUNT));
		}
		return "/admin/member_attribute/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(MemberAttribute memberAttribute, RedirectAttributes redirectAttributes) {
		if (!isValid(memberAttribute, BaseEntity.Save.class)) {
			return ERROR_VIEW;
		}
		if (memberAttribute.getType() == MemberAttribute.Type.select || memberAttribute.getType() == MemberAttribute.Type.checkbox) {
			List<String> options = memberAttribute.getOptions();
			if (options != null) {
				for (Iterator<String> iterator = options.iterator(); iterator.hasNext();) {
					String option = iterator.next();
					if (StringUtils.isEmpty(option)) {
						iterator.remove();
					}
				}
			}
			if (options == null || options.isEmpty()) {
				return ERROR_VIEW;
			}
		} else if (memberAttribute.getType() == MemberAttribute.Type.text) {
			memberAttribute.setOptions(null);
		} else {
			return ERROR_VIEW;
		}
		Integer propertyIndex = memberAttributeService.findUnusedPropertyIndex();
		if (propertyIndex == null) {
			return ERROR_VIEW;
		}
		memberAttribute.setPropertyIndex(propertyIndex);
		memberAttributeService.save(memberAttribute);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("memberAttribute", memberAttributeService.find(id));
		return "/admin/member_attribute/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(MemberAttribute memberAttribute, RedirectAttributes redirectAttributes) {
		if (!isValid(memberAttribute)) {
			return ERROR_VIEW;
		}
		MemberAttribute pMemberAttribute = memberAttributeService.find(memberAttribute.getId());
		if (pMemberAttribute == null) {
			return ERROR_VIEW;
		}
		if (pMemberAttribute.getType() == MemberAttribute.Type.select || pMemberAttribute.getType() == MemberAttribute.Type.checkbox) {
			List<String> options = memberAttribute.getOptions();
			if (options != null) {
				for (Iterator<String> iterator = options.iterator(); iterator.hasNext();) {
					String option = iterator.next();
					if (StringUtils.isEmpty(option)) {
						iterator.remove();
					}
				}
			}
			if (options == null || options.isEmpty()) {
				return ERROR_VIEW;
			}
		} else {
			memberAttribute.setOptions(null);
		}
		memberAttributeService.update(memberAttribute, "type", "propertyIndex");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", memberAttributeService.findPage(pageable));
		return "/admin/member_attribute/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long[] ids) {
		memberAttributeService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}