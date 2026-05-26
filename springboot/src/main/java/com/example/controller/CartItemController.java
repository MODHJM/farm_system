package com.example.controller;

import com.example.common.Result;
import com.example.entity.CartItem;
import com.example.service.CartItemService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车前端操作接口
 **/
@RestController
@RequestMapping("/cart")
public class CartItemController {

    @Resource
    private CartItemService cartitemService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody CartItem cartitem) {
        cartitemService.add(cartitem);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        cartitemService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody CartItem cartitem) {
        cartitemService.updateById(cartitem);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        CartItem cartitem = cartitemService.selectById(id);
        return Result.success(cartitem);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(CartItem cartitem) {
        List<CartItem> list = cartitemService.selectAll(cartitem);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(CartItem cartitem,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<CartItem> page = cartitemService.selectPage(cartitem, pageNum, pageSize);
        return Result.success(page);
    }


    /**
     * 新增：根据用户ID+商品ID查询
     */
    @GetMapping("/selectByUserAndProduct")
    public Result selectByUserAndProduct(@RequestParam Integer userId, @RequestParam Integer productId) {
        CartItem cartItem = cartitemService.selectByUserIdAndProductId(userId, productId);
        return Result.success(cartItem);
    }

    /**
     * 新增：清空用户购物车
     */
    @DeleteMapping("/clear/{userId}")
    public Result clearCart(@PathVariable Integer userId) {
        cartitemService.deleteByUserId(userId);
        return Result.success("清空购物车成功");
    }

}