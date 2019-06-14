package io.github.biezhi.wechat.ai.tuling.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ybd
 * @date 19-6-14
 * @contact yangbingdong1994@gmail.com
 */
@Data
@Accessors(chain = true)
public class InputText {

	private String text;

	public static InputText of(String text) {
		return new InputText().setText(text);
	}

}
