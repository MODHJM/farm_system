package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 普通用户业务处理
 **/
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    // 自定义MD5盐值（项目唯一，可自行修改，越长越安全）
    private static final String MD5_SALT = "ncpSystem_2026_user_pwd";

    /**
     * 密码加密工具方法（全局统一，MD5+固定盐值）
     */
    private String encryptPwd(String plainPwd) {
        // 明文密码拼接盐值后做MD5加密，生成32位密文
        return SecureUtil.md5(plainPwd + MD5_SALT);
    }

    /**
     * 新增
     */
    public void add(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if (ObjectUtil.isNotNull(dbUser)) {
            throw new CustomException("用户已存在");
        }
        if (ObjectUtil.isEmpty(user.getPassword())) {
            user.setPassword("123");
        }
        user.setPassword(encryptPwd(user.getPassword()));
        if (ObjectUtil.isEmpty(user.getName())) {
            user.setName(user.getUsername());
        }
        user.setRole("USER");
        userMapper.insert(user);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    /**
     * 修改
     */
    public void updateById(User user) {

        userMapper.updateById(user);
    }

    /**
     * 根据ID查询
     */
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    /**
     * 分页查询
     */
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll(user);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Account dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException("用户不存在");
        }
        // 核心：前端明文密码加密后，和数据库密文对比
        String encryptPwd = encryptPwd(account.getPassword());
        if (!encryptPwd.equals(dbUser.getPassword())) {
            throw new CustomException("账号或密码错误");
        }

        return dbUser;
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException("用户不存在");
        }

        String oldEncryptPwd = encryptPwd(account.getPassword());
        if (!oldEncryptPwd.equals(dbUser.getPassword())) {
            throw new CustomException("原密码错误");
        }

        // 新密码加密后存入数据库
        dbUser.setPassword(encryptPwd(account.getNewPassword()));
        userMapper.updateByPassword(dbUser);

    }

/**
*  注册
*/
    public void register(User user) {
       this.add(user);
    }
}