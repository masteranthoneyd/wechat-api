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
public class UserInfo {

	private String apiKey = "b24b376ec0444b418035720785a8cde4";
	private String userId;

	public static UserInfo of(String userId) {
		return new UserInfo().setUserId(userId);
	}
}
