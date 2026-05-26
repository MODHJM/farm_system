<template>
  <div style="padding-bottom: 50px; position: relative;">
    <!-- 分类筛选 + 搜索 -->
    <div class="card" style="margin-bottom: 10px">
      <el-button :class="{'active' : data.activeCategoryId === null}" @click="loadCategoryGoods(null)">全部</el-button>
      <el-button
          :class="{'active' : data.activeCategoryId === item.id}"
          @click="loadCategoryGoods(item.id)"
          v-for="item in data.categoryList"
          :key="item.id"
      >{{item.name}}</el-button>
    </div>
    <div style="margin-bottom: 10px">
      <el-input v-model="data.name" placeholder="请输入名称查询" style="width: 230px; margin-right: 5px;"></el-input>
      <el-button type="primary" @click="load">搜索</el-button>
    </div>

    <!-- 商品列表 -->
    <el-row :gutter="10" v-if="data.total>0">
      <el-col style="margin-bottom: 10px" :span="6" v-for="item in data.goodsList" :key="item.id">
        <div class="card" style="padding: 10px; box-sizing: border-box;">
          <img :src="item.img" style="width: 100%;height: 250px;object-fit: cover;">
          <div style="margin:5px 0;font-size: 18px;color:#333;">{{item.name}}</div>
          <el-tooltip v-if="item.descr?.length>40" :content="item.descr" placement="top" effect="light">
            <div class="line2" style="margin:5px 0;color:#666;font-size: 14px;height: 40px;overflow: hidden;">{{item.descr}}</div>
          </el-tooltip>
          <div v-else style="margin:5px 0;color:#666;font-size: 14px;height: 40px;">{{item.descr}}</div>
          <div style="margin: 5px 0" >
            <el-tag type="success">{{item.specials}}</el-tag>
          </div>
          <div style="margin: 10px 0;display: flex;align-items: center ;color:#666;">
            <div style="flex: 1;">
              <strong style="color:red;">￥{{item.price}}/{{item.unit}}</strong>
            </div>
            <div  style="flex: 1;text-align: center">
              库存：{{item.store}}
            </div>
            <div >
              <el-input-number @change="calcItemTotal(item)" v-model="item.num" style="width: 110px;"   :min="0" :max="item.store">  </el-input-number>
            </div>
          </div>
          <!-- 加入购物车按钮：数量>0才显示 -->
          <div style="text-align: right; margin-top: 10px;" v-if="item.num>0">
            <el-button type="primary" @click="addCart(item)">加入购物车</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    <div style="padding: 50px 0;font-size: 24px;color:#888;text-align: center" v-else>暂无农产品.....</div>

    <!-- 分页 -->
    <div class="card" v-if="data.total">
      <el-pagination
          background
          layout="prev, pager, next"
          @current-change="load"
          @size-change="load"
          v-model:page-size="data.pageSize"
          v-model:current-page="data.pageNum"
          :total="data.total"
      />
    </div>

    <!-- 购物车悬浮按钮：固定在右下角，显示商品数量 -->
    <div
        class="cart-btn"
        @click="openCartDialog()"
        :class="{'has-goods': data.cartList.length>0}"
    >
      <el-icon style="font-size: 24px;"><ShoppingCart /></el-icon>
      <span class="cart-count" v-if="data.cartList.length>0">{{data.cartTotalNum}}</span>
    </div>

    <!-- 购物车弹窗：核心结算区域 -->
    <el-dialog
        title="我的购物车"
        width="60%"
        v-model="data.cartVisible"
        :close-on-click-modal="false"
    >
      <div v-if="data.cartList.length>0">
        <!-- 购物车商品列表 -->
        <el-table :data="data.cartList" border style="width: 100%; margin-bottom: 20px;">
          <el-table-column label="商品图片" width="100">
            <template #default="scope">
              <img :src="scope.row.image" style="width: 80px; height: 80px; object-fit: cover;">
            </template>
          </el-table-column>
          <el-table-column label="商品名称" prop="productName" min-width="200"></el-table-column>
          <el-table-column label="单价(元)" prop="price" align="center">
            <template #default="scope">￥{{scope.row.price}}</template>
          </el-table-column>
          <el-table-column label="单位" prop="unit" align="center">
            <template #default="scope">/{{scope.row.unit}}</template>
          </el-table-column>
          <el-table-column label="数量" align="center" width="150">
            <template #default="scope">
              <el-input-number
                  @change="updateCartNum(scope.row)"
                  v-model="scope.row.num"
                  :min="1"
                  :max="scope.row.store"
                  style="width: 100px;"
              ></el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="小计(元)" align="center">
            <template #default="scope">
              ￥{{(scope.row.price * scope.row.num).toFixed(2)}}
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="100">
            <template #default="scope">
              <el-button type="text" color="red" @click="removeCartItem(scope.row.id)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 购物车底部操作：清空 + 总价 + 支付 -->
        <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px; background: #f5f5f5; border-radius: 4px;">
          <el-button type="text" color="red" @click="clearCart">清空购物车</el-button>
          <div style="font-size: 18px; font-weight: bold;">
            购物车总价：<span style="color: red; font-size: 20px;">￥{{data.cartTotalPrice}}</span>
          </div>
          <el-button type="primary" size="large" @click="payCart">立即支付</el-button>
        </div>
      </div>
      <!-- 购物车为空 -->
      <div v-else style="text-align: center; padding: 50px 0;">
        <el-icon style="font-size: 48px; color: #ccc;"><ShoppingCart /></el-icon>
        <div style="font-size: 18px; color: #888; margin-top: 10px;">购物车空空如也，快去挑选商品吧~</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {reactive, onMounted} from "vue";
