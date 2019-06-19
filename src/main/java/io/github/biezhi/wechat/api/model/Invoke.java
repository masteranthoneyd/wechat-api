package io.github.biezhi.wechat.api.model;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.biezhi.wechat.WeChatBot;
import io.github.biezhi.wechat.api.enums.AccountType;
import io.github.biezhi.wechat.api.enums.MsgType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 消息执行器
 *
 * @author biezhi
 * @date 2018/1/20
 */
@Data
@Slf4j
@AllArgsConstructor
public class Invoke {

	private static final Cache<String, String> INVOKED_MSG_CACHE = Caffeine.newBuilder()
																		 .expireAfterWrite(5, MINUTES)
																		 .maximumSize(10_000)
																		 .build();

	private static final String HOLDER = "";

    private Method            method;
    private List<AccountType> accountTypes;
    private MsgType           msgType;

    /**
     * 回调给客户端
     *
     * @param bot
     * @param message
     * @param <T>
     */
    public <T extends WeChatBot> void call(T bot, WeChatMessage message) {
        if (INVOKED_MSG_CACHE.getIfPresent(message.getId()) != null) {
            return;
        }
        try {
            // 自动同意添加好友
            if (message.getMsgType() == MsgType.ADD_FRIEND && bot.config().autoAddFriend()) {
                bot.api().verify(message.getRecommend());
                return;
            }
            Account account = bot.api().getAccountById(message.getFromUserName());
            if (null == account) {
                log.warn("{} not found in map, message ignore", message.getFromUserName());
                return;
            }
            if (msgType == MsgType.ALL || msgType == message.getMsgType()) {
                if (message.getMsgType() == MsgType.ADD_FRIEND) {
					INVOKED_MSG_CACHE.put(message.getId(), HOLDER);
                    method.invoke(bot, message);
                } else {
                    if (accountTypes.contains(account.getAccountType())) {
						INVOKED_MSG_CACHE.put(message.getId(), HOLDER);
                        method.invoke(bot, message);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("回调给客户端出错: {}\r\n", message, e);
        }
    }
}
