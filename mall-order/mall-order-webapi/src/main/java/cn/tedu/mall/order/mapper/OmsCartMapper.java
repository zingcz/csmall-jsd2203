package cn.tedu.mall.order.mapper;


import cn.tedu.mall.pojo.order.model.OmsCart;
import cn.tedu.mall.pojo.order.vo.CartStandardVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OmsCartMapper {
    //判断当前购物车中是否存在选择的sku
    OmsCart selectExistCart(@Param("userId") Long userId,
                            @Param("skuId") Long skuId);
    //新增sku商品到cart
    int saveCart(OmsCart omsCart);

    //根据购物车id修改sku数量
    int updateQuantityById(OmsCart omsCart);

    //根据当前用户id查询购物车列表
    List<CartStandardVO> selectCartsByUserId(Long userId);
}
