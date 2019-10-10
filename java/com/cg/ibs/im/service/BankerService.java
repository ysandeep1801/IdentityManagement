package com.cg.ibs.im.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.cg.ibs.bean.AccountBean;
import com.cg.ibs.bean.ApplicantBean;
import com.cg.ibs.bean.ApplicantBean.ApplicantStatus;
import com.cg.ibs.im.exception.IBSCustomException;

public interface BankerService {
	boolean verifyLogin(String user, String password);

	Set<Long> viewPendingApplications();

	Set<Long> viewApprovedApplications();

	Set<Long> viewDeniedApplications();

	boolean updateStatus(long applicantId, ApplicantStatus applicantStatus) throws IBSCustomException;

	String generatePassword(long applicantId);

	AccountBean createNewAccount(String uci);

	String createNewCustomer(ApplicantBean applicant) throws IBSCustomException;

	boolean isApplicantPresentInPendingList(long applicantId);

	boolean isApplicantPresent(long applicantId);

	void downloadDocuments(StringBuilder sb) throws IOException;

	StringBuilder getDocuments() throws IOException, ClassNotFoundException;

	String displayDetails(long applicantId) throws IBSCustomException;

	String generateUsername(long applicantId) throws IBSCustomException;

	public boolean download(String destPath, String fileName) throws  IOException;

	public List<String> getFilesAvialable();
}
