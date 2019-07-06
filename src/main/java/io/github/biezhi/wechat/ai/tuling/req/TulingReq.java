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
public class TulingReq {
	private int reqType = 0;
	private Perception perception;
	private UserInfo userInfo;

	public static TulingReq of(String text, String userId, String apiKey) {
		return new TulingReq().setPerception(Perception.of(text)).setUserInfo(UserInfo.of(userId, apiKey));
	}
}
