package io.github.biezhi.wechat.api.constant.custom;

import lombok.Data;

import java.util.Set;

/**
 * @author ybd
 * @date 19-6-17
 * @contact yangbingdong1994@gmail.com
 */
@Data
public class AutoReply {

	private Boolean enable = false;

	private Set<String> nickNames;

	private String prefix;

	public Set<String> userNameSet;
}
