package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理员业务处理
 **/
@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper;

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
    public void add(Admin admin) {
        Admin dbAdmin = adminMapper.selectByUsername(admin.getUsername());
        if (ObjectUtil.isNotNull(dbAdmin)) {
            throw new CustomException("用户已存在");
        }
        if (ObjectUtil.isEmpty(admin.getPassword())) {
            admin.setPassword("admin");
        }
        admin.setPassword(encryptPwd(admin.getPassword()));
        if (ObjectUtil.isEmpty(admin.getName())) {
            admin.setName(admin.getUsername());
        }
        admin.setRole("ADMIN");
        adminMapper.insert(admin);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }

    /**
     * 修改
     */
    public void updateById(Admin admin) {
        if (ObjectUtil.isNotEmpty(admin.getPassword())) {
            admin.setPassword(encryptPwd(admin.getPassword()));
        }

        adminMapper.updateById(admin);
    }

    /**
     * 根据ID查询
     */
    public Admin selectById(Integer id) {
        return adminMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Admin> selectAll(Admin admin) {
        return adminMapper.selectAll(admin);
    }

    /**
     * 分页查询
     */
    public PageInfo<Admin> selectPage(Admin admin, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> list = adminMapper.selectAll(admin);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Account dbAdmin = adminMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException("用户不存在");
        }

        // 核心：前端明文密码加密后，和数据库密文对比
        String encryptPwd = encryptPwd(account.getPassword());
        if (!encryptPwd.equals(dbAdmin.getPassword())) {
            throw new CustomException("账号或密码错误");
        }
        return dbAdmin;
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        Admin dbAdmin = adminMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException("用户不存在");
        }

        // 核心：原密码明文加密后，和数据库密文对比
        String oldEncryptPwd = encryptPwd(account.getPassword());
        if (!oldEncryptPwd.equals(dbAdmin.getPassword())) {
            throw new CustomException("原密码错误");
        }

        // 新密码加密后存入数据库
        dbAdmin.setPassword(encryptPwd(account.getNewPassword()));
        adminMapper.updateByPassword(dbAdmin);

    }

}