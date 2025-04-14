package com.sky.context;

public class BaseContext {

    //创建ThreadLocal静态对象
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 包装ThreadLocal.set方法 存储当前用户的id
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 包装ThreadLocal.get方法 获取当前用户id
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 包装ThreadLocal.remove方法 移除ThreadLocal中存储的值
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
