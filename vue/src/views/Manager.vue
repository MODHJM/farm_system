<template>
  <div>
    <div style=" position: fixed;
        top: 0;
        left: 0;
        right: 0;
        height: 60px;
        background-color: #fff;
        display: flex;
        align-items: center;
        border-bottom: 1px solid #ddd;
        z-index: 100;">

      <div style="flex: 1">
        <div style="padding-left: 20px; display: flex; align-items: center">
          <img src="@/assets/imgs/logo.png" alt="" style="width: 40px">
          <div style="font-weight: bold; font-size: 24px; margin-left: 5px;color:#1450aa">农产品销售系统</div>
        </div>
      </div>
      <div style="width: fit-content; padding-right: 25px; display: flex; align-items: center;">
        <img style="width: 40px; height: 40px; border-radius: 50%" :src="data.user.avatar ||
         'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="">

        <el-dropdown style="margin-left: 10px;" @command="handleDropdown">
          <span class="el-dropdown-link" >{{ data.user.name }}</span>
          <template #dropdown >
            <el-dropdown-menu>
              <el-dropdown-item command="a">个人资料</el-dropdown-item>
              <el-dropdown-item command="b">修改密码</el-dropdown-item>
              <el-dropdown-item command="c">退出系统</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div style="padding-top: 60px; display: flex; min-height: 100vh">
      <div style="position: fixed;
          top: 60px;
          left: 0;
          width: 200px;
          height: calc(100vh - 60px);
          background: #fff;
          border-right: 1px solid #ddd;
          z-index: 99;">
        <el-menu
            router
            style="border: none"
            :default-active="router.currentRoute.value.path"
            :default-openeds="['1', '2']"
        >
          <el-menu-item index="/home">
            <el-icon><HomeFilled /></el-icon>
            <span>系统首页</span>
          </el-menu-item>
          <el-menu-item index="/cartItem" v-if="data.user.role === 'USER'">
            <el-icon><Goods /></el-icon>
            <span>农产品购买</span>
          </el-menu-item>
          <el-menu-item index="/orders" >
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/notice" v-if="data.user.role === 'ADMIN'">
            <el-icon><BellFilled /></el-icon>
            <span>系统公告管理</span>
          </el-menu-item>
          <el-sub-menu index="2" v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon><Avatar /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin">
              <el-icon><User /></el-icon>
              <span>管理员信息</span>
            </el-menu-item>
            <el-menu-item index="/user">
              <el-icon><User /></el-icon>
              <span>普通用户信息</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="1" v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon><Menu /></el-icon>
              <span>产品管理</span>
            </template>
            <el-menu-item index="/category">
              <el-icon><CollectionTag /></el-icon>
              <span>分类信息</span>
            </el-menu-item>
            <el-menu-item index="/goods">
              <el-icon><Goods /></el-icon>
              <span>农产品管理</span>
            </el-menu-item>
            <el-menu-item index="/goodsStock">
              <el-icon><SoldOut /></el-icon>
              <span>进货管理</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>

      <div style="margin-left: 200px; flex: 1;background-color: #f8f8ff;padding: 10px; box-sizing: border-box;overflow: auto;">
        <router-view @updateUser="updateUser" />
      </div>
    </div>

  </div>
</template>

<script setup>
import { reactive } from "vue";
import router from "@/router";
import {ElMessage, ElMessageBox} from "element-plus";


const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}')
})

if (!data.user?.id) {
  ElMessage.error('请登录！')
  router.push('/login')
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('system-user') || '{}')
}



const handleDropdown = (cmd) => {
  switch (cmd) {
    case 'a':
      router.push('/person')
      break;
    case 'b':
      router.push('/password')
      break;
    case 'c':
      ElMessageBox.confirm('是否退出系统?', '退出确认', { type: 'warning' }).then(res => {
        router.push('/login')
        ElMessage.success('退出成功')
        localStorage.removeItem('system-user')
      }).catch(err => {})
      break;
  }
}
</script>

<style scoped>
.el-menu-item.is-active {
  background-color: #e0edfd !important;
}
.el-menu-item:hover {
  color: #1967e3;
}
:deep(th)  {
  color: #333;
}
.example-showcase .el-dropdown-link {
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
}
</style>