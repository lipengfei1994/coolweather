package db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

	/**
	 *创建省表
	 */
	public static final String CREATE_PROVINCE="create table Province ("
			+"id integer primary key autoincrement,"
			+"province_name text,"
			+"province_code text)"
			;
	/**
	 * 创建城市表
	 */
	public static final String CREATE_CITY="create table City ("
			+"id integer primary key autoincrement,"
			+"city_name text,"
			+"city_code text,"
			+ "province_id integer)"
			;
	/**
	 * 创建乡镇表
	 */
	public static final String CREATE_COUNTRY="create table Country ("
			+"id integer primary key autoincrement,"
			+"country_name text,"
			+"country_code text,"
			+ "city_id integer)"
			;
	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO 自动生成的构造函数存根
	}

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO 自动生成的构造函数存根
	}

	//第一次创建数据库将要执行oncreate（）
	@Override
	public void onCreate(SQLiteDatabase db) {
		//执行建表语句
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTRY);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自动生成的方法存根

	}

}
