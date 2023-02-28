package cn.tedu.mall.front.serviceImpl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.front.service.IFrontCategoryService;
import cn.tedu.mall.pojo.front.entity.FrontCategoryEntity;
import cn.tedu.mall.pojo.front.vo.FrontCategoryTreeVO;
import cn.tedu.mall.pojo.product.vo.CategoryStandardVO;
import cn.tedu.mall.product.service.front.IForFrontCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FrontCategoryServiceImpl implements IFrontCategoryService {

    // front模块要Dubbo调用product模块的方法,实现查询所有分类信息列表
    @DubboReference
    private IForFrontCategoryService dubboCategoryService;

    // 方法要将查询到的信息保存到Redis,需要操作redis的对象
    @Autowired
    private RedisTemplate redisTemplate;

    // 开发时,使用Redis要规范的定义一个常量用作key的名称,防止编码时拼写错误
    public static final String CATEGORY_TREE_KEY="category_tree";

    // 返回三级分类树对象的方法
    @Override
    public FrontCategoryTreeVO categoryTree() {
        // 先检查Redis中是否已经保存了三级分类树对象
        if(redisTemplate.hasKey(CATEGORY_TREE_KEY)){
            // 如果Redis中有这个key,直接获取信息后返回
            FrontCategoryTreeVO<FrontCategoryEntity> treeVO=
                    (FrontCategoryTreeVO<FrontCategoryEntity>)
                            redisTemplate.boundValueOps(CATEGORY_TREE_KEY).get();
            // 别忘了返回
            return treeVO;
        }
        // Redis中没有三级分类树信息,表示本次请求可能是首次访问
        // 这就需要我们从数据库中获取三级分类树信息,在保存到Redis中
        // 要dubbo调用product模块查询所有分类信息
        List<CategoryStandardVO> categoryList =
                                dubboCategoryService.getCategoryList();

        // 上面返回集合的泛型CategoryStandardVO是没有children属性的,而FrontCategoryEntity是有的!
        // 因为转换过程比较复杂,所以最好单独声明一个方法来进行转换
        // 目标是将子分类对象添加到对应的父分类对象的children属性中
        FrontCategoryTreeVO<FrontCategoryEntity> treeVO=
                                            initTree(categoryList);
        // 上面方法完成了三级分类树的构建,返回了treeVO
        // 下面要将treeVO保存到Redis,方便后续请求获取
        redisTemplate.boundValueOps(CATEGORY_TREE_KEY).set(
                        treeVO,
                        1,
                        TimeUnit.MINUTES);
        // 上面时间定义了1分钟,是适合学习中测试使用的,项目上线时,时间会长,例如24小时或更长
        // 这样也要返回treeVO!!!
        return treeVO;
    }

    private FrontCategoryTreeVO<FrontCategoryEntity> initTree(
                                    List<CategoryStandardVO> categoryList) {
        // 第一步
        // 确定所有分类对象对应的父分类的id
        // 创建一个Map
        // 以父分类id为Key,将这个父分类包含的所有子分类对象保存在value中
        // 这样就能实现第一步的目标
        Map<Long,List<FrontCategoryEntity>> map=new HashMap<>();
        log.info("准备构建三级分类树,分类对象数量为:{}",categoryList.size());
        // 遍历数据库查询获取的所有分类对象集合
        for(CategoryStandardVO categoryStandardVO : categoryList){
            // 当前遍历的对象categoryStandardVO没有children属性,不能保存关联的子分类对象
            // 所以我们先将它转换为能够保存子分类对象的FrontCategoryEntity类型
            FrontCategoryEntity frontCategoryEntity=new FrontCategoryEntity();
            // 同名属性赋值
            BeanUtils.copyProperties(categoryStandardVO,frontCategoryEntity);
            // 获取当前分类对象的父分类id,用于后续判断,如果父分类id为0,就是一级分类
            Long parentId=frontCategoryEntity.getParentId();
            // 判断map中是否已经存在这个父分类id作为key
            if(!map.containsKey(parentId)){
                // 如果map中没有这个key,表示当前父分类id作为key第一次出现,要新增一个元素
                // 元素的key就是这个parentId,元素的值是个list
                // 先实例化这个List
                List<FrontCategoryEntity> value=new ArrayList<>();
                // 将当前元素保存到集合中
                value.add(frontCategoryEntity);
                // 最后将key和value作为元素保存到map
                map.put(parentId,value);
            }else{
                // 如果map中已经存在当前分类对象父分类id为key的元素
                // 就将当前分类对象,直接添加到这个父分类id元素的value集合中
                map.get(parentId).add(frontCategoryEntity);
            }
        }
        // 第二步
        // 构建三级分类树,将子分类对象添加到父分类对象的childrens属性中
        // 先来获取所有一级分类对象,也就是父分类id为0的分类对象
        List<FrontCategoryEntity> firstLevels=map.get(0L);
        // 判断一级分类集合如果为null(或元素个数为0)直接抛出异常,终止程序
        if(firstLevels==null || firstLevels.isEmpty()){
            throw new CoolSharkServiceException(
                    ResponseCode.INTERNAL_SERVER_ERROR,"没有一级分类对象!");
        }
        // 遍历一级分类集合
        for(FrontCategoryEntity oneLevel : firstLevels){
            // 一级分类对象的id就是二级分类对象的父id
            Long secondLevelParentId=oneLevel.getId(); // getId!!!!!!!!!!!!!
            // 从map中根据secondLevelParentId获取这个对象包含的二级分类对象集合
            List<FrontCategoryEntity> secondLevels=map.get(secondLevelParentId);
            // 判断二级分类中是否有元素
            if(secondLevels==null || secondLevels.isEmpty()){
                // 二级分类对象集合缺失,不用抛异常,日志输出警告即可
                log.warn("当前二级分类没有内容:{}",secondLevelParentId);
                // 当前二级分类没有内容,无需运行循环中后面的语句,直接进行下次循环
                continue;
            }
            // 二级分类集合有元素,开始遍历二级分类对象
            for (FrontCategoryEntity twoLevel : secondLevels){
                // 获取当前二级分类对象的id,作为三级分类的父id
                Long thirdLevelParentId=twoLevel.getId();  // getId()!!!!!!!!
                // 根据这个id获得这个二级分类对象关联的所有三级分类对象集合
                List<FrontCategoryEntity> thirdLevels=map.get(thirdLevelParentId);
                // 还是判断三级分类集合是否为空
                if(thirdLevels==null || thirdLevels.isEmpty()){
                    log.warn("当前二级分类没有三级分类内容:{}",thirdLevelParentId);
                    continue;
                }
                // 将三级分类对象集合添加到当前二级分类对象的childrens属性中
                twoLevel.setChildrens(thirdLevels);
            }
            // 在内层循环结束后,在外层循环结束前
            // 将二级分类对象集合添加到当前一级分类对象的childrens属性中
            oneLevel.setChildrens(secondLevels);
        }
        // 到此为止,我们所有的分类对象都已经保存在了自己对应的父分类对象的children属性中
        // 现在就需要将firstLevels赋值给FrontCategoryTreeVO类型的categories属性中
        // 实例化对象
        FrontCategoryTreeVO<FrontCategoryEntity> treeVO=
                new FrontCategoryTreeVO<>();
        treeVO.setCategories(firstLevels);
        // 最后千万别忘了返回treeVO!!!
        return treeVO;
    }
}
