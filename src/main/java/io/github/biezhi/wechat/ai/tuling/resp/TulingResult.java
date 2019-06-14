package io.github.biezhi.wechat.ai.tuling.resp;

import lombok.Data;

/**
 * @author ybd
 * @date 19-6-14
 * @contact yangbingdong1994@gmail.com
 */
@Data
public class TulingResult {
	private Integer groupType;
	private String resultType;
	private TulingResultValue values;
}
