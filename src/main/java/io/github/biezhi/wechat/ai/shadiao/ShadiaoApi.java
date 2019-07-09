package io.github.biezhi.wechat.ai.shadiao;

import okhttp3.Request;

import java.io.IOException;

import static io.github.biezhi.wechat.prattle.PrattleInfoReqUtil.okHttpClient;

/**
 * @author ybd
 * @date 19-7-9
 * @contact yangbingdong1994@gmail.com
 */
public class ShadiaoApi {

    /**
     * 骂人宝典...
     */
    public static String curse(boolean fire) {
        try {
            Request request = new Request.Builder().url("https://nmsl.shadiao.app/api.php" + (fire ? "" : "?level=min"))
                                                   .build();
            return okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
