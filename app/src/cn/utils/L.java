package cn.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Formatter;
import java.util.Locale;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @Copyright © 2015 Sanbo Inc. All rights reserved.
 * @Description              <pre>
 * Log统一管理类,提供功能：
 * 1.log工具类支持全部打印
 * 2.支持类似C的格式化输出或Java的String.format
 * 3.支持Java堆栈打印
 * 4.支持键入和不键入TAG(不键入tag,tag是sanbo)
 * 5.支持shell控制log是否打印
 *              </pre>
 * @Version: 1.0
 * @Create: 2015年6月18日 下午4:14:01
 * @Author: sanbo
 */
public class L {

    private L() {
    }

    // 是否打印bug，可以在application的onCreate函数里面初始化
    public static boolean isDebug = true;
    // 是否接受shell控制打印
    public static boolean isControl = true;
    private static String DEFAULT_TAG = "sanbo";
    // 规定每段显示的长度
    private static int LOG_MAXLENGTH = 2000;

    public static final class MLEVEL {
        public static final int VERBOSE = 0x1;
        public static final int DEBUG = 0x2;
        public static final int INFO = 0x3;
        public static final int WARN = 0x4;
        public static final int ERROR = 0x5;
        public static final int WTF = 0x6;
    }

    public static void v(Object obj) {
        if (obj != null) {
            if (obj instanceof String) {
                v(DEFAULT_TAG, (String)obj, null);
            } else if (obj instanceof Throwable) {
                v(DEFAULT_TAG, null, (Throwable)obj);
            } else {
                print(MLEVEL.VERBOSE, obj, null, null);
            }
        }
    }

    public static void d(Object obj) {
        if (obj != null) {
            if (obj instanceof String) {
                d(DEFAULT_TAG, (String)obj, null);
            } else if (obj instanceof Throwable) {
                d(DEFAULT_TAG, null, (Throwable)obj);
            } else {
                print(MLEVEL.DEBUG, obj, null, null);
            }
        }
    }

    public static void w(Object obj) {
        if (obj != null) {
            if (obj instanceof String) {
                w(DEFAULT_TAG, (String)obj, null);
            } else if (obj instanceof Throwable) {
                w(DEFAULT_TAG, null, (Throwable)obj);
            } else {
                print(MLEVEL.WARN, obj, null, null);
            }
        }
    }

    public static void i(Object obj) {
        if (obj != null) {
            if (obj instanceof String) {
                i(DEFAULT_TAG, (String)obj, null);
            } else if (obj instanceof Throwable) {
                i(DEFAULT_TAG, null, (Throwable)obj);
            } else {
                print(MLEVEL.INFO, obj, null, null);
            }
        }
    }

    public static void e(Object obj) {
        if (obj != null) {
            if (obj instanceof String) {
                e(DEFAULT_TAG, (String)obj, null);
            } else if (obj instanceof Throwable) {
                Log.e(DEFAULT_TAG, " throable");
                e(DEFAULT_TAG, null, (Throwable)obj);
            } else {
                print(MLEVEL.ERROR, obj, null, null);
            }
        }
    }

    public static void wtf(Object obj) {
        if (obj != null) {
            if (obj instanceof String) {
                wtf(DEFAULT_TAG, (String)obj, null);
            } else if (obj instanceof Throwable) {
                wtf(DEFAULT_TAG, null, (Throwable)obj);
            } else {
                print(MLEVEL.WTF, obj, null, null);
            }
        }
    }

    public static void i(String msg, Throwable e) {
        i(DEFAULT_TAG, msg, e);
    }

    public static void v(String msg, Throwable e) {
        v(DEFAULT_TAG, msg, e);
    }

    public static void w(String msg, Throwable e) {
        w(DEFAULT_TAG, msg, e);
    }

    public static void d(String msg, Throwable e) {
        d(DEFAULT_TAG, msg, e);
    }

    public static void e(String msg, Throwable e) {
        e(DEFAULT_TAG, msg, e);
    }

    public static void wtf(String msg, Throwable e) {
        wtf(DEFAULT_TAG, msg, e);
    }

    public static void v(String tag, Object obj, Throwable e) {
        if (obj != null) {
            if (obj instanceof String) {
                print(MLEVEL.VERBOSE, (String)obj, e, tag);
            } else {
                if (e == null) {
                    print(MLEVEL.VERBOSE, obj, e, tag);
                } else {
                    print(MLEVEL.VERBOSE, obj, e, tag);
                }
            }
        } else {
            if (e != null) {
                print(MLEVEL.VERBOSE, null, e, tag);
            }
        }

    }

