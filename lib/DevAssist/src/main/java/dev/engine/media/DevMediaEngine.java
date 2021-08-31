package dev.engine.media;

import java.util.Map;

import dev.engine.DevEngineAssist;

/**
 * detail: Media Selector Engine
 * @author Ttt
 */
public final class DevMediaEngine {

    private DevMediaEngine() {
    }

    private static final DevEngineAssist<IMediaEngine> sAssist = new DevEngineAssist<>();

    /**
     * 获取 Engine
     * @return {@link IMediaEngine}
     */
    public static IMediaEngine getEngine() {
        return sAssist.getEngine();
    }

    /**
     * 获取 Engine
     * @param key key
     * @return {@link IMediaEngine}
     */
    public static IMediaEngine getEngine(final String key) {
        return sAssist.getEngine(key);
    }

    /**
     * 设置 Engine
     * @param engine {@link IMediaEngine}
     * @return {@link IMediaEngine}
     */
    public static IMediaEngine setEngine(final IMediaEngine engine) {
        return sAssist.setEngine(engine);
    }

    /**
     * 设置 Engine
     * @param key    key
     * @param engine {@link IMediaEngine}
     * @return {@link IMediaEngine}
     */
    public static IMediaEngine setEngine(
            final String key,
            final IMediaEngine engine
    ) {
        return sAssist.setEngine(key, engine);
    }

    /**
     * 移除 Engine
     */
    public static void removeEngine() {
        sAssist.removeEngine();
    }

    /**
     * 移除 Engine
     * @param key key
     */
    public static void removeEngine(final String key) {
        sAssist.removeEngine(key);
    }

    // ==========
    // = 其他方法 =
    // ==========

    /**
     * 获取 DevEngine Generic Assist
     * @return DevEngine Generic Assist
     */
    public static DevEngineAssist<IMediaEngine> getAssist() {
        return sAssist;
    }

    /**
     * 获取 Engine Map
     * @return Engine Map
     */
    public static Map<String, IMediaEngine> getEngineMaps() {
        return sAssist.getEngineMaps();
    }

    /**
     * 是否存在 Engine
     * @return {@code true} yes, {@code false} no
     */
    public static boolean contains() {
        return sAssist.contains();
    }

    /**
     * 是否存在 Engine
     * @param key key
     * @return {@code true} yes, {@code false} no
     */
    public static boolean contains(final String key) {
        return sAssist.contains(key);
    }

    /**
     * 判断 Engine 是否为 null
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isEmpty() {
        return sAssist.isEmpty();
    }

    /**
     * 判断 Engine 是否为 null
     * @param key key
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isEmpty(final String key) {
        return sAssist.isEmpty(key);
    }
}