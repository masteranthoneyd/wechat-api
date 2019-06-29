package io.github.biezhi.wechat.api.constant.custom;

import lombok.Data;

/**
 * @author ybd
 * @date 19-6-17
 * @contact yangbingdong1994@gmail.com
 */
@Data
public class CustomConfig {

	private Boolean openQrCode = false;

	private LoverPrattle loverPrattle;

	private AutoReply autoReply;
}
