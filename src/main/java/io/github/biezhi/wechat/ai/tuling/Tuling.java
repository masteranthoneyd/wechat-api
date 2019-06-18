package io.github.biezhi.wechat.ai.tuling;

import io.github.biezhi.wechat.ai.tuling.req.TulingReq;
import io.github.biezhi.wechat.ai.tuling.resp.TulingResp;
import io.github.biezhi.wechat.api.model.WeChatMessage;
import io.github.biezhi.wechat.utils.WeChatUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

/**
 * @author ybd
 * @date 19-6-14
 * @contact yangbingdong1994@gmail.com
 */
public class Tuling {

	public static String send(WeChatMessage message, OkHttpClient client) {
		try {
			Request.Builder builder = new Request.Builder();
			RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),
					WeChatUtils.toJson(TulingReq.of(message.getText(), message.getGroupMsgOwner().substring(1, 10))));
			Request request = builder.url("http://openapi.tuling123.com/openapi/api/v2")
									 .post(requestBody)
									 .build();
			Response response = client.newCall(request).execute();
			ResponseBody body = response.body();
			String respJson = body.string();
			TulingResp tulingResp = WeChatUtils.fromJson(respJson, TulingResp.class);
			return tulingResp.getResults()
							 .get(0)
							 .getValues()
							 .getText();
		} catch (IOException e) {
			e.printStackTrace();
			return "机器人智障啦~(≧▽≦)~";
		}
	}
}
