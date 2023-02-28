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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OmsCartServiceImpl implements IOmsCartService {
    @Autowired
    private OmsCartMapper omsCartMapper;

    @Override
    public void addCart(CartAddDTO cartDTO) {
        //1. 判断是否sku已经存在当前购物车
        //2. 获取用户id
        //3. 编写方法统一获取用户id
        Long userId = getUserId();
        Long skuId = cartDTO.getSkuId();
        OmsCart omsCart = omsCartMapper.selectExistCart(userId, skuId);
        //判断是否存在sku类型
        if(omsCart == null){
            //为空先实例化
            omsCart = new OmsCart();
            BeanUtils.copyProperties(cartDTO,omsCart);
            omsCart.setUserId(userId);
            //新增
            omsCartMapper.saveCart(omsCart);
        }else {
            //计算现有数量
            omsCart.setQuantity(omsCart.getQuantity() + cartDTO.getQuantity());
            omsCartMapper.updateQuantityById(omsCart);
        }

    }

    //分页查询当前用户
    @Override
    public JsonPage<CartStandardVO> listCarts(Integer page, Integer pageSize) {
        Long userId = getUserId();
        //设置分页查询范围
        PageHelper.startPage(page,pageSize);
        //上面设置完成后，下面进行查询操作，自动添加
        List<CartStandardVO> cartStandardVOList = omsCartMapper.selectCartsByUserId(userId);
        return JsonPage.restPage(new PageInfo<>(cartStandardVOList));
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

    //理解即可，从security中获取上下文token再token在中获取用户信息
    public CsmallAuthenticationInfo getUserInfo(){
        //获取上下文token
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        //为了逻辑严谨判断上下文token是否为空
        if(authenticationToken == null){
            throw  new CoolSharkServiceException(ResponseCode.UNAUTHORIZED,"没有登录");
        }
        //上下文token中拿取用户信息
        CsmallAuthenticationInfo csmallAuthenticationInfo =
                (CsmallAuthenticationInfo) authenticationToken.getCredentials();
        return csmallAuthenticationInfo;
    }

    //单独拆分出用户id
    public Long  getUserId(){
       return getUserInfo().getId();
    }
}
