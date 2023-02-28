package cn.tedu.mall.front.serviceImpl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.front.service.IFrontProductService;
import cn.tedu.mall.pojo.front.vo.FrontSkuSimpleVO;
import cn.tedu.mall.pojo.product.vo.*;
import cn.tedu.mall.product.service.front.IForFrontAttributeService;
import cn.tedu.mall.product.service.front.IForFrontSkuService;
import cn.tedu.mall.product.service.front.IForFrontSpuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FrontProductServiceImpl implements IFrontProductService {

    @DubboReference
    private IForFrontSpuService dubboSpuService;
    //spuId查询sku列表的dubbo的调用对象
    @DubboReference
    private IForFrontSkuService dubboSkuService;
    @DubboReference
    private IForFrontAttributeService dubboAttributeService;


    // 根据分类id分页查询spu列表
    @Override
    public JsonPage<SpuListItemVO> listSpuByCategoryId(
            Long categoryId, Integer page, Integer pageSize) {
        // dubbo调用的方法是product模块编写好的业务逻辑层方法
        // 这个方法中包含了分页的逻辑和代码,我们只需要调用就可以了
        JsonPage<SpuListItemVO> jsonPage=
                dubboSpuService.listSpuByCategoryId(categoryId, page, pageSize);
        // 别忘了返回jsonPage!!!!
        return jsonPage;
    }

    @Override
    public SpuStandardVO getFrontSpuById(Long id) {
        SpuStandardVO spuById = dubboSpuService.getSpuById(id);
        return spuById;
    }

    @Override
    public List<SkuStandardVO> getFrontSkusBySpuId(Long spuId) {
        List<SkuStandardVO> list = dubboSkuService.getSkusBySpuId(spuId);
        return list;
    }

    @Override
    public SpuDetailStandardVO getSpuDetail(Long spuId) {
        SpuDetailStandardVO spuDetailById = dubboSpuService.getSpuDetailById(spuId);
        return spuDetailById;
    }

    @Override
    public List<AttributeStandardVO> getSpuAttributesBySpuId(Long spuId) {
        List<AttributeStandardVO> spuAttributesBySpuId = dubboAttributeService.getSpuAttributesBySpuId(spuId);
        return spuAttributesBySpuId;
    }
}
