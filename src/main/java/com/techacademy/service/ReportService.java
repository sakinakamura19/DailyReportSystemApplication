package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.EmployeeRepository;
import com.techacademy.repository.ReportRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

	private final ReportRepository reportRepository;

	@Autowired
	public ReportService(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;
	}

	@Transactional
	public Report saveReport(Report rep) {
		LocalDateTime now = LocalDateTime.now();
		rep.setCreatedAt(now);
		rep.setUpdatedAt(now);

		return reportRepository.save(rep);
	}

	// 日報保存
	@Transactional
	public ErrorKinds save(Report rep) {
		List<Report> listRep = reportRepository.findByEmployeeAndReportDate(rep.getEmployee(), rep.getReportDate());
		if (listRep.size() != 0) {
			return ErrorKinds.DUPLICATE_DATE_ERROR;
		}
		LocalDateTime now = LocalDateTime.now();
		rep.setCreatedAt(now);
		rep.setUpdatedAt(now);

		reportRepository.save(rep);
		return ErrorKinds.SUCCESS;

	}

	// 日報更新
	@Transactional
	public ErrorKinds update(Report rep, Integer id) {
		List<Report> listRep = reportRepository.findByEmployeeAndReportDateAndIdNot(rep.getEmployee(), rep.getReportDate(),rep.getId());
		if (listRep.size() != 0) {
			return ErrorKinds.DUPLICATE_DATE_ERROR;
		}
		//Report getReport= findById(id);
		LocalDateTime now = LocalDateTime.now();
		rep.setUpdatedAt(now);
		//rep.setCreatedAt(now);
		reportRepository.save(rep);

		return ErrorKinds.SUCCESS;

	}

	// 日報削除
	@Transactional
	public ErrorKinds delete(Integer id) {

		Report rep = findById(id);
		LocalDateTime now = LocalDateTime.now();
		rep.setUpdatedAt(now);
		rep.setDeleteFlg(true);
		reportRepository.save(rep);
		return ErrorKinds.SUCCESS;
	}

	// 日報一覧表示処理
	public List<Report> findAll() {
		return reportRepository.findAll();
	}

	// 1件を検索
	public Report findById(Integer id) {
		// findByIdで検索
		Optional<Report> option = reportRepository.findById(id);
		// 取得できなかった場合はnullを返す
		Report report = option.orElse(null);
		return report;
	}
	public List<Report> findByEmployee(Employee employee) {
		// findByIdで検索
		List<Report> report = reportRepository.findByEmployee(employee);
		// 取得できなかった場合はnullを返す
		return report;
	}

}