    public static void wtf(String tag, Object obj, Throwable e) {
        if (obj != null) {
            if (obj instanceof String) {
                print(MLEVEL.WTF, (String)obj, e, tag);
            } else {
                if (e == null) {
                    print(MLEVEL.WTF, obj, e, tag);
                } else {
                    print(MLEVEL.WTF, obj, e, tag);
                }
            }
        } else {
            if (e != null) {
                print(MLEVEL.WTF, null, e, tag);
            }
        }
    }

    public static void d(String tag, Object obj, Throwable e) {
        if (obj != null) {
            if (obj instanceof String) {
                print(MLEVEL.DEBUG, (String)obj, e, tag);
            } else {
                if (e == null) {
                    print(MLEVEL.DEBUG, obj, e, tag);
                } else {
                    print(MLEVEL.DEBUG, obj, e, tag);
                }
            }
        } else {
            if (e != null) {
                print(MLEVEL.DEBUG, null, e, tag);
            }
        }
    }

    public static void i(String tag, Object obj, Throwable e) {
        if (obj != null) {
            if (obj instanceof String) {
                print(MLEVEL.INFO, (String)obj, e, tag);
            } else {
                if (e == null) {
                    print(MLEVEL.INFO, obj, e, tag);
                } else {
                    print(MLEVEL.INFO, obj, e, tag);
                }
            }
        } else {
            if (e != null) {
                print(MLEVEL.INFO, null, e, tag);
            }
        }
    }

    public static void w(String tag, Object obj, Throwable e) {
        if (obj != null) {
            if (obj instanceof String) {
                print(MLEVEL.WARN, (String)obj, e, tag);
            } else {
                if (e == null) {
                    print(MLEVEL.WARN, obj, e, tag);
                } else {
                    print(MLEVEL.WARN, obj, e, tag);
                }
            }
        } else {
            if (e != null) {
                print(MLEVEL.WARN, null, e, tag);
            }
        }
    }

    public static void e(String tag, Object obj, Throwable e) {
        if (obj != null) {
            if (obj instanceof String) {
                print(MLEVEL.ERROR, (String)obj, e, tag);
            } else {
                if (e == null) {
                    print(MLEVEL.ERROR, obj, e, tag);
                } else {
                    print(MLEVEL.ERROR, obj, e, tag);
                }
            }
        } else {
            if (e != null) {
                print(MLEVEL.ERROR, null, e, tag);
            }
        }
    }

    /******************************************************************************************/
    /********************************* 该部分是多参数设置的 ******************************************/
    /******************************************************************************************/
    public static void d(String format, Object... args) {
        try {
            StringBuilder sb = new StringBuilder();
            if (format.contains("%")) {
                format = new Formatter(Locale.getDefault()).format(format, args).toString();
                sb.append(format).append("\n");

            } else {
                if (args != null) {
                    if (!TextUtils.isEmpty(format)) {
                        sb.append(format).append("\n");
                    }
                }
            }
            Throwable e = null;
            for (Object obj : args) {
                if (obj instanceof String) {
                    sb.append((String)obj).append("\n");
                } else if (obj instanceof JSONObject) {
                    sb.append(format((JSONObject)obj)).append("\n");
                } else if (obj instanceof JSONArray) {
                    sb.append(format((JSONArray)obj)).append("\n");
                } else if (obj instanceof Throwable) {
                    e = (Throwable)obj;
                } else {
                    sb.append(obj.toString()).append("\n");
                }
            }
            print(MLEVEL.DEBUG, sb.toString(), e, null);
        } catch (Exception e) {
        }
    }

    public static void wtf(String format, Object... args) {
        try {
            StringBuilder sb = new StringBuilder();
            if (format.contains("%")) {
                format = new Formatter(Locale.getDefault()).format(format, args).toString();
                sb.append(format).append("\n");

            } else {
                if (args != null) {
                    if (!TextUtils.isEmpty(format)) {
                        sb.append(format).append("\n");
                    }
                }
            }
            Throwable e = null;
            for (Object obj : args) {
                if (obj instanceof String) {
                    sb.append((String)obj).append("\n");
                } else if (obj instanceof JSONObject) {
                    sb.append(format((JSONObject)obj)).append("\n");
                } else if (obj instanceof JSONArray) {
                    sb.append(format((JSONArray)obj)).append("\n");
                } else if (obj instanceof Throwable) {
                    e = (Throwable)obj;
                } else {
                    sb.append(obj.toString()).append("\n");
                }
            }
            print(MLEVEL.WTF, sb.toString(), e, null);
        } catch (Exception e) {
        }
    }

