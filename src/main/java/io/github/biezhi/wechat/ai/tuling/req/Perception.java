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
public class Perception {

	private InputText inputText;

	public static Perception of(String text) {
		return new Perception().setInputText(InputText.of(text));
	}

}
