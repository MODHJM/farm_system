package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.entity.Goods;
import com.example.entity.Orders;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.OrdersMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单业务处理
 **/
@Service
public class OrdersService {

    @Resource
    private OrdersMapper ordersMapper;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private UserService userService;

    /**
     * 新增
     */
    @Transactional
    public void add(Orders orders) {
        orders.setOrderNo(IdUtil.fastSimpleUUID());
        orders.setTime(DateUtil.now());
        orders.setStatus("待发货");


        // 查询用户地址并存入订单
        User user = userService.selectById(orders.getUserId());
        if (user != null && user.getAddress() != null) {
            orders.setAddress(user.getAddress()); // 把用户地址存入订单
        } else {
            throw new CustomException("请先在个人资料填写收货地址！");
        }

        Goods goods = goodsService.selectById(orders.getGoodsId());
        if(goods==null){
            throw new CustomException("商品不存在");
        }
        int store = goods.getStore() - orders.getNum();
        if(store<0){
            throw new CustomException("商品库存不足");
        }
        goods.setStore(store);

        goodsService.updateById(goods);
        ordersMapper.insert(orders);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
    }

    /**
     * 修改
     */
    @Transactional
    public void updateById(Orders orders) {
        if("已退货".equals(orders.getStatus())){
            Integer goodsId = orders.getGoodsId();
            Goods goods = goodsService.selectById(goodsId);
            if(goods!=null){
                goods.setStore(goods.getStore() + orders.getNum());
                goodsService.updateById(goods);
            }
        }
        ordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Orders> selectAll(Orders orders) {
        return ordersMapper.selectAll(orders);
    }

    /**
     * 分页查询
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize,Integer userId ) {
        PageHelper.startPage(pageNum, pageSize);
        if (userId != null) {
            orders.setUserId(userId);
        }
        List<Orders> list = ordersMapper.selectAll(orders);
        return PageInfo.of(list);
    }





}