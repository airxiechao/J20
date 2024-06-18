package com.airxiechao.j20.common.cache;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;

/**
 * 过期缓存数据项的实现
 * @param <T> 缓存数据类型
 */
@Data
public class ExpireCacheItem<T> {
    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 数据项
     */
    private T item;

    /**
     * 构造过期缓存数据项
     * @param item 数据项
     * @param expireSeconds 过期秒数
     */
    public ExpireCacheItem(T item, int expireSeconds) {
        this.expireTime = buildExpireTime(expireSeconds);
        this.item = item;
    }

    /**
     * 返回是否过期
     * @return 是否过期
     */
    public boolean isExpire(){
        return expireTime.before(new Date());
    }

    /**
     * 从过期秒数构造过期时间
     * @param expireSeconds 过期秒数
     * @return 过期时间
     */
    private Date buildExpireTime(int expireSeconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, expireSeconds);

        return calendar.getTime();
    }
}
