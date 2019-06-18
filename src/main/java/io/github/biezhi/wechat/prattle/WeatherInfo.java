package io.github.biezhi.wechat.prattle;

import lombok.Data;

/**
 * @author ybd
 * @date 19-6-18
 * @contact yangbingdong1994@gmail.com
 */
@Data
public class WeatherInfo {
	private String temp;

	private String weather;

	private String windDirection;

	private String windPower;

	private String humidity;
}
