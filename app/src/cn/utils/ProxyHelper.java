package cn.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Copyright © 2017 sanbo Inc. All rights reserved.
 * @Description: 代理设置取消.
 * @Version: 1.0
 * @Create: 2017年8月21日 上午11:15:34
 * @author: sanbo(转载/使用.请保留原作者)
 */
public class ProxyHelper {

    public static JSONObject getProxyInfo(Context context) {
        JSONObject obj = new JSONObject();
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = getCurrentWifiConfiguration(wifiManager);
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                ProxyInfo info = getProxyInfoFor5x(config);
                if (info != null) {
                    obj.put("host", info.getHost());
                    obj.put("port", info.getPort());
                }
            } else {
                obj = getProxyInfoFor34x(config);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 设置wifi接口
     *
     * @param context
     * @param ip
     * @param port
     */
    public static void setProxy(Context context, String ip, int port) {
        if (Build.VERSION.SDK_INT < 21) {
            setWifiProxySettingsFor34x(context, ip, port);
        } else if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT <= 24) {
            setWifiProxyFor5x(context, ip, port);
        } else {
            setWifiProxyFor7X(context, ip, port);
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
            resetWifiProxySettingsFor34x(context);
        }
    }

    /**
     * 3.x, 4.x系列查询代理
     *
     * @param config
     * @return
     */
    private static JSONObject getProxyInfoFor34x(WifiConfiguration config) {
        JSONObject obj = new JSONObject();
        try {
            if (null != config) {
                Object linkProperties = getField(config, "linkProperties");
                if (null != linkProperties) {
                    Class<?> lpClass = Class.forName("android.net.LinkProperties");
                    Method method = lpClass.getMethod("getHttpProxy");
                    Object proxyProperties = method.invoke(linkProperties);
                    if (proxyProperties != null) {
                        Class<?> ppzz = Class.forName("android.net.ProxyProperties");
                        Method getHost = ppzz.getMethod("getHost");
                        Method getPort = ppzz.getMethod("getPort");

                        String ip = (String)getHost.invoke(proxyProperties);
                        int port = (Integer)getPort.invoke(proxyProperties);
                        obj.put("host", ip);
                        obj.put("port", port);
                    }

                }
            }
        } catch (Throwable e) {
        }
        return obj;
    }

