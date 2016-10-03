package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

import model.City;
import model.CoolWeatherDB;
import model.Country;
import model.Province;
import android.drm.DrmManagerClient.OnErrorListener;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class HttpUtil {
	// 请求数据
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listen) {
		new Thread(new Runnable() {
			
			private HttpURLConnection connection;

			@Override
			public void run() {
				URL url = null;
				try {
					url = new URL(address);
					connection=(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					if (connection.getResponseCode()==200) {
						InputStream in= connection.getInputStream();
						BufferedReader reader=new BufferedReader(new InputStreamReader(in));
						StringBuilder response=new StringBuilder();
						String line;
						while( (line=reader.readLine())!=null){
							response.append(line);
						}
						if(listen!=null){
							//回调onFinsh方法
							listen.onFinish(response.toString());
						}
					}else {
						Log.i("error", "responcecode!=200");
					}
					
				} catch (MalformedURLException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			
				
			}
		}).start();
	}

	//
	public interface HttpCallbackListener {
		void onFinish(String response);

		void OnErrorListener(Exception exception);
	}
	/**
	 * 解析和初级服务器返回的省级数据
	 * @param coolWeatherDB 
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvince= response.split(",");
			if(allProvince!=null&&allProvince.length>0){
				for (String provinceinfo : allProvince) {
					String[] array= provinceinfo.split("\\|");
					Province province=new Province();
					province.setProvinceName(array[1]);
					province.setProvinceCode(array[0]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析处理服务器返回的城市数据
	 * @param coolWeatherDB
	 * @param response
	 * @param cityId
	 * @return
	 */
	public static boolean handleCitysResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities= response.split(",");
			if(allCities!=null&&allCities.length>0){
				for (String city : allCities) {
					String[] array= city.split("\\|");
					City c=new City();
					c.setcityCode(array[0]);
					c.setcityName(array[1]);
					
					c.setProvinceId(provinceId);
					coolWeatherDB.saveCity(c);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析和处理县级数据
	 * @param coolWeatherDB
	 * @param response
	 * @param countryid
	 * @return
	 */
	public static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCountris= response.split(",");
			if(allCountris!=null&&allCountris.length>0){
				for (String country : allCountris) {
					String[] array= country.split("\\|");
					
					Country c=new Country();
					c.setcountryCode(array[0]);
					c.setcountryName(array[1]);
					c.setCityId(cityId);
					coolWeatherDB.saveCountry(c);
				}
				return true;
			}
		}
		return false;
	}
	
}
