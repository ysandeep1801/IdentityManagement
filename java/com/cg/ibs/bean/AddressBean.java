package com.cg.ibs.bean;

public class AddressBean {

	private String houseNumber;
	private String streetName;
	private String area;
	private String landmark;
	private String city;
	private String state;
	private String country;
	private String pincode;

	public AddressBean() {
		super();
	}

	public AddressBean(String houseNumber, String landmark, String area, String city, String state, String country,
			String pincode) {
		super();
		this.houseNumber = houseNumber;
		this.landmark = landmark;
		this.area = area;
		this.city = city;
		this.state = state;
		this.country = country;
		this.pincode = pincode;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\nHouse Number=");
		builder.append(houseNumber);
		builder.append("\nStreet Name=");
		builder.append(streetName);
		builder.append("\nLandmark=");
		builder.append(landmark);
		builder.append("\nArea=");
		builder.append(area);
		builder.append("\nCity=");
		builder.append(city);
		builder.append("\nState=");
		builder.append(state);
		builder.append("\nCountry=");
		builder.append(country);
		builder.append("\nPincode=");
		builder.append(pincode + "\n");
		return builder.toString();
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	
	
	
	

}
