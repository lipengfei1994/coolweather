package model;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.DownloadManager.Query;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {

	// 构造方法私有化:别的类不能调用该构造方法
	private CoolWeatherDB(Context context) {
		// TODO 自动生成的构造函数存根
	}

	/**
	 * 数据库的名字
	 */
	public static final String DB_NAME = "cool_weather";
	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	// 将coolweatherdb定义为了单例类
	/**
	 * 获得coolweatherdb的单例实例
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;

	}

	/**
	 * 保存province到数据库
	 * 
	 * @param province
	 */
	public void saveProvince(Province province) {
		ContentValues values = new ContentValues();
		values.put("province_name", province.getProvinceName());
		values.put("province_code", province.getProvinceCode());
		db.insert("Province", null, values);
	}

	/**
	 * 保存city到数据库
	 * 
	 * @param province
	 */
	public void saveProvince(City city) {
		ContentValues values = new ContentValues();
		values.put("city_name", city.getcityName());
		values.put("city_code", city.getcityCode());
		db.insert("City", null, values);
	}

	/**
	 * 保存country到数据库
	 * 
	 * @param province
	 */
	public void saveProvince(Country country) {
		ContentValues values = new ContentValues();
		values.put("country_name", country.getcountryName());
		values.put("country_code", country.getcountryCode());
		db.insert("Country", null, values);
	}

	/**
	 * 从数据库中读取全国省的列表
	 * 
	 * @return
	 */
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Province province = new Province();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		while (cursor.moveToNext()) {

			province.setId(cursor.getInt(cursor.getColumnIndex("id")));
			province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
			province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
			list.add(province);
		}
		if(cursor!=null){
			cursor.close();//释放cursor中的资源
		}
		return list;
		

	}
	/**
	 * 查看某个province下的城市的列表
	 * @return
	 */
	public List<City> loadCities(int provinceId){
		List<City> list=new ArrayList<City>();
		City city=new City();
		Cursor cursor= db.query("City", null, "province_id=?", new String[]{}, null, null, null);
		while (cursor.moveToNext()) {
			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			city.setcityName(cursor.getString(cursor.getColumnIndex("city_name")));
			city.setcityCode(cursor.getString(cursor.getColumnIndex("city_code")));
			list.add(city);
			
		}
		if(cursor!=null){
			cursor.close();//释放cursor中的资源
		}
		return list;
	}
	/**
	 * 查看某个city下的country的列表
	 * @param cityId
	 * @return
	 */
	public List<Country> loadCountries(int cityId){
		List<Country> list=new ArrayList<Country>();
		Country country=new Country();
		Cursor cursor= db.query("Country", null, "city_id=?", new String[]{}, null, null, null);
		while (cursor.moveToNext()) {
			country.setId(cursor.getInt(cursor.getColumnIndex("id")));
			country.setcountryCode( cursor.getString(cursor.getColumnIndex("country_code")));
			country.setcountryName(cursor.getString(cursor.getColumnIndex("country_name")));
			list.add(country);
			
		}
		if(cursor!=null){
			cursor.close();//释放cursor中的资源
		}
		return list;
	}

}