    public static void i(String format, Object... args) {
        try {
            StringBuilder sb = new StringBuilder();
            if (format.contains("%")) {
                format = new Formatter(Locale.getDefault()).format(format, args).toString();
                sb.append(format).append("\n");

            } else {
                if (args != null) {
                    if (!TextUtils.isEmpty(format)) {
                        sb.append(format).append("\n");
                    }
                }
            }
            Throwable e = null;
            for (Object obj : args) {
                if (obj instanceof String) {
                    sb.append((String)obj).append("\n");
                } else if (obj instanceof JSONObject) {
                    sb.append(format((JSONObject)obj)).append("\n");
                } else if (obj instanceof JSONArray) {
                    sb.append(format((JSONArray)obj)).append("\n");
                } else if (obj instanceof Throwable) {
                    e = (Throwable)obj;
                } else {
                    sb.append(obj.toString()).append("\n");
                }
            }
            print(MLEVEL.INFO, sb.toString(), e, null);
        } catch (Exception e) {
        }
    }

    public static void w(String format, Object... args) {
        try {
            StringBuilder sb = new StringBuilder();
            if (format.contains("%")) {
                format = new Formatter(Locale.getDefault()).format(format, args).toString();
                sb.append(format).append("\n");

            } else {
                if (args != null) {
                    if (!TextUtils.isEmpty(format)) {
                        sb.append(format).append("\n");
                    }
                }
            }
            Throwable e = null;
            for (Object obj : args) {
                if (obj instanceof String) {
                    sb.append((String)obj).append("\n");
                } else if (obj instanceof JSONObject) {
                    sb.append(format((JSONObject)obj)).append("\n");
                } else if (obj instanceof JSONArray) {
                    sb.append(format((JSONArray)obj)).append("\n");
                } else if (obj instanceof Throwable) {
                    e = (Throwable)obj;
                } else {
                    sb.append(obj.toString()).append("\n");
                }
            }
            print(MLEVEL.WARN, sb.toString(), e, null);

        } catch (Exception e) {
        }
    }

    public static void v(String format, Object... args) {
        try {

            StringBuilder sb = new StringBuilder();
            if (format.contains("%")) {
                format = new Formatter(Locale.getDefault()).format(format, args).toString();
                sb.append(format).append("\n");

            } else {
                if (args != null) {
                    if (!TextUtils.isEmpty(format)) {
                        sb.append(format).append("\n");
                    }
                }
            }
            Throwable e = null;
            for (Object obj : args) {
                if (obj instanceof String) {
                    sb.append((String)obj).append("\n");
                } else if (obj instanceof JSONObject) {
                    sb.append(format((JSONObject)obj)).append("\n");
                } else if (obj instanceof JSONArray) {
                    sb.append(format((JSONArray)obj)).append("\n");
                } else if (obj instanceof Throwable) {
                    e = (Throwable)obj;
                } else {
                    sb.append(obj.toString()).append("\n");
                }
            }
            print(MLEVEL.VERBOSE, sb.toString(), e, null);

        } catch (Exception e) {
        }
    }

    public static void e(String format, Object... args) {
        try {
            StringBuilder sb = new StringBuilder();
            if (format.contains("%")) {
                format = new Formatter(Locale.getDefault()).format(format, args).toString();
                sb.append(format).append("\n");

            } else {
                if (args != null) {
                    if (!TextUtils.isEmpty(format)) {
                        sb.append(format).append("\n");
                    }
                }
            }
            Throwable e = null;
            for (Object obj : args) {
                if (obj instanceof String) {
                    sb.append((String)obj).append("\n");
                } else if (obj instanceof JSONObject) {
                    sb.append(format((JSONObject)obj)).append("\n");
                } else if (obj instanceof JSONArray) {
                    sb.append(format((JSONArray)obj)).append("\n");
                } else if (obj instanceof Throwable) {
                    e = (Throwable)obj;
                } else {
                    sb.append(obj.toString()).append("\n");
                }
            }
            print(MLEVEL.ERROR, sb.toString(), e, null);

        } catch (Exception e) {
        }
    }

    /**
     * 转换json为字符串
     *
     * @param level 打印的log
     * @param obj   打印的消息体
     * @param e     打印的堆栈信息
     * @param tag   打印使用的临时tag
     */
    private static void print(int level, Object obj, Throwable e, String tag) {
        if (isDebug) {
            String msg = "";
            if (obj != null) {
                if (obj instanceof JSONObject) {
                    msg = format((JSONObject)obj);
                } else if (obj instanceof JSONArray) {
                    msg = format((JSONArray)obj);
                } else if (obj instanceof String) {
                    msg = (String)obj;
                } else {
                    msg = obj.toString();
                }
            } else {
                msg = " the message is null";
            }
            print(level, msg, e, tag);

        }
    }

