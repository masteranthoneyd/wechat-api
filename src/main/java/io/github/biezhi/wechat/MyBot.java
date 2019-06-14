package io.github.biezhi.wechat;

import io.github.biezhi.wechat.ai.tuling.Tuling;
import io.github.biezhi.wechat.api.annotation.Bind;
import io.github.biezhi.wechat.api.constant.Config;
import io.github.biezhi.wechat.api.enums.AccountType;
import io.github.biezhi.wechat.api.enums.MsgType;
import io.github.biezhi.wechat.api.model.WeChatMessage;
import io.github.biezhi.wechat.utils.StringUtils;
import io.github.biezhi.wechat.utils.WeChatUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 我的小机器人
 *
 * @author biezhi
 * @date 2018/1/19
 */
@Slf4j
public class MyBot extends WeChatBot {

	public MyBot(Config config) {
        super(config);
    }

    /**
     * 绑定群聊信息
     *
     * @param message
     */
    @Bind(msgType = MsgType.TEXT, accountType = AccountType.TYPE_GROUP)
    public void groupMessage(WeChatMessage message) {
        log.info("接收到群 [{}] 的消息: {}", message.getName(), message.getText());
        log.info("raw: {}", message.getRaw());
		if (autoReply4Group(message)) {
			autoReplyByAI(message);
		}
    }

	private void autoReplyByAI(WeChatMessage message) {
		this.api().sendText(message.getFromUserName(),
				"【我是盖伦】" + Tuling.send(message, this.client().nativeOkHttpClient()));
	}

	private boolean autoReply4Group(WeChatMessage message) {
		return this.config().groupUserName().equals(message.getFromUserName()) && message.isAtMe();
	}

	/**
     * 绑定私聊消息
     *
     * @param message
     */
    @Bind(msgType = MsgType.TEXT, accountType = AccountType.TYPE_FRIEND)
    public void friendMessage(WeChatMessage message) {
        if (StringUtils.isNotEmpty(message.getName())) {
            log.info("接收到好友 [{}] 的消息: {}", message.getName(), message.getText());
			log.info("raw: {}", WeChatUtils.toPrettyJson(message.getRaw()));
			/*if (message.getText().equals("拉我")) {
				this.api().inviteJoinGroup(message.getFromUserName(), this.config().get(CONF_GROUP_USERNAME));
			}*/
            // this.api().sendText(message.getFromUserName(), "自动回复: " + message.getText());
//            this.api().sendFile("战斗型美少女", "/Users/biezhi/Desktop/Hot_Spots_blade2.0.4_alpha1.html");
        }
    }

    /**
     * 好友验证消息
     *
     */
/*    @Bind(msgType = MsgType.ADD_FRIEND)
    public void addFriend(WeChatMessage message) {
        log.info("收到好友验证消息: {}", message.getText());
        if (message.getText().contains("java")) {
            this.api().verify(message.getRaw().getRecommend());
        }
    }*/

    public static void main(String[] args) throws IOException {
        new MyBot(Config.me()).start();

		/*MyBot myBot = new MyBot(Config.me());
		OkHttpClient okHttpClient = myBot.client().nativeOkHttpClient();
		String send = Tuling.send(" 最怕空气突然安静", okHttpClient);
		System.out.println(send);*/

	}


}