import request from "@/utils/request";
import {ElMessage, ElMessageBox} from "element-plus";


const data=reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  categoryList:[],
  pageNum: 1,
  pageSize: 8,
  total: 0,
  goodsList:[],
  name:'',
  activeCategoryId:null,
  // 购物车新增数据
  cartVisible: false,
  cartList: [],
  cartTotalNum: 0,
  cartTotalPrice: 0
})

// 初始化：加载分类 + 登录用户加载购物车
onMounted(() => {
  loadCategory();
  if (data.user.id) {
    loadCartList();
  }
  load();
})

// 获取分类数据
const loadCategory = () => {
  request.get('/category/selectAll').then(res=>{
    data.categoryList = res.data || [];
  }).catch(err => {
    ElMessage.error('分类加载失败');
  })
}

// 加载用户购物车列表
const loadCartList = () => {
  if (!data.user.id) return;
  request.get('/cart/selectAll', {
    params: { userId: data.user.id }
  }).then(res => {
    // 转换后端字段适配前端显示
    data.cartList = (res.data || []).map(item => ({
      id: item.id,
      productId: item.productId,
      productName: item.productName,
      price: Number(item.price),
      num: item.num,
      image: item.image,
      unit: item.unit,
      store: item.store ,
      total: (Number(item.price) * item.num).toFixed(2)
    }));
    calcCartTotal(); // 计算统计信息
  }).catch(err => {
    ElMessage.error('购物车加载失败');
  })
}

// 打开购物车弹窗：先加载最新数据
const openCartDialog = () => {
  if (!data.user.id) {
    ElMessage.warning('请先登录查看购物车');
    return;
  }
  loadCartList();
  data.cartVisible = true;
}

// 计算单个商品的小计
const calcItemTotal = (goods) => {
  if (goods.num < 0) goods.num = 0;
  if (goods.num > goods.store) goods.num = goods.store;
  // 保留2位小数，避免浮点精度问题
  goods.total = (goods.price * goods.num).toFixed(2)
}

// 加入购物车方法
const addCart = (goods) => {
  // 校验是否登录
  if (!data.user.id) {
    ElMessage.warning('请先登录再进行加购操作');
    return;
  }
  // 校验数量是否有效
  if (goods.num <= 0) {
    ElMessage.warning('请选择大于0的商品数量');
    return;
  }

  // 构建后端需要的购物车项
  const cartItem = {
    userId: data.user.id,
    productId: goods.id,
    productName: goods.name,
    price: goods.price,
    num: goods.num,
    image: goods.img,
    unit: goods.unit,
    store: goods.store
  };

  // 先查询后端：该商品是否已在购物车
  request.get('/cart/selectByUserAndProduct', {
    params: {
      userId: data.user.id,
      productId: goods.id
    }
  }).then(res => {
    const existItem = res.data;
    if (existItem) {
      // 已有：更新数量（不超过库存）
      const newNum = existItem.num + goods.num;
      const finalNum = newNum > goods.store ? goods.store : newNum;
      request.put('/cart/update', {
        id: existItem.id,
        num: finalNum
      }).then(() => {
        ElMessage.success('商品数量已更新');
        loadCartList();
        goods.num = 0; // 重置数量选择器
      }).catch(err => {
        ElMessage.error('更新购物车失败');
      });
    } else {
      // 没有：新增购物车项
      request.post('/cart/add', cartItem).then(() => {
        ElMessage.success('加入购物车成功');
        loadCartList();
        goods.num = 0; // 重置数量选择器
      }).catch(err => {
        ElMessage.error('加入购物车失败');
      });
    }
  }).catch(err => {
    ElMessage.error('查询购物车商品失败');
  });
}

