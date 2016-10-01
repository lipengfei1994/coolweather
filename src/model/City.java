package model;

import android.R.integer;

public class City {
	private int id;
	private String cityName;
	private String cityCode;
	
	public int getId() {
		
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getcityName() {
		return cityName;
	}

	public void setcityName(String cityName) {
		this.cityName = cityName;
	}

	public String getcityCode() {
		return cityCode;
	}

	public void setcityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public City() {
		// TODO 自动生成的构造函数存根
	}
	
}
