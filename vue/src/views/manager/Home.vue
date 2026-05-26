<template>
  <div>

    <div style="padding-left: 60px; font-size: 20px; font-weight: bold;margin-bottom: 20px">系统公告</div>
    <el-timeline style="width: 50%;" >
      <el-timeline-item :timestamp="item.time" placement="top" v-for="item in data.noticeList " :key="item.id">
        <el-card>
          <h4>{{ item.tilde }}</h4>
          <p>{{ item.content }}</p>
        </el-card>
      </el-timeline-item>
    </el-timeline>

  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request";

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  noticeList:[],
})

request.get('/notice/selectAll').then(res=>{
  data.noticeList=res.data
})
</script>