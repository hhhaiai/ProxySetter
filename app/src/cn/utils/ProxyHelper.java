package cn.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;

/**
 * @Copyright © 2017 sanbo Inc. All rights reserved.
 * @Description: 代理设置取消.
 * @Version: 1.0
 * @Create: 2017年8月21日 上午11:15:34
 * @author: sanbo
 */
public class ProxyHelper {

    /**
     * 设置wifi接口
     *
     * @param context
     * @param ip
     * @param port
     */
    public static void setProxy(Context context, String ip, int port) {
        if (Build.VERSION.SDK_INT >= 21) {
            setWifiProxyFor5x(context, ip, port);
        } else {
            setWifiProxySettings(context, ip, port);
        }
    }

    /**
     * 取消wifi入口
     *
     * @param context
     */
    public static void clearProxy(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            resetSetHttpProxyFor5x(context);
        } else {
            resetWifiProxySettings(context);
        }
    }

    /**
     * 3.x, 4.x系列设置代理
     *
     * @param context
     * @param ip
     * @param port
     */
    static void setWifiProxySettings(Context context, String ip, int port) {
        // get the current wifi configuration
        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = GetCurrentWifiConfiguration(manager);
        if (null == config) { return; }

        try {
            // get the link properties from the wifi configuration
            Object linkProperties = getField(config, "linkProperties");
            if (null == linkProperties) { return; }

            // get the setHttpProxy method for LinkProperties
            Class<?> proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            Class<?>[] setHttpProxyParams = new Class[1];
            setHttpProxyParams[0] = proxyPropertiesClass;
            Class<?> lpClass = Class.forName("android.net.LinkProperties");
            Method setHttpProxy = lpClass.getDeclaredMethod("setHttpProxy", setHttpProxyParams);
            setHttpProxy.setAccessible(true);

            // get ProxyProperties constructor
            Class<?>[] proxyPropertiesCtorParamTypes = new Class[3];
            proxyPropertiesCtorParamTypes[0] = String.class;
            proxyPropertiesCtorParamTypes[1] = int.class;
            proxyPropertiesCtorParamTypes[2] = String.class;

            Constructor<?> proxyPropertiesCtor = proxyPropertiesClass.getConstructor(proxyPropertiesCtorParamTypes);

            // create the parameters for the constructor
            Object[] proxyPropertiesCtorParams = new Object[3];
            proxyPropertiesCtorParams[0] = ip;
            proxyPropertiesCtorParams[1] = port;
            proxyPropertiesCtorParams[2] = null;

            // create a new object using the params
            Object proxySettings = proxyPropertiesCtor.newInstance(proxyPropertiesCtorParams);

            // pass the new object to setHttpProxy
            Object[] params = new Object[1];
            params[0] = proxySettings;
            setHttpProxy.invoke(linkProperties, params);

            setProxySettings("STATIC", config);

            // save the settings
            manager.updateNetwork(config);
            manager.disconnect();
            manager.reconnect();
        } catch (Exception e) {
            L.e("setWifiProxySettings-->exception-->toString:" + e.toString() + ",message:" + e.getMessage());
        }
    }

    /**
     * 取消网络代理
     *
     * @param context
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static void resetWifiProxySettings(Context context) {
        // WifiManager manager = (WifiManager)
        // context.getSystemService(Context.WIFI_SERVICE);
        // WifiConfiguration config = GetCurrentWifiConfiguration(manager);
        // if (null == config)
        // return;
        WifiManager manager = null;
        WifiConfiguration config = null;
        try {
            manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            config = GetCurrentWifiConfiguration(manager);
            if (null == config) { return; }
        } catch (Exception e) {
            L.e(e);
        }

        try {
            // get the link properties from the wifi configuration
            Object linkProperties = getField(config, "linkProperties");
            if (null == linkProperties) { return; }

            // get the setHttpProxy method for LinkProperties
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            Class[] setHttpProxyParams = new Class[1];
            setHttpProxyParams[0] = proxyPropertiesClass;
            Class lpClass = Class.forName("android.net.LinkProperties");
            Method setHttpProxy = lpClass.getDeclaredMethod("setHttpProxy", setHttpProxyParams);
            setHttpProxy.setAccessible(true);

            // pass null as the proxy
            Object[] params = new Object[1];
            params[0] = null;
            setHttpProxy.invoke(linkProperties, params);

            setProxySettings("NONE", config);

            // save the config
            manager.updateNetwork(config);
            manager.disconnect();
            manager.reconnect();
        } catch (Exception e) {
        }
    }

    static Object getField(Object obj, String name)
        throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        if (obj == null) {
            return null;
        }
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    static Object getDeclaredField(Object obj, String name) {
        if (obj == null) {
            return null;
        }
        Object out = null;
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            out = f.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static void setEnumField(Object obj, String value, String name)
        throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>)f.getType(), value));
    }

    static void setProxySettings(String assign, WifiConfiguration wifiConf)
        throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "proxySettings");
    }

    static WifiConfiguration GetCurrentWifiConfiguration(WifiManager manager) {
        if (!manager.isWifiEnabled()) { return null; }

        List<WifiConfiguration> configurationList = manager.getConfiguredNetworks();
        WifiConfiguration configuration = null;
        int cur = manager.getConnectionInfo().getNetworkId();
        for (int i = 0; i < configurationList.size(); ++i) {
            WifiConfiguration wifiConfiguration = configurationList.get(i);
            if (wifiConfiguration.networkId == cur) { configuration = wifiConfiguration; }
        }

        return configuration;
    }

    /****************************/

    /**
     * 5.x取消代理设置
     */
    @TargetApi(21)
    static void resetSetHttpProxyFor5x(Context context) {

        ProxyInfo mInfo = null;
        try {
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration configuration = getCurrentWifiConfiguration(wifiManager);
            mInfo = ProxyInfo.buildDirectProxy(null, 0);
            if (configuration != null) {
                Class<?> clazz = Class.forName("android.net.wifi.WifiConfiguration");
                Class<?> parmars = Class.forName("android.net.ProxyInfo");
                Method method = clazz.getMethod("setHttpProxy", parmars);
                method.invoke(configuration, mInfo);
                Object mIpConfiguration = getDeclaredFieldObject(configuration, "mIpConfiguration");
                setEnumField(mIpConfiguration, "NONE", "proxySettings");
                setDeclardFildObject(configuration, "mIpConfiguration", mIpConfiguration);

                // 保存设置
                wifiManager.updateNetwork(configuration);
                wifiManager.disconnect();
                wifiManager.reconnect();
            }
        } catch (Exception e) {
        }

    }

    /**
     * 5.x 系列设置代理
     *
     * @param context
     * @param host
     * @param port
     */
    @TargetApi(21)
    static void setWifiProxyFor5x(Context context, String host, int port) {
        ProxyInfo mInfo = null;
        try {
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration config = getCurrentWifiConfiguration(wifiManager);
            mInfo = ProxyInfo.buildDirectProxy(host, port);
            if (config != null) {
                Class<?> clazz = Class.forName("android.net.wifi.WifiConfiguration");
                Class<?> parmars = Class.forName("android.net.ProxyInfo");
                Method method = clazz.getMethod("setHttpProxy", parmars);
                method.invoke(config, mInfo);
                Object mIpConfiguration = getDeclaredFieldObject(config, "mIpConfiguration");

                setEnumField(mIpConfiguration, "STATIC", "proxySettings");
                setDeclardFildObject(config, "mIpConfiguration", mIpConfiguration);
                // save the settings
                wifiManager.updateNetwork(config);
                wifiManager.disconnect();
                wifiManager.reconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static Object getDeclaredFieldObject(Object obj, String name)
        throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    static void setDeclardFildObject(Object obj, String name, Object object) {
        Field f = null;
        try {
            f = obj.getClass().getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            f.set(obj, object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // 获取当前的Wifi连接
    static WifiConfiguration getCurrentWifiConfiguration(WifiManager wifiManager) {
        if (!wifiManager.isWifiEnabled()) { return null; }
        List<WifiConfiguration> configurationList = wifiManager.getConfiguredNetworks();
        WifiConfiguration configuration = null;
        int cur = wifiManager.getConnectionInfo().getNetworkId();
        // Log.d("当前wifi连接信息",wifiManager.getConnectionInfo().toString());
        for (int i = 0; i < configurationList.size(); ++i) {
            WifiConfiguration wifiConfiguration = configurationList.get(i);
            if (wifiConfiguration.networkId == cur) { configuration = wifiConfiguration; }
        }
        return configuration;
    }
}
