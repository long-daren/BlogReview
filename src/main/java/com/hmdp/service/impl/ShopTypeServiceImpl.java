package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.LOCK_TYPE_KEY;
import static com.hmdp.utils.RedisConstants.LOCK_TYPE_TTL;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result queryType() {
        String key = LOCK_TYPE_KEY;
        String shopType = stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(shopType)){
            List<ShopType> shopTypes = JSONUtil.toList(shopType, ShopType.class);
            return Result.ok(shopTypes);
        }
        List<ShopType> shopTypes = query().orderByAsc("sort").list();
        if(shopTypes==null && shopTypes.size()<=0){
            return Result.fail("商铺类型分类不存在");
        }
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(shopTypes),LOCK_TYPE_TTL, TimeUnit.MINUTES);
        return Result.ok(shopTypes);
    }
}
