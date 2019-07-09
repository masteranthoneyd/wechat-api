package io.github.biezhi.wechat.api.constant.custom;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ybd
 * @date 19-7-9
 * @contact yangbingdong1994@gmail.com
 */
@Data
public class AutoCurse {
    private boolean enable = false;

    private boolean fire = false;

    private Set<String> nickNames;

    public Set<String> userNameSet = new HashSet<>();
}
