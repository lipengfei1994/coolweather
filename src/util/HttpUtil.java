package util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

import org.apache.http.HttpConnection;

import model.City;
import model.CoolWeatherDB;
import model.Country;
import model.Province;
import android.R.integer;
import android.drm.DrmManagerClient.OnErrorListener;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class HttpUtil {
	// 请求数据
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listen) {
		new Thread(new Runnable() {
			
			

			@Override
			public void run() {
				HttpURLConnection connection=null;
				try {
					URL url = new URL(address);
					connection=(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					
					//connection.setRequestProperty("contentType","UTF-8");
					if (connection.getResponseCode()==200) {
						InputStream in= connection.getInputStream();
						
//						StringBuilder response=new StringBuilder();
//						
//						BufferedReader reader=new BufferedReader(new InputStreamReader(in));
//						
//						String s=null;
//						while( (s=reader.readLine())!=null){
//							response.append(s);
//						}
						
						byte[] byt=new byte[1024];
						int len= in.read(byt);
						String response=new String(byt,0,len);
						
//						
//						
						
						if(listen!=null){
							//回调onFinsh方法
							String s1=response.toString();
							listen.onFinish(s1);
						}
					}else {
						Log.i("error", "responcecode!=200");
					}
					
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
					if(listen!=null){
						listen.OnErrorListener(e);
					}
					
				}
				finally{
					if(connection!=null){
						connection.disconnect();
					}
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