    /**
     * 区别处理异常、超长log打印
     *
     * @param level 打印log等级
     * @param msg   打印log信息
     * @param e     打印异常信息
     * @param tag   打印使用的tag
     */
    private static void print(int level, String msg, Throwable e, String tag) {
        if (isDebug) {
            tag = (TextUtils.isEmpty(tag) ? DEFAULT_TAG : tag);
            if (!TextUtils.isEmpty(msg)) {
                int strLength = msg.length();
                int start = 0;
                int end = LOG_MAXLENGTH;
                for (int i = 0; i < 100; i++) {
                    // 剩下的文本还是大于规定长度则继续重复截取并输出
                    if (strLength > end) {
                        realSendToCmd(level, tag, msg.substring(start, end));
                        start = end;
                        end = end + LOG_MAXLENGTH;
                    } else {
                        realSendToCmd(level, tag, msg.substring(start, strLength));
                        break;
                    }
                }
            } // end with msg print

            if (e != null) {
                String result = getStackTrace(e);
                if (!TextUtils.isEmpty(result)) {
                    realSendToCmd(level, tag, result);
                }
            }
        }
    }

    /**
     * 真正打印数据. 支持命令行控制log展示,控制命令：setprop log.tag.sanbo log等级.
     * log等级：VERBOSE/DEBUG/INFO/WARN/ERROR/ASSERT
     *
     * @param level
     * @param tag
     * @param logMsg
     */
    private static void realSendToCmd(int level, String tag, String logMsg) {
        switch (level) {
            case MLEVEL.DEBUG:

                if (isControl) {
                    if (Log.isLoggable(tag, Log.DEBUG)) {
                        Log.d(tag, logMsg);
                    }
                } else {
                    Log.d(tag, logMsg);
                }

                break;
            case MLEVEL.INFO:
                if (isControl) {
                    if (Log.isLoggable(tag, Log.INFO)) {
                        Log.i(tag, logMsg);
                    }
                } else {
                    Log.i(tag, logMsg);
                }

                break;
            case MLEVEL.ERROR:
                if (isControl) {
                    if (Log.isLoggable(tag, Log.ERROR)) {
                        Log.e(tag, logMsg);
                    }
                } else {
                    Log.e(tag, logMsg);
                }
                break;
            case MLEVEL.VERBOSE:
                if (isControl) {
                    if (Log.isLoggable(tag, Log.VERBOSE)) {
                        Log.v(tag, logMsg);
                    }
                } else {
                    Log.v(tag, logMsg);
                }

                break;
            case MLEVEL.WARN:
                if (isControl) {
                    if (Log.isLoggable(tag, Log.WARN)) {
                        Log.w(tag, logMsg);
                    }
                } else {
                    Log.w(tag, logMsg);
                }

                break;
            case MLEVEL.WTF:
                if (isControl) {
                    if (Log.isLoggable(tag, Log.ASSERT)) {
                        Log.wtf(tag, logMsg);
                    }
                } else {
                    Log.wtf(tag, logMsg);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 将error转换成字符串
     */

    public static String getStackTrace(Throwable e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        String result = "";
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            result = sw.toString();
        } catch (Throwable error) {
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (Throwable e1) {
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return result;
    }

    /**
     * 格式化输出JSONArray
     *
     * @param arr
     * @return
     */
    public static String format(JSONArray arr) {
        if (arr != null) {
            return format(arr.toString());
        }
        return "";
    }

    /**
     * 格式化输出JSONObject
     *
     * @param obj
     * @return
     */
    public static String format(JSONObject obj) {
        if (obj != null) {
            return format(obj.toString());
        }
        return "";
    }

    private static String SPACE = "    ";

    public static String format(String jsonStr) {
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char llast = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            llast = last;
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '"':
                    if (last == ',' && llast == '}') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    sb.append(current);
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);

                    switch (last) {
                        case '\\':

                            break;
                        case ']':
                            // 支持JsonArray
                            sb.append('\n');
                            addIndentBlank(sb, indent);
                            break;
                        case '"':
                            // 支持json Value里多个,的
                            if (llast != ':') {
                                sb.append('\n');
                                addIndentBlank(sb, indent);
                            }
                            break;

                        default:
                            break;
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * 添加space
     *
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append(SPACE);
        }
    }

}