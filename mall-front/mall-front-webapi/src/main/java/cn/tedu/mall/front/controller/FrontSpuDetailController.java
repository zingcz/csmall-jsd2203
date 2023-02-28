package cn.tedu.mall.front.controller;


import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.front.service.IFrontProductService;
import cn.tedu.mall.pojo.product.vo.SkuStandardVO;
import cn.tedu.mall.pojo.product.vo.SpuDetailStandardVO;
import cn.tedu.mall.pojo.product.vo.SpuStandardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/front/spu/detail")
@Api(tags = "前台SpuDetail模块")
public class FrontSpuDetailController {
    @Autowired
    private IFrontProductService frontProductService;

    // localhost:10004/front/sku/1
    @GetMapping("/{spuId}")
    @ApiOperation("根据spuId查询spuDetail")
    @ApiImplicitParam(value = "spuId",name ="spuId",example = "1")
    public JsonResult<SpuDetailStandardVO> getSpuDetailBySkuId(
            @PathVariable Long spuId){
        SpuDetailStandardVO spuDetail = frontProductService.getSpuDetail(spuId);
        return JsonResult.ok(spuDetail);
    }


}
