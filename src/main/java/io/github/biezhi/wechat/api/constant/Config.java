package io.github.biezhi.wechat.api.constant;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 微信API配置
 *
 * @author biezhi
 * @date 2018/1/18
 */
@Slf4j
public class Config {

	/**
     * 资源存储的文件夹，包括图片、视频、音频
     */
    private static final String CONF_ASSETS_DIR         = "wechat.assets-path";
    private static final String CONF_ASSETS_DIR_DEFAULT = "assets";

    /**
     * 是否输出二维码到终端
     */
    private static final String CONF_OPEN_QRCODE = "wechat.open-qrcode";

    /**
     * 自动回复消息，测试时用
     */
    private static final String CONF_AUTO_REPLY         = "wechat.auto-reply";
    private static final String CONF_AUTO_REPLY_DEFAULT = "false";

    /**
     * 自动登录
     */
    private static final String CONF_AUTO_LOGIN         = "wechat.auto-login";
    private static final String CONF_AUTO_LOGIN_DEFAULT = "false";

    /**
     * 自动添加好友请求
     */
    private static final String CONF_AUTO_ADDFRIEND         = "wechat.auto-addfriend";
    private static final String CONF_AUTO_ADDFRIEND_DEFAULT = "false";

	/**
	 * 定制化配置
	 */
	public static final String CONF_GROUP_NAME = "group.name";
	public static final String CONF_GROUP_USERNAME = "group.username";
	public static final String CONF_LOVER_NICKNAME = "lover.nickname";
	public static final String CONF_LOVER_USERNAME = "lover.username";

	public String currentJarExecPath;


	private Properties props = new Properties();

    public static Config me() {
		Config load = new Config().load("config.properties", true)
								  .load("wechat.properties", false);
		if (log.isDebugEnabled()) {
			log.debug("Property load: {}", load.props);
		}
		return load;
    }

    /**
     * 加载 ClassPath 下的配置文件
     *
     * @param filePath
     * @param require
	 * @return
     */
    public Config load(String filePath, boolean require) {
		InputStream in = null;
		InputStreamReader reader = null;
		try {
			String pathGetClass = getCurrentJarExecPath();
			File file = new File(pathGetClass + filePath);
			boolean fileExists = file.exists();
			if (require && !fileExists) {
				throw new FileNotFoundException("config.properties is required!");
			}
			in = fileExists ? new FileInputStream(file) : getClass().getClassLoader().getResourceAsStream(filePath);
			assert in != null;
			reader = new InputStreamReader(in, UTF_8);
			props.load(reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				log.error("Config load error: ", e);
			}
		}
        return this;
    }

	private String getCurrentJarExecPath() throws UnsupportedEncodingException {
		if (currentJarExecPath != null) {
			return currentJarExecPath;
		}
		String pathGetClass = decode(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), UTF_8.toString());
		int lastIndex = pathGetClass.lastIndexOf(File.separator) + 1;
		currentJarExecPath = pathGetClass.substring(0, lastIndex);
		return currentJarExecPath;
	}

	public void set(String key, String value) {
		props.setProperty(key, value);
	}

    public String get(String key) {
        return props.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public String assetsDir() {
        return props.getProperty(CONF_ASSETS_DIR, CONF_ASSETS_DIR_DEFAULT);
    }

    public Config assetsDir(String dir) {
        props.setProperty(CONF_ASSETS_DIR, dir);
        return this;
    }

    public boolean openQrCode() {
        return Boolean.valueOf(props.getProperty(CONF_OPEN_QRCODE));
    }

	public boolean autoReply() {
        return Boolean.valueOf(props.getProperty(CONF_AUTO_REPLY, CONF_AUTO_REPLY_DEFAULT));
    }

    public Config autoReply(boolean autoReply) {
        props.setProperty(CONF_AUTO_REPLY, String.valueOf(autoReply));
        return this;
    }

    public Config autoLogin(boolean autoLogin) {
        props.setProperty(CONF_AUTO_LOGIN, String.valueOf(autoLogin));
        return this;
    }

    public Config autoAddFriend(boolean autoAddFriend) {
        props.setProperty(CONF_AUTO_ADDFRIEND, String.valueOf(autoAddFriend));
        return this;
    }

    public boolean autoAddFriend() {
        return Boolean.valueOf(props.getProperty(CONF_AUTO_ADDFRIEND, CONF_AUTO_ADDFRIEND_DEFAULT));
    }

    public boolean autoLogin() {
        return Boolean.valueOf(props.getProperty(CONF_AUTO_LOGIN, CONF_AUTO_LOGIN_DEFAULT));
    }

	public String loverUserName() {
		return props.getProperty(CONF_LOVER_USERNAME);
	}

	public String groupName() {
		return props.getProperty(CONF_GROUP_NAME);
	}

	public String groupUserName() {
		return props.getProperty(CONF_GROUP_USERNAME);
	}

	public void groupUserName(String groupUsername) {
		props.setProperty(CONF_GROUP_USERNAME, groupUsername);
	}

}
