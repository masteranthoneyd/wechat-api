package io.github.biezhi.wechat;

import io.github.biezhi.wechat.ai.tuling.Tuling;
import io.github.biezhi.wechat.api.annotation.Bind;
import io.github.biezhi.wechat.api.constant.Config;
import io.github.biezhi.wechat.api.constant.custom.AutoVerify;
import io.github.biezhi.wechat.api.constant.custom.LoverPrattle;
import io.github.biezhi.wechat.api.enums.AccountType;
import io.github.biezhi.wechat.api.enums.MsgType;
import io.github.biezhi.wechat.api.model.Account;
import io.github.biezhi.wechat.api.model.WeChatMessage;
import io.github.biezhi.wechat.job.PrattleJobEngine;
import io.github.biezhi.wechat.prattle.PrattleInfoReqUtil;
import io.github.biezhi.wechat.utils.DateUtils;
import io.github.biezhi.wechat.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.biezhi.wechat.utils.StringUtils.isNotEmpty;

/**
 * 我的小机器人
 *
 * @author biezhi
 * @date 2018/1/19
 */
@Slf4j
public class MyBot extends WeChatBot {

	private static final Pattern MODIFY_GROUP_NAME_PATTERN = Pattern.compile("修改群名为“(.*)”");
	private static final Pattern INVITE_FRIEND_PATTERN = Pattern.compile("邀请\"(.*)\"加入了群聊");

	public MyBot(Config config) {
        super(config);
		PrattleJobEngine.fire(this);
    }

    /**
     * 绑定群聊信息
     *
     * @param message
     */
    @Bind(msgType = MsgType.TEXT, accountType = AccountType.TYPE_GROUP)
    public void groupMessage(WeChatMessage message) {
        log.info("接收到群 [{}] 的消息: {}", message.getName(), message.getText());
		if (autoReply4Group(message)) {
			autoReplyByAI(message);
		}
    }

	private void autoReplyByAI(WeChatMessage message) {
		this.api().sendText(message.getFromUserName(),
				getMessagePrefix() + Tuling.send(message, this.client().nativeOkHttpClient(), this.customConfig().getAutoReply().getTulingApiKey()));
	}

	private String getMessagePrefix() {
		return this.customConfig().getAutoReply().getPrefix();
	}

	private boolean autoReply4Group(WeChatMessage message) {
		return this.customConfig().getAutoReply().getEnable() &&
				message.isAtMe() && containGroup(message);
	}

	private boolean containGroup(WeChatMessage message) {
		return this.customConfig().getAutoReply().getUserNameSet().contains(message.getFromUserName());
	}

	@Bind(msgType = MsgType.SYSTEM, accountType = AccountType.TYPE_GROUP)
	public void handlerSystem(WeChatMessage message) {
		String content = message.getRaw().getContent();
		if (isNotEmpty(content)) {
			Account fromAccount = this.api().getAccountById(message.getFromUserName());
			if (fromAccount != null) {
				Matcher modifyGroupNameMatcher = MODIFY_GROUP_NAME_PATTERN.matcher(content);
				if (modifyGroupNameMatcher.find()) {
					fromAccount.setNickName(modifyGroupNameMatcher.group(1));
					return;
				}
			}

			if (containGroup(message)) {
				Matcher inviteFriendMatcher = INVITE_FRIEND_PATTERN.matcher(content);
				if (inviteFriendMatcher.find()) {
					String newGroupMember = inviteFriendMatcher.group(1);
					Account account = this.api().getAccountByName(newGroupMember);
					this.api()
						.sendText(message.getFromUserName(), getMessagePrefix() + "@ " + (account == null ? newGroupMember : account.getNickName()) + "\n\n 欢迎加入");
					log.info("邀请了好友: {} 进群", newGroupMember);
				}
			}
		}
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
			if (autoReplyLover(message)) {
				autoReplyByAI(message);
			}
			/*if (message.getText().equals("拉我进群")) {
				this.api().inviteJoinGroup(message.getFromUserName(), this.config().groupUserName());
			}*/
            // this.api().sendText(message.getFromUserName(), "自动回复: " + message.getText());
//            this.api().sendFile("战斗型美少女", "/Users/biezhi/Desktop/Hot_Spots_blade2.0.4_alpha1.html");
        }
    }

	private boolean autoReplyLover(WeChatMessage message) {
		return message.getFromUserName().equals(customConfig().getLoverPrattle().getLoverUserName())
				&& customConfig().getAutoReply().getEnable();
	}

	/**
	 * 好友验证消息
	 */
    @Bind(msgType = MsgType.ADD_FRIEND)
    public void addFriend(WeChatMessage message) {
        log.info("收到好友验证消息: {}", message.getText());
        AutoVerify autoVerify = this.customConfig().getAutoVerify();
        if (autoVerify.isEnable()) {
            Set<String> passMessage = autoVerify.getPassMessage();
            if (passMessage != null && passMessage.size() > 0 && passMessage.contains(message.getText())) {
                DateUtils.sendSleep();
                this.api().verify(message.getRaw().getRecommend());
            }
        }
    }

    /**
     * 可使用文件助手调试
     * Account account = api().getAccountByName("文件传输助手");
     */
	public void sendPrattle() {
		LoverPrattle loverPrattle = this.customConfig().getLoverPrattle();
		if (loverPrattle != null) {
			String prattle = PrattleInfoReqUtil.reducePrattle(this.customConfig().getLoverPrattle());
			sendMsg(loverPrattle.getLoverUserName(), prattle);
		}
	}

    public static void main(String[] args) {
        new MyBot(Config.me()).start();
	}


}
