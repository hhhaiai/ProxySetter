package cn.utils;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

/**
 * @Copyright © 2017 sanbo Inc. All rights reserved.
 * @Description: 若指针handler工具类,使用方法:实现OnReceiveMessageListener接口,实例化HandlerHelper示例发消息即可
 * @Version: 1.0
 * @Create: 2017年8月24日 上午10:35:22
 * @author: sanbo
 */
public class HandlerHelper extends Handler {
    private static WeakReference<OnReceiveMessageListener> mListenerWeakReference;

    private HandlerHelper() {
    }

    private static class HandlerHelperHOLDER {
        private static final HandlerHelper INSTANCE = new HandlerHelper();
    }

    public static HandlerHelper getInstance(OnReceiveMessageListener listener) {
        if (mListenerWeakReference == null) {
            mListenerWeakReference = new WeakReference<OnReceiveMessageListener>(listener);
        }
        return HandlerHelperHOLDER.INSTANCE;
    }

    @Override
    public void handleMessage(Message msg) {
        if (mListenerWeakReference != null && mListenerWeakReference.get() != null) {
            mListenerWeakReference.get().handlerMessage(msg);
        }
    }

    /**
     * 收到消息回调接口
     */
    public interface OnReceiveMessageListener {
        void handlerMessage(Message msg);
    }
}
