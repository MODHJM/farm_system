<template>
  <div>

    <div class="card" style="margin-bottom: 5px;">
      <el-input v-model="data.goodsName" style="width: 300px; margin-right: 10px" placeholder="请输入农产品名称查询"></el-input>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <div style="margin-bottom: 10px">
        <el-button type="primary" @click="handleAdd">进货</el-button>
      </div>
      <el-table :data="data.tableData" stripe>
        <el-table-column label="农产品名称" prop="goodsName"></el-table-column>
        <el-table-column label="进货数量" prop="num"></el-table-column>
        <el-table-column label="进货渠道" prop="channel"></el-table-column>
        <el-table-column label="进货日期" prop="date"></el-table-column>
        <el-table-column label="备注" prop="comment"></el-table-column>

        <el-table-column label="操作"  header-align="center" width="160">
          <template #default="scope">
            <el-button type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="card" v-if="data.total">
      <el-pagination background layout="prev, pager, next" @current-change="load" @size-change="load" v-model:page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>
    </div>

    <el-dialog title="进货信息" width="40%" v-model="data.formVisible" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="100px" style="padding-right: 50px" ref="formRef" :rules="data.rules">
        <el-form-item label="农产品名称" prop="goodsId">
          <el-select v-model="data.form.goodsId" placeholder="请选择农产品" style="width: 100%">
            <el-option
                v-for="item in data.goodsList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
                :disabled="!!data.form.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="进货数量" prop="num">
          <el-input-number v-model="data.form.num" :min="1" :disabled="!!data.form.id"> </el-input-number>
        </el-form-item>
        <el-form-item label="进货渠道" prop="channel">
          <el-input v-model="data.form.channel" placeholder="请输入进货渠道"> </el-input>
        </el-form-item>
        <el-form-item label="进货日期" prop="date">
          <el-date-picker style="width: 100%;" type="date" v-model="data.form.date"  placeholder="请输入进货日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD"> </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="comment">
          <el-input v-model="data.form.comment" placeholder="请输入备注"> </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="data.formVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">保 存</el-button>
      </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import request from "@/utils/request";
import {reactive,ref} from "vue";
import {ElMessageBox, ElMessage} from "element-plus";


const data = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  rules:{
    goodsId: [
      { required: true, message: '请选择进货商品', trigger: 'blur' }
    ],
    num: [
      { required: true, message: '请输入进货数量', trigger: 'blur' }
    ],

  },
  tableData: [],
  goodsName: null,
  goodsList:[]
})

const formRef=ref()

request.get('goods/selectAll').then(res=>{
  data.goodsList=res.data
})

// 分页查询
const load = () => {
  request.get('/goodsStock/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      goodsName: data.goodsName
    }
  }).then(res => {
    data.tableData = res.data?.list
    data.total = res.data?.total
  })
}

// 新增
const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}

// 编辑
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

// 新增保存
const add = () => {
  request.post('/goodsStock/add', data.form).then(res => {
    if (res.code === '200') {
      load()
      ElMessage.success('操作成功')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 编辑保存
const update = () => {
  request.put('/goodsStock/update', data.form).then(res => {
    if (res.code === '200') {
      load()
      ElMessage.success('操作成功')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 弹窗保存
const save = () => {
  formRef.value.validate((valid)=>{
    if (!valid){
      return ElMessage.warning('请完善必填字段')
    }
    data.form.id ? update() : add()
  })

}

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/goodsStock/delete/' + id).then(res => {
      if (res.code === '200') {
        load()
        ElMessage.success('操作成功')
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {})
}

// 重置
const reset = () => {
  data.goodsName = null
  load()
}


load()
</script>