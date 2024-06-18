package com.airxiechao.j20.auth.db.repository;

import com.airxiechao.j20.auth.db.record.UserRecord;
import org.springframework.data.repository.CrudRepository;

/**
 * 用户记录接口
 */
public interface IUserRepository extends CrudRepository<UserRecord, String> {
    boolean existsByUsernameIgnoreCase(String username);
    UserRecord findFirstByUsernameIgnoreCase(String username);
}
