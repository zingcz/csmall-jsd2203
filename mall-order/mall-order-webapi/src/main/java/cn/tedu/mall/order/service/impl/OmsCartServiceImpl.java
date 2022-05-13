package cn.tedu.mall.order.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.pojo.domain.CsmallAuthenticationInfo;
import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.order.mapper.OmsCartMapper;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.pojo.order.dto.CartAddDTO;
import cn.tedu.mall.pojo.order.dto.CartUpdateDTO;
import cn.tedu.mall.pojo.order.model.OmsCart;
import cn.tedu.mall.pojo.order.vo.CartStandardVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class OmsCartServiceImpl implements IOmsCartService {

    @Autowired
    private OmsCartMapper omsCartMapper;

    @Override
    public void addCart(CartAddDTO cartDTO) {
        // 获取当前登录用户的userId
        Long userId=getUserId();
        // 判断这个userId的用户是否已经将指定的sku添加到购物车
        OmsCart omsCartExist= omsCartMapper.selectExistCart(userId,cartDTO.getSkuId());
        if(omsCartExist!=null) {
            // 如果sku已经存在于购物车中,就修改这个sku在购物车中的数量
            omsCartExist.setQuantity(omsCartExist.getQuantity()+cartDTO.getQuantity());
            // 执行数据库修改
            omsCartMapper.updateQuantityById(omsCartExist);
        }else {
            // 如果sky没有存在于购物车中,就新增sku信息到购物车
            OmsCart omsCartNew=new OmsCart();
            // 将cartDTO对象中的同名属性赋值给 新创建的OmsCart对象
            BeanUtils.copyProperties(cartDTO,omsCartNew);
            // OmsCart对象中赋值当前登录用户id
            omsCartNew.setUserId(userId);
            // 新增新增
            omsCartMapper.saveCart(omsCartNew);

        }
    }

    @Override
    public JsonPage<CartStandardVO> listCarts(Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public void removeCart(Long[] ids) {

    }

    @Override
    public void removeAllCarts() {

    }

    @Override
    public void removeUserCarts(OmsCart omsCart) {

    }

    @Override
    public void updateQuantity(CartUpdateDTO cartUpdateDTO) {

    }

    // 业务逻辑层中可能有多个方法需要获得当前用户信息
    // 我们可以定义一个方法实现从SpringSecurity中获得用户信息
    public CsmallAuthenticationInfo getUserInfo(){
        // 从SpringSecurity框架的容器中,获得当前用户的authenticationToken对象
        UsernamePasswordAuthenticationToken authenticationToken=
                (UsernamePasswordAuthenticationToken)SecurityContextHolder
                                        .getContext().getAuthentication();
        // 判断获取的对象是不是null
        if(authenticationToken!=null){
            // 如果不是空,就是登录成功了,从authenticationToken对象中获得当前登录用户
            CsmallAuthenticationInfo csmallAuthenticationInfo=
                    (CsmallAuthenticationInfo)authenticationToken.getCredentials();
            return csmallAuthenticationInfo;
        }
        throw new CoolSharkServiceException(ResponseCode.UNAUTHORIZED,"没有登录信息");
    }
    // 单纯获得当前登录用户id的方法
    public Long getUserId(){
        return getUserInfo().getId();
    }






}
