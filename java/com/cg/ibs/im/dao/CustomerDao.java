package com.cg.ibs.im.dao;

import java.io.IOException;
import java.util.Set;

import com.cg.ibs.bean.CustomerBean;
import com.cg.ibs.im.exception.IBSCustomException;

public interface CustomerDao {
	
	
	boolean saveCustomer(CustomerBean customer) throws IBSCustomException;
	
	CustomerBean getCustomerDetails(String uci) throws IBSCustomException;
	
	Set<String> getAllCustomers();

	CustomerBean getCustomerByApplicantId(long applicantId);
	
	public boolean copy(String srcPath, String destPath) throws IOException;
	
}
