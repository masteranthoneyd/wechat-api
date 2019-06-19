package io.github.biezhi.wechat.prattle;

import com.alibaba.fastjson.JSONObject;
import io.github.biezhi.wechat.api.constant.custom.LoverPrattle;
import io.github.biezhi.wechat.utils.OkHttpUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * @author ybd
 * @date 19-6-18
 * @contact yangbingdong1994@gmail.com
 */
public class PrattleInfoReqUtil {
	private static OkHttpClient okHttpClient;
	static {
		OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
																 .writeTimeout(30, TimeUnit.SECONDS)
																 .readTimeout(60, TimeUnit.SECONDS);
		OkHttpUtils.configureToIgnoreCertificate(builder);
		okHttpClient = builder.build();
	}
	private static final String[][] WEEK_ARRAY = new String[][]{{"1", "一"}, {"2", "二"}, {"3", "三"}, {"4", "四"}, {"5", "五"}, {"6", "六"}, {"7", "日"}};

	public static String getCurrentDate() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DayOfWeek dayOfWeek = now.getDayOfWeek();
		String k = String.valueOf(dayOfWeek.getValue());
		for (String[] strings : WEEK_ARRAY) {
			if (k.equals(strings[0])) {
				k = strings[1];
				break;
			}
		}
		return dateTimeFormatter.format(now) + " 星期" + k + "\n";
	}

	public static String getLoveDays(String date) {
		LocalDate parse = LocalDate.parse(date);
		LocalDate localDate = LocalDate.now();
		return "这是我们在一起的第" + ChronoUnit.DAYS.between(parse, localDate) + "天";
	}

	/**
	 * https://github.com/MZCretin/RollToolsApi#%E8%8E%B7%E5%8F%96%E7%89%B9%E5%AE%9A%E5%9F%8E%E5%B8%82%E4%BB%8A%E6%97%A5%E5%A4%A9%E6%B0%94
	 */
	public static String getWeather(String city) {
		try {
			String cityEncode = URLEncoder.encode(city, "UTF-8");
			Request request = new Request.Builder().url("https://www.mxnzp.com/api/weather/current/" + cityEncode).build();
			String respJson = okHttpClient.newCall(request).execute().body().string();
			JSONObject json = JSONObject.parseObject(respJson);
			WeatherInfo weather = json.getJSONObject("data").toJavaObject(WeatherInfo.class);
			StringBuilder sb = new StringBuilder("今日天气").append("\n");
			sb.append("    ").append(weather.getTemp()).append(" ").append(weather.getWeather()).append("\n")
			  .append("    ").append(weather.getWindDirection()).append("风 ").append(weather.getWindPower()).append("\n")
			  .append("    ").append("湿度 ").append(weather.getHumidity()).append("\n");
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * https://www.jinrishici.com/doc/
	 */
	public static String getPoetry() {
		try {
			Request request = new Request.Builder().url("https://v2.jinrishici.com/sentence")
												   .header("X-User-Token", "6EItLdSixaB4xW3gWb5lj1/9b6JohoN0")
												   .build();
			String respJson = okHttpClient.newCall(request).execute().body().string();
			JSONObject json = JSONObject.parseObject(respJson);
			return json.getJSONObject("data").getString("content");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * https://www.v2ex.com/t/569853
	 * https://www.tianapi.com/apiview/80
	 */
	public static String getPrattle() {
		try {
			Request request = new Request.Builder().url("https://api.lovelive.tools/api/SweetNothings")
												   .build();
			return "我想对你说:\n    " + okHttpClient.newCall(request).execute().body().string();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String reducePrattle(LoverPrattle loverPrattle) {
		StringBuilder sb = new StringBuilder();
		sb.append(getCurrentDate())
		  .append(getLoveDays(loverPrattle.getFallInLoveAt()))
		  .append("\n\n")
		  .append(getWeather(loverPrattle.getCity()))
		  .append("    ").append(getPoetry())
		  .append("\n\n")
		  .append(getPrattle());
		return sb.toString();
	}

}
