package cn.proxy.updater;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cn.utils.HandlerHelper;
import cn.utils.HandlerHelper.OnReceiveMessageListener;
import cn.utils.L;
import cn.utils.ProxyHelper;
import cn.utils.T;
import org.json.JSONObject;

/**
 * @Copyright © 2017 sanbo Inc. All rights reserved.
 * @Description: 代理设置
 * @Version: 1.0
 * @Create: 2017年8月23日 下午5:43:25
 * @author: sanbo(转载/使用.请保留原作者)
 */
public class MainActivity extends Activity implements OnReceiveMessageListener {
    private HandlerHelper mHandler;

    private Context mContext;
    private EditText mEtPort = null;
    private EditText mEtIP = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mHandler = HandlerHelper.getInstance(this);
        mEtPort = (EditText)this.findViewById(R.id.etPort);
        mEtIP = (EditText)this.findViewById(R.id.etIp);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSetProxy:
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(0);
                }
                break;
            case R.id.btnQuery:
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(1);
                }
                break;
            case R.id.btnCancleProxy:
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(2);
                }
                break;

            case R.id.btnReporrtError:
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(3);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void handlerMessage(Message msg) {
        switch (msg.what) {
            case 0:
                L.i(" handler receiver set proxy");
                if (mEtPort == null) {
                    mEtPort = (EditText)this.findViewById(R.id.etPort);
                }
                if (mEtIP == null) {
                    mEtIP = (EditText)this.findViewById(R.id.etIp);
                }
                String ip = mEtIP.getText().toString();
                String port = mEtPort.getText().toString();
                if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
                    T.show(mContext, "请检查IP/端口,不可以为空", Toast.LENGTH_LONG);
                } else {
                    T.show(mContext, "将要设置代理:\r\n" + ip.trim() + ":" + Integer.valueOf(port.trim()),
                        Toast.LENGTH_LONG);
                    ProxyHelper.setProxy(mContext, ip.trim(), Integer.valueOf(port.trim()));
                }

                break;
            case 1:
                L.i(" handler receiver quert ip and port");
                JSONObject result = ProxyHelper.getProxyInfo(this);
                if (result != null) {
                    if (result.length() > 0) {
                        if (result.optInt("port") == -1 || result.optInt("port") == 0) {
                            L.w("没有设置代理...");
                            T.show(mContext, "没有设置代理...", Toast.LENGTH_LONG);
                        } else {
                            L.i(result.toString());

                            T.show(mContext,
                                "代理地址:\r\n" + result.optString("host") + ":" + result.optInt("port"),
                                Toast.LENGTH_LONG);
                        }
                    } else {
                        L.w("没有设置代理...");
                        T.show(mContext, "没有设置代理...", Toast.LENGTH_LONG);
                    }
                }
                break;
            case 2:
                L.i(" handler receiver clear proxy");
                ProxyHelper.clearProxy(mContext);

                break;
            case 3:
                L.i(" handler receiver report error");
                break;

            default:
                break;
        }

    }
}
