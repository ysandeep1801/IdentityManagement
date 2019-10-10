package com.cg.ibs.im.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.cg.ibs.bean.AddressBean;
import com.cg.ibs.bean.ApplicantBean;
import com.cg.ibs.bean.ApplicantBean.ApplicantStatus;
import com.cg.ibs.bean.ApplicantBean.Gender;
import com.cg.ibs.bean.CustomerBean;
import com.cg.ibs.im.exception.IBSCustomException;
import com.cg.ibs.im.service.BankerService;
import com.cg.ibs.im.service.BankerSeviceImpl;
import com.cg.ibs.im.service.CustomerService;
import com.cg.ibs.im.service.CustomerServiceImpl;

public class IdentityManagementUI {
	static Scanner scanner;

	private CustomerService customer = new CustomerServiceImpl();
	private BankerService banker = new BankerSeviceImpl();

	void init() {
		UserMenu choice = null;
		while (UserMenu.QUIT != choice) {
			System.out.println("------------------------");
			System.out.println("Choose your identity from MENU:");
			System.out.println("------------------------");
			for (UserMenu menu : UserMenu.values()) {
				System.out.println((menu.ordinal()) + 1 + "\t" + menu);
			}
			System.out.println("Choice");
			int ordinal = scanner.nextInt();

			if (0 < (ordinal) && UserMenu.values().length >= (ordinal)) {
				choice = UserMenu.values()[ordinal - 1];
				switch (choice) {
				case BANKER:
					try {
						selectBankerAction();
					} catch (Exception exception) {
						System.out.println(exception.getMessage());
					}
					break;
				case CUSTOMER:
					try {
						selectCustomerAction();
					} catch (Exception exception) {
						System.out.println(exception.getMessage());
					}
					break;
				case SERVICE_PROVIDER:
					selectSPAction(); // Group no. 6
					break;
				case QUIT:
					System.out.println("Application closed!!");
					break;
				}
			} else {
				System.out.println("Please enter a valid option.");
				choice = null;
			}

		}

	}

	public void selectBankerAction() {
		if (bankerLogin()) {
			BankerAction choice = null;
			System.out.println("------------------------");
			System.out.println("Choose a valid option");
			System.out.println("------------------------");
			for (BankerAction menu : BankerAction.values()) {
				System.out.println(menu.ordinal() + 1 + "\t" + menu);
			}
			System.out.println("Choices:");
			int ordinal = scanner.nextInt();

			if (0 < ordinal && BankerAction.values().length >= ordinal) {
				choice = BankerAction.values()[ordinal - 1];
				switch (choice) {
				case VIEW_PENDING_DETAILS:
					try {
						pendingApplications();
					} catch (Exception exception) {
						System.out.println(exception.getMessage());
					}
					break;
				case VIEW_APPROVED_DETAILS:
					approvedApplications();
					break;
				case VIEW_DENIED_DETAILS:
					deniedApplications();
					break;
				case QUIT:
					System.out.println("BACK ON HOME PAGE!!");
					break;
				}
			} else {
				System.out.println("Please enter a valid option.");
				choice = null;
			}
		}
	}

	public void selectCustomerAction() {
		CustomerMenu choice = null;
		System.out.println("------------------------");
		System.out.println("Choose an appropriate option from MENU:");
		System.out.println("------------------------");
		for (CustomerMenu menu : CustomerMenu.values()) {
			System.out.println(menu.ordinal() + 1 + "\t" + menu);
		}
		System.out.println("Choice");
		int ordinal = scanner.nextInt();

		if (0 < ordinal && UserMenu.values().length >= ordinal) {
			choice = CustomerMenu.values()[ordinal - 1];
			switch (choice) {
			case SIGNUP:
				try {
					signUp();
				} catch (Exception exception) {
					System.out.println(exception.getMessage());
				}
				break;
			case LOGIN:
				try {
					login();
				} catch (Exception exception) {
					System.out.println(exception.getMessage());
				}
				break;
			case CHECK_STATUS:
				try {
					checkStatus();
				} catch (Exception exception) {
					System.out.println(exception.getMessage());
				}
				break;
			case GO_BACK:
				System.out.println();
				break;
			}
		} else {
			System.out.println("Please enter a valid option.");
			choice = null;
		}

	}

	public void selectSPAction() {
		System.out.println("Under Maintainence. (Use-Case 4!)");
	}

