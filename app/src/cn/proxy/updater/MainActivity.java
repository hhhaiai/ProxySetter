package cn.proxy.updater;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * @Copyright © 2017 sanbo Inc. All rights reserved.
 * @Description: 代理设置
 * @Version: 1.0
 * @Create: 2017年8月23日 下午5:43:25
 * @author: sanbo
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSetProxy:
                break;
            case R.id.btnCancleProxy:
                break;
            case R.id.btnReporrtError:
                break;

            default:
                break;
        }
    }
}
