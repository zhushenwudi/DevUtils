package dev.utils.app.assist;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

import dev.DevUtils;
import dev.utils.LogPrintUtils;

/**
 * detail: 屏幕传感器辅助类 ( 监听是否横竖屏 )
 * @author Ttt
 */
public final class ScreenSensorAssist {

    // 日志 TAG
    private static final String TAG = ScreenSensorAssist.class.getSimpleName();

    // ======================
    // = 重力传感器监听对象 =
    // ======================

    // 传感器管理对象
    private SensorManager mSensorManager;
    // 重力传感器
    private Sensor mSensor;
    // 重力传感器监听事件
    private OrientationSensorListener mListener;

    // ===================================================
    // = 重力传感器监听对象 ( 改变方向后, 判断参数不同 ) =
    // ===================================================

    // 传感器管理对象 ( 切屏后 )
    private SensorManager mSensorManagerChange;
    // 重力传感器监听事件 ( 切屏后 )
    private OrientationSensorChangeListener mListenerChange;

    // ========
    // = 常量 =
    // ========

    // 坐标索引常量
    private final int DATA_X = 0;
    private final int DATA_Y = 1;
    private final int DATA_Z = 2;
    // 方向未知常量
    private final int ORIENTATION_UNKNOWN = -1;
    // 触发屏幕方向改变回调
    public static final int CHANGE_ORIENTATION_WHAT = 9919;

    // ========
    // = 变量 =
    // ========

    // 是否允许切屏
    private boolean mAllowChange = false;
    // 是否是竖屏
    private boolean mPortrait = true;
    // 回调操作
    private Handler mHandler;

