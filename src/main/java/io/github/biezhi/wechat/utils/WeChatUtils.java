package io.github.biezhi.wechat.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vdurmont.emoji.EmojiParser;
import io.github.biezhi.wechat.api.constant.Constant;
import io.github.biezhi.wechat.exception.WeChatException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.getProperty;

/**
 * 微信公共静态方法
 *
 * @author biezhi
 * @date 2018/1/19
 */
@Slf4j
public class WeChatUtils {

    private static final Gson GSON        = new Gson();
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 正则匹配
     *
     * @param reg
     * @param text
     * @return
     */
    public static String match(String reg, String text) {
        Pattern pattern = Pattern.compile(reg);
        Matcher m       = pattern.matcher(text);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public static Matcher matcher(String reg, String text) {
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(text);
    }

    /**
     * 格式化微信消息
     * <p>
     * 处理emoji表情、HTML转义符
     *
     * @param msg
     * @return
     */
    public static String formatMsg(String msg) {
        msg = msg.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("<br/>", "\n");
        return emojiParse(msg);
    }

    /**
     * 获取文件 MimeType
     *
     * @param fileUrl
     * @return
     */
    public static String getMimeType(String fileUrl) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String      type        = fileNameMap.getContentTypeFor(fileUrl);
        return type;
    }

    public static String emojiParse(String text) {
        Matcher       m         = matcher("<span class=\"emoji emoji(.{1,10})\"></span>", text);
        StringBuilder sb        = new StringBuilder();
        int           lastStart = 0;
        while (m.find()) {
            String str = m.group(1);
            if (str.length() == 6) {

            } else if (str.length() == 10) {

            } else {
                str = "&#x" + str + ";";
                String tmp = text.substring(lastStart, m.start());
                sb.append(tmp + str);
                lastStart = m.end();
            }
        }
        if (lastStart < text.length()) {
            sb.append(text.substring(lastStart));
        }
        if (sb.length() > 0) {
            return EmojiParser.parseToUnicode(sb.toString());
        }
        return text;
    }

    public static String toPrettyJson(Object bean) {
        return PRETTY_GSON.toJson(bean);
    }

    public static String toJson(Object bean) {
        return GSON.toJson(bean);
    }

    public static <T> T fromJson(String json, Class<T> from) {
        return GSON.fromJson(json, from);
    }

    public static <T> T fromJson(String json, Type from) {
        return GSON.fromJson(json, from);
    }

    public static <T> T fromJson(FileReader fileReader, Type from) {
        return GSON.fromJson(fileReader, from);
    }

    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        return GSON.fromJson(json, typeToken.getType());
    }

    public static File saveFile(InputStream inputStream, String dirPath, String fileName) {
        return saveFileByDay(inputStream, dirPath, fileName, false);
    }

    public static File saveFileByDay(InputStream inputStream, String dirPath, String fileName, boolean byDay) {
        OutputStream outputStream = null;
        try {
            if (byDay) {
                dirPath = dirPath + "/" + DateUtils.getDateString();
            }
            File dir = new File(dirPath);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            File path = new File(dir, fileName);
            if (path.exists()) {
                path.delete();
            }
            outputStream = new FileOutputStream(path);

            byte[] buffer = new byte[1024];
            int    len    = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            return path;
        } catch (Exception e) {
            throw new WeChatException(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static <T> void writeJson(String file, T data) {
        try {
            FileWriter writer = new FileWriter(file);
            GSON.toJson(data, writer);
            writer.close();
        } catch (Exception e) {
            log.warn("写入JSON到文件: {} 失败", file, e);
        }
    }

    public static int random(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

	public static void printSystemInfo() {
		System.out.println("\n" +
				" __          __       _           _     ____        _   \n" +
				" \\ \\        / /      | |         | |   |  _ \\      | |  \n" +
				"  \\ \\  /\\  / /__  ___| |__   __ _| |_  | |_) | ___ | |_ \n" +
				"   \\ \\/  \\/ / _ \\/ __| '_ \\ / _` | __| |  _ < / _ \\| __|\n" +
				"    \\  /\\  /  __/ (__| | | | (_| | |_  | |_) | (_) | |_ \n" +
				"     \\/  \\/ \\___|\\___|_| |_|\\__,_|\\__| |____/ \\___/ \\__|\n" +
				"                                                        \n" +
				"                                                        \n");
		log.info("Wechat Bot Version:	 {}", Constant.VERSION);
		log.info("Java Version:			 {}", getProperty("java.version"));
		log.info("Operating System :	 {}", getProperty("os.name") + " " + getProperty("os.arch") + " " + System.getProperty("os.version"));
		log.info("User Name:			 {}", getProperty("user.name"));
	}

}
