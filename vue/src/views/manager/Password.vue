<template>
  <div style="width: 50%">
    <div class="card" style="padding: 30px">
      <el-form :model="pwdForm" label-width="100px" style="padding-right: 50px">
        <el-form-item label="原密码">
          <el-input v-model="pwdForm.oldPassword" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" show-password />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="pwdForm.confirmPassword" show-password />
        </el-form-item>
        <div style="text-align: center">
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import {reactive} from "vue"
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import router from "@/router";


// 从缓存取登录用户
const user = JSON.parse(localStorage.getItem('system-user') || '{}');

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})


// 把当前修改的用户信息存储到后台数据库
const save = () => {

  //  确认密码一致性校验
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致');
    return;
  }

  // 构造传给后端的参数：username+明文原密码+明文新密码
  const params = {
    username: user.username, //后端根据用户名查用户
    password: pwdForm.oldPassword, // 明文原密码
    newPassword: pwdForm.newPassword, // 明文新密码
    role:user.role //后端区别管理员与用户
  }

  request.put('/updatePassword', params).then(res => {
    if (res.code === '200') {
      ElMessage.success('修改密码成功')
      //清空缓存
      localStorage.removeItem('system-user')
      router.push('/login')
    } else {
      ElMessage.error(res.msg)
    }
  })
}
</script>