    /**
     * 角度处理 Handler
     */
    private Handler mRotateHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_ORIENTATION_WHAT:
                    // 获取角度
                    int rotation = msg.arg1;
                    // =
                    LogPrintUtils.dTag(TAG, "当前角度: " + rotation);
                    // 判断角度
                    if (rotation > 45 && rotation < 135) { // 横屏 - 屏幕对着别人
                        LogPrintUtils.dTag(TAG, "切换成横屏 - 屏幕对着自己");
                        // =
                        if (mPortrait) {
                            mPortrait = false;
                            if (mHandler != null) {
                                Message vMsg = new Message();
                                vMsg.what = CHANGE_ORIENTATION_WHAT;
                                vMsg.arg1 = 1;
                                mHandler.sendMessage(vMsg);
                            }
                        }
                    } else if (rotation > 135 && rotation < 225) { // 竖屏 - 屏幕对着别人
                        LogPrintUtils.dTag(TAG, "切换成竖屏 - 屏幕对着别人");
                        // =
                        if (!mPortrait) {
                            mPortrait = true;
                            if (mHandler != null) {
                                Message vMsg = new Message();
                                vMsg.what = CHANGE_ORIENTATION_WHAT;
                                vMsg.arg1 = 2;
                                mHandler.sendMessage(vMsg);
                            }
                        }
                    } else if (rotation > 225 && rotation < 315) { // 横屏 - 屏幕对着自己
                        LogPrintUtils.dTag(TAG, "切换成横屏 - 屏幕对着自己");
                        // =
                        if (mPortrait) {
                            mPortrait = false;
                            if (mHandler != null) {
                                Message vMsg = new Message();
                                vMsg.what = CHANGE_ORIENTATION_WHAT;
                                vMsg.arg1 = 1;
                                mHandler.sendMessage(vMsg);
                            }
                        }
                    } else if ((rotation > 315 && rotation < 360) || (rotation > 0 && rotation < 45)) { // 竖屏 - 屏幕对着自己
                        LogPrintUtils.dTag(TAG, "切换成竖屏 - 屏幕对着自己");
                        // =
                        if (!mPortrait) {
                            mPortrait = true;
                            if (mHandler != null) {
                                Message vMsg = new Message();
                                vMsg.what = CHANGE_ORIENTATION_WHAT;
                                vMsg.arg1 = 2;
                                mHandler.sendMessage(vMsg);
                            }
                        }
                    } else {
                        LogPrintUtils.dTag(TAG, "其他角度: " + rotation);
                    }
                    break;
            }
        }
    };

    // =

    /**
     * 初始化操作
     * @param handler 回调 {@link Handler}
     */
    private void init(final Handler handler) {
        this.mHandler = handler;
        // 注册重力感应器, 监听屏幕旋转
        mSensorManager = getSensorManager();
        mListener = new OrientationSensorListener();
        // 根据 旋转之后、点击全屏之后 两者方向一致, 激活 SensorManager
        mSensorManagerChange = getSensorManager();
        mListenerChange = new OrientationSensorChangeListener();
        // 设置传感器
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * 开始监听
     * @param handler 回调 {@link Handler}
     * @return {@code true} success, {@code false} fail
     */
    public boolean start(final Handler handler) {
        mAllowChange = true;
        try {
            LogPrintUtils.dTag(TAG, "start orientation listener.");
            // 初始化操作
            init(handler);
            // 监听重力传感器
            mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_UI);
            return true;
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "start");
        }
        return false;
    }

    /**
     * 停止监听
     * @return {@code true} success, {@code false} fail
     */
    public boolean stop() {
        mAllowChange = false;
        LogPrintUtils.dTag(TAG, "stop orientation listener.");
        try {
            mSensorManager.unregisterListener(mListener);
        } catch (Exception e) {
        }
        try {
            mSensorManagerChange.unregisterListener(mListenerChange);
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * 是否竖屏
     * @return {@code true} 竖屏, {@code false} 非竖屏
     */
    public boolean isPortrait() {
        return this.mPortrait;
    }

    /**
     * 是否允许切屏
     * @return {@code true} 允许, {@code false} 不允许
     */
    public boolean isAllowChange() {
        return this.mAllowChange;
    }

    // =

    /**
     * detail: 重力传感器监听事件
     * @author Ttt
     */
    class OrientationSensorListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[DATA_X];
            float Y = -values[DATA_Y];
            float Z = -values[DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (mRotateHandler != null) {
                mRotateHandler.obtainMessage(CHANGE_ORIENTATION_WHAT, orientation, 0).sendToTarget();
            }
        }
    }

    /**
     * detail: 重力传感器监听事件 ( 切屏后 )
     * @author Ttt
     */
    class OrientationSensorChangeListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[DATA_X];
            float Y = -values[DATA_Y];
            float Z = -values[DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (orientation > 225 && orientation < 315) { // 检测到当前实际是横屏
                if (!mPortrait) {
                    mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_UI);
                    mSensorManagerChange.unregisterListener(mListenerChange);
                }
            } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) { // 检测到当前实际是竖屏
                if (mPortrait) {
                    mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_UI);
                    mSensorManagerChange.unregisterListener(mListenerChange);
                }
            }
        }
    }

    // ======================
    // = 其他工具类实现代码 =
    // ======================

    // ============
    // = AppUtils =
    // ============

    /**
     * 获取 SensorManager
     * @return {@link SensorManager}
     */
    private static SensorManager getSensorManager() {
        return getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * 获取 SystemService
     * @param name 服务名
     * @param <T>  泛型
     * @return SystemService Object
     */
    private static <T> T getSystemService(final String name) {
        if (isSpace(name)) return null;
        try {
            return (T) DevUtils.getContext().getSystemService(name);
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "getSystemService");
        }
        return null;
    }

    // ===============
    // = StringUtils =
    // ===============

    /**
     * 判断字符串是否为 null 或全为空白字符
     * @param str 待校验字符串
     * @return {@code true} yes, {@code false} no
     */
    private static boolean isSpace(final String str) {
        if (str == null) return true;
        for (int i = 0, len = str.length(); i < len; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}