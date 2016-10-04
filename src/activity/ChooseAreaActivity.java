package activity;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import util.HttpUtil;
import util.HttpUtil.HttpCallbackListener;

import com.test.coolweather.R;

import model.City;
import model.CoolWeatherDB;
import model.Country;
import model.Province;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> datalist = new ArrayList<String>();

	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	/**
	 * 市列表
	 */
	private List<City> cityList;
	/**
	 * 县级列表
	 */
	private List<Country> countryList;

	private Province selectedProvince;
	private City selectedCity;
	/**
	 * 别选中的级别
	 */
	private int currentLevel;
	/**
	 * 是否是从weatherActivity跳过来的
	 */
	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (sp.getBoolean("city_selected", false)&&!isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, datalist);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCity();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCountries();
				} else if (currentLevel == LEVEL_COUNTRY) {
					String countryCode = countryList.get(position)
							.getcountryCode();
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("country_code", countryCode);
					startActivity(intent);
					finish();
				}

			}

		});
		queryProvinces();// 加载省级数据
	}

	/**
	 * 查询选中市下的所有县，优先从数据库中查，数据库中没有再从服务器去查询
	 */
	private void queryCountries() {
		countryList = coolWeatherDB.loadCountries(selectedCity.getId());
		if (countryList.size() > 0) {
			datalist.clear();
			for (Country country : countryList) {
				datalist.add(country.getcountryName());
			}
			// 适配器更改时刷新listview
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getcityName());
			currentLevel = LEVEL_COUNTRY;
		} else {
			queryFromSever(selectedCity.getcityCode(), "country");
		}

	}

	/**
	 * 查询选中省下的所有市，优先从数据库中查，数据库中没有再从服务器去查询
	 */
	private void queryCity() {

		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			datalist.clear();
			for (City city : cityList) {
				datalist.add(city.getcityName());
			}
			// 适配器更改时刷新listview
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromSever(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * 查询全国所有省，优先从数据库中查，数据库中没有再从服务器去查询
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			datalist.clear();
			for (Province province : provinceList) {
				datalist.add(province.getProvinceName());
			}
			// 适配器更改时刷新listview
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromSever(null, "province");
		}

	}

	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 * 
	 * @param object
	 * @param string
	 */
	private void queryFromSever(String code, final String type) {
		String address;
		if (TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO 自动生成的方法存根
				boolean result = false;
				if ("province".equals(type)) {
					result = HttpUtil.handleProvincesResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = HttpUtil.handleCitysResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("country".equals(type)) {
					result = HttpUtil.handleCountriesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				// 处理字符串完毕，并且将它保存到数据库中之后，修改对话框
				if (result) {
					// 通过runOnUIThread（）方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCity();
							} else if ("country".equals(type)) {
								queryCountries();
							}
						}
					});
				}
			}

			@Override
			public void OnErrorListener(Exception exception) {
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", 0);
					}
				});

			}
		});

	}

	/**
	 * 显示对话框
	 */
	private void showProgressDialog() {
		// TODO 自动生成的方法存根
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			// 点击对话框以外的地方不可以取消对话框
			progressDialog.setCanceledOnTouchOutside(false);

		}
		progressDialog.show();
	}

	/**
	 * 关闭对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see android.app.Activity#onBackPressed() 判断是回到上一层，还是关闭应用
	 */
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTRY) {
			queryCity();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			if (isFromWeatherActivity) {
				Intent intent=new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}
