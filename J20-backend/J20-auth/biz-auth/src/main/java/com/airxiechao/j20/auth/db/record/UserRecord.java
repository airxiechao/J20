package com.airxiechao.j20.auth.db.record;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户记录
 */
@Data
@Entity(name = "t_user")
public class UserRecord {
    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 用户名
     */
    @Column(unique = true)
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后修改时间
     */
    private Date lastUpdateTime;
}
