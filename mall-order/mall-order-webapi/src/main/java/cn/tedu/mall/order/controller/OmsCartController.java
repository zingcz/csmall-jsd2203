package cn.tedu.mall.order.controller;

import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.pojo.order.dto.CartAddDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oms/cart")
@Api(tags="购物车功能")
public class OmsCartController {
    @Autowired
    private IOmsCartService cartService;
    @PostMapping("/add")
    @ApiOperation("当前登录用户新增sku到购物车")
    // 判断当前用户是否为ROLE_user(是否登录了)
    @PreAuthorize("hasRole('ROLE_user')")
    // @Validated启动控制器运行前对CartAddDTO对象的SpringValidation验证
    public JsonResult addCart(@Validated CartAddDTO cartAddDTO){
        cartService.addCart(cartAddDTO);
        return JsonResult.ok("添加到购物车完成");
    }
    // leaf   passport    order
    // sso:10002   order:10005
    // 先运行10002的knife4j 访问前台用户登录 登录成功复制jwt
    // 转到order10005,在全局设置中设置参数
    // name:   Authorization
    // value:  Beaer [复制的jwt]
    // 然后要刷新10005的knife4j页面
    // 就可以发送新增到购物车的请求了

}







