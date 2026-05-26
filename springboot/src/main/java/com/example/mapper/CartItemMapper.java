package com.example.mapper;

import com.example.entity.CartItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作cartitem相关数据接口
*/
@Mapper
public interface CartItemMapper {

    /**
      * 新增
    */
    int insert(CartItem cartitem);

    /**
      * 删除
    */
    @Delete("delete from cart_item where id = #{id}")
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(CartItem cartitem);

    /**
      * 根据ID查询
    */
    @Select("select * from cart_item where id = #{id}")
    CartItem selectById(Integer id);

    /**
      * 查询所有
    */
    List<CartItem> selectAll(CartItem cartitem);

    // <font color="red">新增：根据用户ID+商品ID查询购物车项（用于加购去重）</font>
    @Select("select * from cart_item where user_id = #{userId} and product_id = #{productId}")
    CartItem selectByUserIdAndProductId(CartItem cartItem);

    // <font color="red">新增：清空用户购物车</font>
    @Delete("delete from cart_item where user_id = #{userId}")
    int deleteByUserId(Integer userId);

}