package com.airxiechao.j20.auth.service;

import com.airxiechao.j20.auth.api.pojo.User;
import com.airxiechao.j20.auth.api.service.IUserService;
import com.airxiechao.j20.auth.db.record.UserRecord;
import com.airxiechao.j20.auth.db.repository.IUserRepository;
import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.util.ApplicationContextUtil;
import com.airxiechao.j20.common.util.CodecUtil;
import com.airxiechao.j20.common.util.UuidUtil;

import java.util.Date;

public class UserService implements IUserService {

    private IUserRepository userRepository = ApplicationContextUtil.getContext().getBean(IUserRepository.class);

    @Override
    public boolean exists(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public User getByUsername(String username) throws NotFoundException {
        UserRecord userRecord = userRepository.findFirstByUsernameIgnoreCase(username);
        if(null == userRecord){
            throw new NotFoundException();
        }

        return buildUser(userRecord);
    }

    @Override
    public boolean validatePassword(String username, String password) {
        try {
            User user = getByUsername(username);
            return user.getPassword().equals(encodePassword(password));
        } catch (NotFoundException e) {
            return false;
        }
    }

    @Override
    public User add(User user) {
        Date now = new Date();

        user.setId(UuidUtil.random());
        user.setPassword(encodePassword(user.getPassword()));
        user.setCreateTime(now);
        user.setLastUpdateTime(now);

        UserRecord record = userRepository.save(buildUserRecord(user));
        return buildUser(record);
    }

    @Override
    public User updatePassword(String username, String password) throws NotFoundException {
        User user = getByUsername(username);
        user.setPassword(encodePassword(password));
        user.setLastUpdateTime(new Date());

        UserRecord record = userRepository.save(buildUserRecord(user));
        return buildUser(record);
    }

    private String encodePassword(String password){
        return CodecUtil.md5(String.format("#%s#j20#", password));
    }

    private User buildUser(UserRecord record){
        return new User(
                record.getId(),
                record.getUsername(),
                record.getPassword(),
                record.getCreateTime(),
                record.getLastUpdateTime()
        );
    }

    private UserRecord buildUserRecord(User user){
        UserRecord record = new UserRecord();
        record.setId(user.getId());
        record.setUsername(user.getUsername());
        record.setPassword(user.getPassword());
        record.setCreateTime(user.getCreateTime());
        record.setLastUpdateTime(user.getLastUpdateTime());

        return record;
    }
}
