package com.airxiechao.j20.detection.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 速度计数器
 */
public class SpeedCounter {
    /**
     * 计数开始时间戳
     */
    private AtomicLong millis = new AtomicLong(0);

    /**
     * 计数
     */
    private AtomicLong num = new AtomicLong(0);

    /**
     * 获取时间戳
     * @return 时间戳
     */
    public long getMillis(){
        return millis.get();
    }

    /**
     * 添加数量
     * @param count 数量
     */
    public void addNum(int count){
        num.getAndAdd(count);
    }

    /**
     * 重置
     * @param millis 时间戳
     */
    public void reset(long millis){
        num.set(0);
        this.millis.set(millis);
    }

    /**
     * 计算 DPS
     * @param millis 时间戳
     * @return DPS
     */
    public double getDps(long millis){
        double dps = num.get() / ((millis - this.millis.get()) / 1000.0);
        return dps;
    }


}