    /**
     * 3.x, 4.x系列设置代理
     *
     * @param context
     * @param ip
     * @param port
     */
    static void setWifiProxySettingsFor34x(Context context, String ip, int port) {
        // get the current wifi configuration
        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = getCurrentWifiConfiguration(manager);
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

            setEnumField(config, "STATIC", "proxySettings");

            // save the settings
            manager.updateNetwork(config);
        } catch (Exception e) {
            L.e("setWifiProxySettings-->exception-->toString:" + e.toString() + ",message:" + e.getMessage());
        }
    }

    /**
     * 取消网络代理
     *
     * @param context
     */
    static void resetWifiProxySettingsFor34x(Context context) {
        WifiManager manager = null;
        WifiConfiguration config = null;
        try {
            manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            config = getCurrentWifiConfiguration(manager);
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

            setEnumField(config, "NONE", "proxySettings");

            // save the config
            manager.updateNetwork(config);
        } catch (Exception e) {
        }
    }

    /************************************************************************/
    /****************************API21以上设置代理***************************/
    /************************************************************************/

    @TargetApi(21)
    public static ProxyInfo getProxyInfoFor5x(WifiConfiguration configuration) {
        try {
            if (configuration != null) {
                Class<?> clazz = Class.forName("android.net.wifi.WifiConfiguration");
                Method method = clazz.getMethod("getHttpProxy");
                ProxyInfo info = (ProxyInfo)method.invoke(configuration);
                return info;
            }
        } catch (Throwable e) {
        }
        return null;
    }

    /**
     * 5.x取消代理设置
     */
    @TargetApi(21)
    static void resetSetHttpProxyFor5x(Context context) {

        ProxyInfo mInfo = null;
        try {
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration configuration = getCurrentWifiConfiguration(wifiManager);

            ProxyInfo info = getProxyInfoFor5x(configuration);
            if (info != null) {
                if (info.getHost() == null && info.getPort() == 0) {
                    L.wtf("resetSetHttpProxyFor5x(). 没有设置代理....");
                    return;
                }
            }

            mInfo = ProxyInfo.buildDirectProxy(null, 0);
            if (configuration != null) {
                Class<?> clazz = Class.forName("android.net.wifi.WifiConfiguration");
                Class<?> parmars = Class.forName("android.net.ProxyInfo");
                Method method = clazz.getMethod("setHttpProxy", parmars);
                method.invoke(configuration, mInfo);
                Object mIpConfiguration = getDeclaredField(configuration, "mIpConfiguration");
                setEnumField(mIpConfiguration, "NONE", "proxySettings");
                setDeclardFildObject(configuration, "mIpConfiguration", mIpConfiguration);

                // 保存设置
                wifiManager.updateNetwork(configuration);
            }
        } catch (Exception e) {
        }

    }

    /**
     * 5.x 系列设置代理
     * 7.1.1 25以后不能正常工作.
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
            ProxyInfo info = getProxyInfoFor5x(config);
            if (info != null) {
                String s = info.getHost();
                int p = info.getPort();
                if (p == port && s.equals(host)) {
                    L.wtf("setWifiProxyFor5x(). 已经设置相同代理...");
                    return;
                }
            }
            mInfo = ProxyInfo.buildDirectProxy(host, port);
            if (config != null) {
                Class<?> clazz = Class.forName("android.net.wifi.WifiConfiguration");
                Class<?> parmars = Class.forName("android.net.ProxyInfo");
                Method method = clazz.getMethod("setHttpProxy", parmars);
                method.invoke(config, mInfo);
                Object mIpConfiguration = getDeclaredField(config, "mIpConfiguration");

                setEnumField(mIpConfiguration, "STATIC", "proxySettings");
                setDeclardFildObject(config, "mIpConfiguration", mIpConfiguration);
                // save the settings
                wifiManager.updateNetwork(config);
            }
        } catch (Throwable e) {
            L.e(e);
        }
    }

    /**
     * 拦截<code>WifiConfiguration.setProxy(ProxySettings settings, ProxyInfo proxy)</code>
     *
     * @param context
     */
    @TargetApi(21)
    public static void setWifiProxyFor5xPlanB(Context context) {
        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = getCurrentWifiConfiguration(manager);
        try {
            if (null != config) {
                Class proxySettings = Class.forName("android.net.IpConfiguration$ProxySettings");
                Class[] setProxyParams = new Class[2];
                setProxyParams[0] = proxySettings;
                setProxyParams[1] = ProxyInfo.class;

                Method setProxy = config.getClass().getDeclaredMethod("setProxy", setProxyParams);
                setProxy.setAccessible(true);

                ProxyInfo desiredProxy = ProxyInfo.buildDirectProxy("30.30.142.01", 30);

                Object[] methodParams = new Object[2];
                methodParams[0] = Enum.valueOf(proxySettings, "STATIC");
                methodParams[1] = desiredProxy;

                setProxy.invoke(config, methodParams);

                // save the settings
                manager.updateNetwork(config);
            }
        } catch (Throwable e) {
        }
    }

    /**
     * can't work . updateNetwork failed
     *
     * @param context
     * @param port
     * @param ip
     */
    public static void setWifiProxyFor7X(Context context, String port, int ip) {
        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = getCurrentWifiConfiguration(manager);
        try {
            if (null != config) {

                ProxyInfo desiredProxy = ProxyInfo.buildDirectProxy(port, ip);

                Object mIpConfiguration = getDeclaredField(config, "mIpConfiguration");
                Class<?> clazz = Class.forName("android.net.IpConfiguration");
                Method mm = clazz.getMethod("setHttpProxy", ProxyInfo.class);
                mm.invoke(mIpConfiguration, desiredProxy);
                //setDeclardFildObject(config, "mIpConfiguration", mIpConfiguration);

                setEnumField(mIpConfiguration, "STATIC", "ipAssignment");
                Class wifiConfiguration = Class.forName("android.net.wifi.WifiConfiguration");
                Class<?> parmars = Class.forName("android.net.IpConfiguration");
                Method ssa = wifiConfiguration.getMethod("setIpConfiguration", parmars);

                ssa.invoke(config, mIpConfiguration);

                L.i("=======>" + config.networkId);
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                // save the settings. failed  return -1
                int res = manager.updateNetwork(config);
                //manager.disconnect();
                //manager.reconnect();

                L.d("updateNetwork: " + res);
                config = getCurrentWifiConfiguration(manager);
                L.d(config.toString());
            }
        } catch (Throwable e) {
            L.e(e);
        }
    }

    /**
     * 获取当前的Wifi连接
     *
     * @param wifiManager
     * @return
     */
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

    static void setEnumField(Object obj, String value, String name)
        throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>)f.getType(), value));
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

    static Object getField(Object obj, String name) {
        if (obj == null) {
            return null;
        }
        Object out = null;
        try {
            Field f = obj.getClass().getField(name);
            out = f.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

}
