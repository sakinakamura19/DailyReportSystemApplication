package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("report")
public class ReportController {

	private final ReportService reportService;

	@Autowired
	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	// 日報一覧画面
	@GetMapping
	public String list(Model model) {

		model.addAttribute("listSize", reportService.findAll().size());
		model.addAttribute("reportList", reportService.findAll());

		return "report/list";
	}

	// 日報詳細画面
	@GetMapping(value = "/{id}/")
	public String detail(@PathVariable Integer id, Model model) {

		model.addAttribute("rep", reportService.findById(id));
		return "report/detail";
	}

	// 日報新規登録画面
	@GetMapping(value = "/add")
	public String create(@AuthenticationPrincipal UserDetail ud, @ModelAttribute Report rep) {
		rep.setEmployee(ud.getEmployee());
		return "report/new";
	}

	// 日報登録処理
	@PostMapping(value = "/add")
	public String add(@Validated Report rep, BindingResult res, Model model, @AuthenticationPrincipal UserDetail ud) {

		// 入力チェック
		if (res.hasErrors()) {
			return create(ud, rep);
		}
		rep.setEmployee(ud.getEmployee());
		ErrorKinds result = reportService.save(rep);

		if (ErrorMessage.contains(result)) {
			model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
			return create(ud, rep);
		}

		return "redirect:/report";
	}

	// 日報削除処理
	@PostMapping(value = "/{id}/delete")
	public String delete(@PathVariable Integer id, Model model) {

		ErrorKinds result = reportService.delete(id);

		return "redirect:/report";
	}

	// 日報更新画面
	@GetMapping(value = "/{id}/update")
	public String update(@PathVariable("id") Integer id, Report rep, Model model,@AuthenticationPrincipal UserDetail ud) {
		rep.setEmployee(ud.getEmployee());
		if (id != null) {
			model.addAttribute("report", reportService.findById(id));
		} else {
			model.addAttribute("report", rep);
		}
		return "report/update";
	}

	// 日報更新処理
	@PostMapping(value = "/{id}/update")
	public String saveReport(@Validated Report rep, BindingResult res,@PathVariable("id") Integer id,  Model model,@AuthenticationPrincipal UserDetail ud) {
		if (res.hasErrors()) {
			return update(null, rep, model,ud);
		}
		rep.setEmployee(ud.getEmployee());

		boolean passcheck = true;

		ErrorKinds result = reportService.update(rep,id);
		if (ErrorMessage.contains(result)) {
			model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
			return update(null, rep, model,ud);
		}
		return "redirect:/report";
}
}
