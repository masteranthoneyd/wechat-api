package io.github.biezhi.wechat.api.constant.custom;

import lombok.Data;

import java.util.Set;

/**
 * @author ybd
 * @date 19-7-6
 * @contact yangbingdong1994@gmail.com
 */
@Data
public class AutoVerify {
    private boolean enable = false;

    private Set<String> passMessage;
}