	void pendingApplications() {
		Set<Long> pendingList = banker.viewPendingApplications();
		if (pendingList.size() > 0) {
			System.out.println("The list of the pending applicants is here:");
			Iterator<Long> iterator = pendingList.iterator();
			while (iterator.hasNext()) {
				System.out.println(iterator.next());
			}
		} else {
			System.out.println("There are no pending applicant requests.");
		}

		System.out.println("Enter an application number to check details:");
		long applicantId = scanner.nextLong();
		//
		while (!banker.isApplicantPresentInPendingList(applicantId)) {
			System.out.println("applicant is not present. Please enter a valid id:");
			applicantId = scanner.nextLong();
		}
		int index = -1;
		try {
			System.out.println(banker.displayDetails(applicantId));
			List<String> fnms = banker.getFilesAvialable();
			for (int i = 0; i < fnms.size(); i++) {
				System.out.println(i + "\t" + fnms.get(i));
			}
			do {
				System.out.println("Enter file index to download: ");
				index = scanner.nextInt();
				if (index < fnms.size()) {
					System.out.println("Enter a download folder loc: ");
					String dwnLoc = scanner.next();
					banker.download(dwnLoc, fnms.get(index));
					break;
				} else {
					System.out.println(index);
					System.out.println("File doesn't exist");
				}
			} while (index >= fnms.size());

		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

		System.out.println("------------------------");
		System.out.println("Choose valid option:");
		System.out.println("------------------------");
		System.out.println("1.\tApprove application");
		System.out.println("2.\tDeny application");
		int choice = scanner.nextInt();
		while (choice != 1 && choice != 2) {
			System.out.println("Please enter a valid choice");
			choice = scanner.nextInt();
		}

		if (choice == 1) {
			try {
				ApplicantBean applicant = customer.getApplicantDetails(applicantId);
				applicant.setApplicantStatus(ApplicantStatus.APPROVED);
				customer.storeApplicantDetails(applicant);
				String uci = banker.createNewCustomer(applicant);
				System.out.println("The status has been approved for the applicant.\nCustomer ID: " + uci + "\n");
			} catch (Exception exception) {
				System.out.println(exception.getMessage());
			}

		} else if (choice == 2) {
			try {
				banker.updateStatus(applicantId, ApplicantStatus.DENIED);
			} catch (Exception exception) {
				System.out.println(exception.getMessage());
			}
		}
	}

	void approvedApplications() {
		Set<Long> approvedList = banker.viewApprovedApplications();
		if (approvedList.size() > 0) {
			Iterator<Long> iterator = approvedList.iterator();
			while (iterator.hasNext()) {
				System.out.println(iterator.next());
			}
		} else {
			System.out.println("There are no approved applications.");
		}
	}

	void deniedApplications() {
		Set<Long> deniedList = banker.viewDeniedApplications();
		if (deniedList.size() > 0) {
			Iterator<Long> iterator = deniedList.iterator();
			while (iterator.hasNext()) {
				System.out.println(iterator.next());
			}
		} else {
			System.out.println("There are no denied applications.");
		}
	}

	public void signUp() {

		ApplicantBean applicant = new ApplicantBean();
		System.out.println("Enter the following Details:");

		BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter the first name");
		try {
			String firstName = keyboardInput.readLine();
			while (!customer.verifyName(firstName)) {
				System.out.println("Please enter an appropriate name");
				firstName = keyboardInput.readLine();
			}
			applicant.setFirstName(firstName);
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

		System.out.println("Enter the last name");
		try {
			String lastName = keyboardInput.readLine();
			while (!customer.verifyName(lastName)) {
				System.out.println("Please enter an appropriate name");
				lastName = keyboardInput.readLine();
			}
			applicant.setLastName(lastName);
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

		System.out.println("Enter Father's name");
		try {
			String fatherName = keyboardInput.readLine();
			while (!customer.verifyName(fatherName)) {
				System.out.println("Please enter an appropriate name");
				fatherName = keyboardInput.readLine();
			}
			applicant.setFatherName(fatherName);
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

		System.out.println("Enter Mother's name");
		try {
			String motherName = keyboardInput.readLine();
			while (!customer.verifyName(motherName)) {
				System.out.println("Please enter an appropriate name");
				motherName = keyboardInput.readLine();
			}
			applicant.setMotherName(motherName);
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

		System.out.println("Enter your Date of Birth in DD-MM-YYYY format");
		String date = scanner.next();
		// if format of date is invalid, ask for DOB again.
		DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate dt = LocalDate.parse(date, dtFormat);
		while (!customer.verifyDob(dt)) {
			System.out.println("Please enter a valid date of birth. Your age should be greater than 18!");
			date = scanner.next();
			dt = LocalDate.parse(date, dtFormat);
		}
		applicant.setDob(dt);

		// Enter Gender
		Gender genderChoice = null;
		while (genderChoice == null) {
			System.out.println("Choose your gender from the menu:");
			for (Gender menu : Gender.values()) {
				System.out.println(menu.ordinal() + 1 + "\t" + menu);
			}
			System.out.println("Choice");
			int gender = scanner.nextInt();

			if (0 < gender && Gender.values().length >= gender) {
				genderChoice = Gender.values()[gender - 1];
				switch (genderChoice) {
				case MALE:
					applicant.setGender(Gender.MALE);
					break;
				case FEMALE:
					applicant.setGender(Gender.FEMALE);
					break;
				case OTHERS:
					applicant.setGender(Gender.OTHERS);
					break;
				}
			} else {
				System.out.println("Please enter a valid option.");
				genderChoice = null;
			}
		}

		// Permanent Address
		AddressBean address = addAddress();
		applicant.setPermanentAddress(address);

		// Current Address
		System.out.println("Is your current address same as permanent address?\n1. yes\n2. no");
		int addressSame = scanner.nextInt();
		while (addressSame != 1 && addressSame != 2) {
			System.out.println("Please enter a valid choice. Is your current address same as"
					+ " permanent address?\1. yes\n2. no");
			addressSame = scanner.nextInt();
		}

		if (addressSame == 1) {
			applicant.setCurrentAddress(address);
		} else if (addressSame == 2) {
			address = addAddress();
			applicant.setCurrentAddress(address);
		}

		System.out.println("Enter Mobile number");
		String mobileNumber = scanner.next();
		while (!customer.verifyMobileNumber(mobileNumber)) {
			System.out.println("Please enter an appropriate phone number");
			mobileNumber = scanner.next();
		}
		applicant.setMobileNumber(mobileNumber);

		System.out.println("Enter Alternate Mobile Number");
		String alternateMobileNumber = scanner.next();
		while (!customer.verifyMobileNumber(alternateMobileNumber)) {
			System.out.println("Please enter an appropriate phone number");
			alternateMobileNumber = scanner.next();
		}

		while (customer.verifyMobileNumbers(mobileNumber, alternateMobileNumber)) {
			System.out.println("Alternate mobile number can't be the same as primary mobile number");
			alternateMobileNumber = scanner.next();
			while (!customer.verifyMobileNumber(alternateMobileNumber)) {
				System.out.println("Please enter an appropriate phone number");
				alternateMobileNumber = scanner.next();
			}
		}
		applicant.setAlternateMobileNumber(alternateMobileNumber);

		System.out.println("Enter email id");
		String emailId = scanner.next();
		while (!customer.verifyEmailId(emailId)) {
			System.out.println("Please enter an appropriate email Id");
			emailId = scanner.next();
		}
		applicant.setEmailId(emailId);

		System.out.println("Enter Aadhar Number");
		try {
			String aadharNumber = keyboardInput.readLine();
			while (!customer.verifyAadharNumber(aadharNumber)) {
				System.out.println("Please enter an appropriate aadhar number");
				aadharNumber = keyboardInput.readLine();
			}
			applicant.setAadharNumber(aadharNumber);
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

		System.out.println("Enter Pan Number");
		try {
			String panNumber = keyboardInput.readLine();
			while (!customer.verifyPanNumber(panNumber)) {
				System.out.println("Please enter an appropriate PAN number");
				panNumber = keyboardInput.readLine();
			}
			applicant.setPanNumber(panNumber);
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

		System.out.println("Upload two Government ID proofs\n");

		System.out.println("File path to upload file: ");
		String filePath;
		try {
			int test1;
			do {
				test1 = 0;
				System.out.println("Enter a valid path");
				filePath = keyboardInput.readLine();
				if (filePath.length() == 0) {
					test1 = 1;
				}
			} while (1 == test1);
			customer.upload(filePath);
			System.out.println("Uploaded ");
			System.out.println("Your application has been sent to the bank.");
			applicant.setApplicationDate(LocalDate.now());
			applicant.setApplicantStatus(ApplicantStatus.PENDING);
			try {
				customer.saveApplicantDetails(applicant);
			} catch (IBSCustomException e) {
				e.printStackTrace();
			}
			System.out.println(
					"Keep updated with your status.\nYour applicant " + "id is :" + applicant.getApplicantId());

		} catch (IOException e) {
			System.out.println("Please enter the valid path");
		}

	}

	public AddressBean addAddress() {
		BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter your permanent address:");
		AddressBean address = new AddressBean();

		try {
			System.out.println("House Number:");
			String houseNumber = keyboardInput.readLine();
			while (houseNumber.length() == 0) {
				System.out.println("Please enter an appropriate House Number ");
				houseNumber = keyboardInput.readLine();
			}
			address.setHouseNumber(houseNumber);

			System.out.println(" Street Name:");
			String streetName = keyboardInput.readLine();
			while (streetName.length() == 0) {
				System.out.println("Please enter an appropriate Street Name");
				streetName = keyboardInput.readLine();
			}
			address.setStreetName(streetName);

			System.out.println("Landmark:");
			String landMark = keyboardInput.readLine();
			while (landMark.length() == 0) {
				System.out.println("Please enter an appropriate Land Mark");
				landMark = keyboardInput.readLine();
			}
			address.setLandmark(landMark);

			System.out.println("Area:");
			String area = keyboardInput.readLine();
			while (area.length() == 0) {
				System.out.println("Please enter an appropriate Area");
				area = keyboardInput.readLine();
			}
			address.setArea(area);

			System.out.println("City:");
			String city = keyboardInput.readLine();
			while (city.length() == 0) {
				System.out.println("Please enter an appropriate City");
				city = keyboardInput.readLine();
			}
			address.setCity(city);

			System.out.println("State:");
			String state = keyboardInput.readLine();
			while (state.length() == 0) {
				System.out.println("Please enter an appropriate State");
				state = keyboardInput.readLine();
			}
			address.setState(state);

			System.out.println("Country:");
			String country = keyboardInput.readLine();
			while (country.length() == 0) {
				System.out.println("Please enter an appropriate Country");
				country = keyboardInput.readLine();
			}
			address.setCountry(country);

			System.out.println("Pincode:");
			String pinCode = keyboardInput.readLine();
			while (!customer.verifyPincode(pinCode)) {
				System.out.println("Please enter an appropriate pincode");
				pinCode = keyboardInput.readLine();
			}
			address.setPincode(pinCode);
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return address;
	}

	public void login() {

		System.out.println("Please enter the UCI to login");
		String userUci = scanner.next();
		System.out.println("Enter the password");
		String password = scanner.next();
		try {
			if (customer.login(userUci, password)) {
				if (customer.firstLogin(userUci)) {
					firstLogin(userUci, password);

				}
				System.out.println("Welcome to the Home Page!!");
			} else {
				System.out.println("INVALID DETAILS! Enter the details again.");
				login();
			}
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
	}

	public void firstLogin(String userUci, String password) {

		System.out.println("Reset your username");
		String userId = scanner.next();
		while (!customer.checkCustomerDetails(userId, userId)) {
			System.out.println("User Ids don't match.");
			System.out.println("Enter username again");
			userId = scanner.next();

		}
		try {
			CustomerBean newCustomer = customer.getCustomerDetails(userUci);
			if (customer.updateUserId(newCustomer, userId)) {
				System.out.println("User Id updated");

			}

			System.out.println("Reset your password");
			String userPassword = scanner.next();
			System.out.println("Confirm password");
			String confirmPassword = scanner.next();
			while (!customer.checkCustomerDetails(userPassword, confirmPassword)) {
				System.out.println("Passwords don't match.");
				System.out.println("Enter password again");
				userPassword = scanner.next();
				System.out.println("Confirm the password");
				confirmPassword = scanner.next();
			}
			try {
				customer.updatePassword(newCustomer, confirmPassword);
				System.out.println("Password updated");
			} catch (Exception exception) {
				System.out.println(exception.getMessage());
			}

		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
	}

	public void checkStatus() {
		System.out.println("Enter the applicant ID to check status:");
		long applicantId = scanner.nextLong();
		try {
			while (!customer.verifyApplicantId(applicantId)) {
				System.out.println("Please enter a valid applicant ID");
				applicantId = scanner.nextLong();
			}
			ApplicantStatus status = customer.checkStatus(applicantId);
			System.out.println("Your application status is: " + status);

			if (status == ApplicantStatus.APPROVED) {
				CustomerBean newCustomer = customer.getCustomerByApplicantId(applicantId);
				String uci = newCustomer.getUci();
				String userId = newCustomer.getUserId();
				String password = newCustomer.getPassword();
				System.out.println("Login using the following details:");
				System.out.println("\nUCI: " + uci);
				System.out.println("\nUser ID: " + userId);
				System.out.println("\nPassword: " + password);
			}
		} catch (Exception exception) {
			System.out.println(exception.getStackTrace());
		}

	}

	public boolean bankerLogin() {
		System.out.println("Enter a login ID:");
		String bankUser = scanner.next();
		System.out.println("Enter password:");
		String bankPassword = scanner.next();
		if (banker.verifyLogin(bankUser, bankPassword)) {
			System.out.println("Logged in!");
		} else {
			System.out.println("Please enter valid details");
			bankerLogin();
		}
		return true;
	}

	public static void main(String[] args) {

		scanner = new Scanner(System.in);
		IdentityManagementUI identityManagement = new IdentityManagementUI();
		identityManagement.init();
		scanner.close();

	}
}