// 更新购物车中商品的数量
const updateCartNum = (cartItem) => {
  if (cartItem.num < 1) cartItem.num = 1;
  if (cartItem.num > cartItem.store) cartItem.num = cartItem.store;

  // 调用后端更新接口
  request.put('/cart/update', {
    id: cartItem.id,
    num: cartItem.num
  }).then(() => {
    calcCartTotal(); // 更新统计
    ElMessage.success('数量更新成功');
  }).catch(err => {
    ElMessage.error('更新数量失败');
    loadCartList(); // 失败后刷新数据
  });
}

// 移除购物车单个商品
const removeCartItem = (id) => {
  ElMessageBox.confirm('确定移除该商品吗？', '提示', {type: 'info'})
      .then(() => {
        request.delete(`/cart/delete/${id}`).then(() => {
          ElMessage.success('移除成功');
          loadCartList();
        }).catch(err => {
          ElMessage.error('移除失败');
        });
      })
      .catch(() => {});
}

// 清空购物车
const clearCart = () => {
  ElMessageBox.confirm('确定清空购物车吗？', '警告', {type: 'warning'})
      .then(() => {
        request.delete(`/cart/clear/${data.user.id}`).then(() => {
          ElMessage.success('购物车已清空');
          loadCartList();
        }).catch(err => {
          ElMessage.error('清空失败');
        });
      })
      .catch(() => {});
}

// 计算购物车总数量和总价
const calcCartTotal = () => {
  let totalNum = 0;
  let totalPrice = 0;
  data.cartList.forEach(item => {
    totalNum += item.num;
    totalPrice += Number(item.price) * item.num; // 重新计算避免数据不一致
  });
  data.cartTotalNum = totalNum;
  // 保留2位小数，解决浮点精度问题
  data.cartTotalPrice = totalPrice.toFixed(2);
}

// 购物车批量支付结算
const payCart = () => {
  // 构建订单提交数据：遍历购物车，生成多个订单项
  const orderList = data.cartList.map(item => ({
    goodsId: item.productId, // 对应后端productId
    num: item.num,
    userId: data.user.id
  }));
  // 批量提交订单
  let successCount = 0;
  let errorCount = 0;
  orderList.forEach(item => {
    request.post('/orders/add', item)
        .then(res => {
          if (res.code === '200') {
            successCount++;
          } else {
            errorCount++;
            ElMessage.error(`商品购买失败：${res.msg}`);
          }
          // 所有请求完成后统一处理
          if (successCount + errorCount === orderList.length) {
            if (successCount > 0) {
              ElMessage.success(`共${successCount}件商品购买成功`);
              data.cartVisible = false;
              clearCart();
              load(); // 刷新商品列表
            }
            if (errorCount > 0) {
              ElMessage.warning(`共${errorCount}件商品购买失败`);
            }
          }
        })
        .catch(err => {
          errorCount++;
          if (successCount + errorCount === orderList.length) {
            ElMessage.warning(`部分商品购买失败，请重试`);
          }
        });
  });
}

// 分页查询商品
const load = () => {
  request.get('/goods/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name,
      categoryId: data.activeCategoryId
    }
  }).then(res => {
    data.goodsList = res.data?.list || [];
    data.total = res.data?.total || 0;
    data.goodsList.forEach(item => {
      item.num = 0;
      item.total = 0;
    });
  }).catch(err => {
    ElMessage.error('商品加载失败');
  })
}

// 切换分类查询
const loadCategoryGoods=(categoryId)=>{
  data.activeCategoryId=categoryId;
  data.pageNum = 1; // 切换分类重置为第1页
  load();
}
</script>

<style scoped>
/* 分类激活样式 */
.active{
  color: white !important;
  background-color: #1967e3 !important;
}
/* 购物车悬浮按钮：固定右下角 */
.cart-btn{
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: #1967e3;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(25, 103, 227, 0.5);
  z-index: 999;
}
/* 购物车有商品时的样式 */
.cart-btn.has-goods{
  background-color: #e64340;
}
/* 购物车数量角标 */
.cart-count{
  position: absolute;
  top: -5px;
  right: -5px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: white;
  color: #e64340;
  font-size: 12px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
}
/* 商品描述 */
.line2{
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>