package com.example.service;

import com.example.entity.CartItem;
import com.example.entity.CartItem;
import com.example.mapper.CartItemMapper;
import com.example.mapper.CartItemMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车业务处理
 **/
@Service
public class CartItemService {

    @Resource
    private CartItemMapper cartitemMapper;

    /**
     * 新增
     */
    public void add(CartItem cartitem) {
        cartitemMapper.insert(cartitem);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        cartitemMapper.deleteById(id);
    }

    /**
     * 修改
     */
    public void updateById(CartItem cartitem) {
        cartitemMapper.updateById(cartitem);
    }

    /**
     * 根据ID查询
     */
    public CartItem selectById(Integer id) {
        return cartitemMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<CartItem> selectAll(CartItem cartitem) {
        return cartitemMapper.selectAll(cartitem);
    }

    /**
     * 分页查询
     */
    public PageInfo<CartItem> selectPage(CartItem cartitem, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CartItem> list = cartitemMapper.selectAll(cartitem);
        return PageInfo.of(list);
    }


    // 新增：根据用户ID+商品ID查询
    public CartItem selectByUserIdAndProductId(Integer userId, Integer productId) {
        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        return cartitemMapper.selectByUserIdAndProductId(cartItem);
    }

    // 新增：清空用户购物车
    public void deleteByUserId(Integer userId) {
        cartitemMapper.deleteByUserId(userId);
    }




}