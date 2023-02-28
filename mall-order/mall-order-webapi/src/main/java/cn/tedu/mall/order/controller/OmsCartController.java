package cn.tedu.mall.order.controller;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.order.utils.WebConsts;
import cn.tedu.mall.pojo.order.dto.CartAddDTO;
import cn.tedu.mall.pojo.order.vo.CartStandardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api("购物车管理模块")
@RequestMapping("/oms/cart")
public class OmsCartController {
    @Autowired
    private IOmsCartService omsCartService;

    @PostMapping("/add")
    @ApiOperation("新增sku信息到购物车")
    @PreAuthorize("hasAuthority('ROLE_user')")
    public JsonResult<String> addCart(@Validated CartAddDTO cartAddDTO){
        omsCartService.addCart(cartAddDTO);
        return JsonResult.ok("新增sku到购物车完成");
    }

    @PostMapping("/list")
    @ApiOperation("根据用户id和分页信息查询购物车列表")
    @PreAuthorize("hasAuthority('ROLE_user')")
    public JsonResult<JsonPage<CartStandardVO>> list(
            //false允许前端传入数据为空，后端自动赋默认值
        @RequestParam(required = false, defaultValue = WebConsts.DEFAULT_PAGE) Integer page,
        @RequestParam(required = false, defaultValue = WebConsts.DEFAULT_PAGE_SIZE) Integer pageSize)
    {
        JsonPage<CartStandardVO> cartStandardVOJsonPage = omsCartService.listCarts(page,pageSize);
        return JsonResult.ok(cartStandardVOJsonPage);
    }
}
