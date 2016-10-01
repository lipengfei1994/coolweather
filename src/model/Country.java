package model;

import android.R.integer;

public class Country {
	private int id;
	private String countryName;
	private String countryCode;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getcountryName() {
		return countryName;
	}

	public void setcountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getcountryCode() {
		return countryCode;
	}

	public void setcountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Country() {
		// TODO 自动生成的构造函数存根
	}
	
}
