package cn.tedu.mall.front.controller;


import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.front.service.IFrontProductService;
import cn.tedu.mall.pojo.product.vo.AttributeStandardVO;
import cn.tedu.mall.pojo.product.vo.SkuStandardVO;
import cn.tedu.mall.pojo.product.vo.SpuListItemVO;
import cn.tedu.mall.pojo.product.vo.SpuStandardVO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/front/sku")
@Api(tags = "前台商品Spu模块")
public class FrontSkuController {
    @Autowired
    private IFrontProductService frontProductService;

    // localhost:10004/front/sku/1
    @GetMapping("/{spuId}")
    @ApiOperation("根据spuId查询sku列表")
    @ApiImplicitParam(value = "spuId",name ="spuId",example = "3")
    public JsonResult<List<SkuStandardVO>> getSkuListBySkuId(
            @PathVariable Long spuId){
        List<SkuStandardVO> frontSkusBySpuId = frontProductService.getFrontSkusBySpuId(spuId);
        return JsonResult.ok(frontSkusBySpuId);
    }


}
