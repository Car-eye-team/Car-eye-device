/**
 *
 */
package com.sh.camera.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 对json数据解析过程中的错误进行异常处理，保证解析过程中不会以为部分内容解析异常而使整个解析过程停止。
 *
 * @author 田裕杰
 */
public class JSONUtil {

	/**
	 * JSONObjec报文获取键值对中值时进行容错处理， 获取String
	 *
	 * @param jsonObject
	 *            ：需要解析的json对象、name：json中的键名
	 * @return String：通过键名得到的值
	 */
	public static String getString(JSONObject jsonObject, String name) {
		String value = "";
		try {
			if (jsonObject != null) {
				value = jsonObject.getString(name);
			}
		} catch (JSONException e) {
			value = "";
		}
		return value;
	}

	/**
	 * JSONArray报文获取键值对中值时进行容错处理， 获取String
	 *
	 * @param jsonArray
	 *            ：需要解析的json对象、i:数组中某个对象、name：json中的键名
	 * @return String：通过键名得到的值
	 */
	public static String getString(JSONArray jsonArray, int i, String name) {
		String value = "";
		JSONObject jsonObject = getJSONObject(jsonArray, i);
		if (jsonObject != null) {
			value = jsonObject.optString(name, "fallback");
		}
		return value;
	}

	/**
	 * JSONObjec报文获取键值对中值时进行容错处理， 获取String
	 *
	 * @param jsonObject
	 *            ：需要解析的json对象、name：json中的键名
	 * @return double：通过键名得到的值
	 */
	public static Double getDouble(JSONObject jsonObject, String name) {
		Double value = 0.0;
		try {
			if (jsonObject != null) {
				value = jsonObject.getDouble(name);
			}
		} catch (JSONException e) {
			value = 0.0;
		}
		return value;
	}

	/**
	 * JSONObjec报文获取键值对中值时进行容错处理， 获取String
	 *
	 * @param jsonObject
	 *            ：需要解析的json对象、name：json中的键名
	 * @return isBoolean：通过键名得到的boolean值
	 */
	public static Boolean getBoolean(JSONObject jsonObject, String name) {
		Boolean value = null;
		try {
			if (jsonObject != null) {
				value = jsonObject.getBoolean(name);
			}
		} catch (JSONException e) {
			value = null;
		}
		return value;
	}

	/**
	 * JSONObjec报文获取键值对中值时进行容错处理， 获取String
	 *
	 * @param jsonObject
	 *            ：需要解析的json对象、name：json中的键名
	 * @return int：通过键名得到的int值
	 */
	public static int getInt(JSONObject jsonObject, String name) {
		int value = 1;
		try {
			if (jsonObject != null) {
				value = jsonObject.getInt(name);
			}
		} catch (JSONException e) {
			value = 1;
		}
		return value;
	}

	/**
	 * JSONObjec报文获取键值对中值时进行容错处理， 获取String
	 *
	 * @param jsonObject
	 *            ：需要解析的json对象、name：json中的键名
	 * @return long：通过键名得到的long值
	 */
	public static long getLong(JSONObject jsonObject, String name) {
		long value = 0;
		try {
			if (jsonObject != null) {
				value = jsonObject.getLong(name);
			}
		} catch (JSONException e) {
			value = 0;
		}
		return value;
	}

	/**
	 * JSONArray报文解析容错处理，获取数组中的JSONObject对象
	 *
	 * @param jsonArray
	 *            ：需要解析的jsonArray对象、i：JSONObject在JSONArray中的索引
	 * @return JSONObject：通过键名得到的JSONObject对象
	 */
	public static JSONObject getJSONObject(JSONArray jsonArray, int i) {
		JSONObject value = new JSONObject();
		try {
			if (jsonArray != null) {
				value = jsonArray.getJSONObject(i);
			}
		} catch (JSONException e) {
		}
		return value;
	}

	/**
	 * JSONArray报文解析容错处理，获取数组中的JSONObject对象
	 *
	 * @param jsonObject
	 *            ：需要解析的jsonObject对象、name:健名
	 * @return JSONObject：通过键名得到的JSONObject对象
	 */
	public static JSONObject getJSONObject(JSONObject jsonObject, String name) {
		JSONObject value = new JSONObject();
		try {
			if (jsonObject != null) {
				value = jsonObject.getJSONObject(name);
			}
		} catch (JSONException e) {
		}
		return value;
	}

	/**
	 * JSONObject报文解析容错处理，获取JSONObject中的JSONArray数组
	 *
	 * @param jsonObject
	 *            ：需要解析的json对象、name：json中的键名
	 * @return JSONArray：通过键名得到的JSONArray对象
	 */
	public static JSONArray getJSONArray(JSONObject jsonObject, String name) {
		JSONArray value = new JSONArray();
		try {
			if (jsonObject != null) {
				value = jsonObject.getJSONArray(name);
			}
		} catch (JSONException e) {
//			value = null;
		}
		return value;
	}

	/**
	 * JSONObject报文解析容错处理，获取JSONObject中的JSONArray数组
	 *
	 * @param jsonObject
	 *            ：需要解析的json对象、name：json中的键名
	 * @return JSONArray：通过键名得到的JSONArray对象
	 */
	public static JSONArray getJSONArray1(JSONObject jsonObject, String name) {
		JSONArray value = new JSONArray();
		try {
			if (jsonObject != null) {
				value = jsonObject.getJSONArray(name);
			}
		} catch (JSONException e) {
//			value = null;
		}
		return value;
	}

	/**
	 * JSONObject报文解析容错处理，判断报文格式是否正确
	 *
	 * @param string
	 *            ：需要转换为JSONObject的字符串
	 * @return JSONObject：通过键名得到的JSONArray对象
	 */
	public static JSONObject getJSONObject(String string) {
		JSONObject value = new JSONObject();
		if (string != null && string != "") {
			try {
				value = new JSONObject(string);
			} catch (JSONException e) {
			}
		}
		return value;
	}

	/**
	 * JSONArray报文解析容错处理，判断报文格式是否正确
	 *
	 * @param string
	 *            ：需要转换为JSONArray的字符串
	 * @return JSONArray：通过键名得到的JSONArray对象
	 */
	public static JSONArray getJSONArray(String string) {
		JSONArray value = new JSONArray();
		if (string != null && string != "") {
			try {
				value = new JSONArray(string);
			} catch (JSONException e) {
			}
		}
		return value;
	}

	/**
	 * JSONObject赋值容错处理
	 *
	 * @param jsonObject：需要赋值的对象，name：键（String），value：值（String）
	 * @return 无
	 */
	public static void setString(JSONObject jsonObject, String name, String value) {
		try {
			jsonObject.put(name, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
	}

	/**
	 * JSONObject赋值容错处理
	 *
	 * @param jsonObject：需要赋值的对象，name：键（String），value：值（Double）
	 * @return 无
	 */
	public static void setDouble(JSONObject jsonObject, String name, Double value) {
		try {
			jsonObject.put(name, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
	}
}
