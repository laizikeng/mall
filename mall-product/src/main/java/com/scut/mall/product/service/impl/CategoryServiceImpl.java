package com.scut.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.scut.mall.product.service.CategoryBrandRelationService;
import com.scut.mall.product.vo.Catelog2Vo;
import com.scut.mall.product.vo.Catelog3Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scut.common.utils.PageUtils;
import com.scut.common.utils.Query;

import com.scut.mall.product.dao.CategoryDao;
import com.scut.mall.product.entity.CategoryEntity;
import com.scut.mall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public List<CategoryEntity> listWithTree() {
        // 查出所有的分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        // 组装成树结构
        // 查找所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
            categoryEntity.getParentCid() == 0
        ).map(menu->{
            menu.setChildren(getChildren(menu,entities));
            return menu;
        }).sorted((menu1,menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return level1Menus;
    }

    private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == root.getCatId()
        ).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }


    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO 1、检查当前删除的菜单是否被别的地方引用
        // 逻辑删除
        baseMapper.deleteBatchIds(asList);
    }


    @Override
    public Long[] findCatelogPath(Long catelogId) {
        ArrayList<Long> paths = new ArrayList<>();
        CategoryEntity byId = null;

        do {
            paths.add(catelogId);
            byId = this.getById(catelogId);
        } while ((catelogId = byId.getParentCid())!=0);

        Collections.reverse(paths);

        return paths.toArray(new Long[paths.size()]);
    }


    /**
     * 级联更新所有关联的数据
     * 缓存数据一致性
     * 1.双写模式
     *      问题：读到的最新数据有延迟
     *            脏数据问题：这是暂时性的脏数据问题，但是在数据稳定，缓存过期以后，又能得到最新的数据
     *      解决：加锁，或者不解决（等缓存过期）
     * 2.失效模式
     *      问题：频繁更新缓存问题
     * 解决：使用canal，缺点：多一个中间件
     *
     * @CacheEvict:失效模式
     * @Caching:同时进行多种缓存操作
     * @CacheEvict(value = "category",allEntries = true):删除某个分区下的所有数据
     * @CachePut:双写模式
     * Spring-Cache的不足：
     *      1.读模式：
     *          缓存穿透：cache-null-values: true
     *          缓存击穿：默认不加锁，可以使用redisson或者sync = true
     *          缓存雪崩：加上过期时间
     *      2.写模式（缓存与数据库一致）：
     *          读写加锁
     *          引入Canal，感知到MySQL的更新就去更新数据库
     *          读多写多，直接去数据库查询就行
     */
    @Override
    @Transactional
//    @Caching(evict = {
//        @CacheEvict(value = {"category"}, key = "'getLevel1Categorys'"),
//        @CacheEvict(value = {"category"}, key = "'getCatelogJson'")
//	})
    @CacheEvict(value = "category",allEntries = true)
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }


    /**
     * 每一个需要缓存的数据我们都来指定要放到哪个名字的缓存(缓存的分区【按照业务类型分】)
     * @Cacheable: 当前方法的结果需要缓存,如果缓存中有,方法不用调用,如果缓存中没有,会调用方法,最后将方法的结果放入缓存
     * 触发将数据保存到缓存的操作
     * 默认行为：
     *  1.如果缓存中有,方法不用调用
     *  2.key默认自动生成，缓存的名字：SimpleKey[](自主生成的key值)
     *  3.缓存的value值 默认使用jdk序列化
     *  4.默认的ttl时间：-1
     * 自定义：
     *  1.指定生成缓存使用的key
     * 	2.指定缓存数据存活时间	[配置文件中修改]
     * 	3.将数据保存为json格式
     */
    @Override
    @Cacheable(value = {"category"},key = "#root.method.name",sync = true)
    public List<CategoryEntity> getLevel1Categorys() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid",0));
    }


    @Cacheable(value = "category", key = "#root.methodName",sync = true)
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        List<CategoryEntity> entityList = baseMapper.selectList(null);
        // 查询所有一级分类
        List<CategoryEntity> level1Categorys = getCategoryEntities(entityList, 0L);

        // 封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 每一个一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getCategoryEntities(entityList, v.getCatId());
            // 封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), l2.getName(), l2.getCatId().toString(), null);
                    // 找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getCategoryEntities(entityList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            // 封装成指定格式
                            Catelog3Vo catelog3Vo = new Catelog3Vo(l3.getCatId().toString(), l3.getName(), l2.getCatId().toString());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));

        return parent_cid;
    }


    // springboot2.0以后默认使用lettuce作为操作redis的客户端,它使用neetty进行网络通信
    // lettuce的bug导致netty堆外内存溢出
    // 解决方案：不能使用-Dio.netty.maxDirectMemory进行设置
    //          升级lettuce客户端
    //          切换使用jedis
    public Map<String, List<Catelog2Vo>> getCatelogJson2() {
        // 加入缓存逻辑,缓存中存储的数据是json字符串
        String catelogJSON = redisTemplate.opsForValue().get("catelogJSON");
        if(StringUtils.isEmpty(catelogJSON)){
            // 缓存中没有
            Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatelogJsonFromDbWithRedisLock();

            return catelogJsonFromDb;
        }

        // 转为指定的对象
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catelogJSON,new TypeReference<Map<String, List<Catelog2Vo>>>(){});

        return result;
    }

    // TODO 考虑优化？所有线程在这里会变成单线程
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDBWithRedissonLock() {
        RLock lock = redisson.getLock("catelogJson-lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> data;
        try {
            data = getDataFromDB();
        } finally {
            lock.unlock();
        }
        return data;
    }

    // 从数据库查询并封装分类数据
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedisLock() {
        String uuid = UUID.randomUUID().toString();
        // 占分布式锁,设置过期时间加锁成功
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,300, TimeUnit.SECONDS);
        if(lock){
            Map<String, List<Catelog2Vo>> data;
            try {
                // 加锁成功，执行业务
                data = getDataFromDB();
            } finally {
                // 加锁成功，执行业务
                Map<String, List<Catelog2Vo>> dataFromDB = getDataFromDB();
                String lockValue = redisTemplate.opsForValue().get("lock");
                // 删除也必须是原子操作 Lua脚本操作 删除成功返回1 否则返回0
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                redisTemplate.execute(new DefaultRedisScript<Long>(script,Long.class),Arrays.asList("lock"),uuid);
            }
            return data;
        } else{
            // 重试加锁
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatelogJsonFromDbWithRedisLock();
        }


    }

    private Map<String, List<Catelog2Vo>> getDataFromDB() {
        // 得到锁之后应该再去缓存中确认一次，如果没有才需要继续查询
        String catelogJSON = redisTemplate.opsForValue().get("catelogJSON");
        if(!StringUtils.isEmpty(catelogJSON)){
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catelogJSON,new TypeReference<Map<String, List<Catelog2Vo>>>(){});
            return result;
        }

        List<CategoryEntity> entityList = baseMapper.selectList(null);
        // 查询所有一级分类
        List<CategoryEntity> level1Categorys = getCategoryEntities(entityList, 0L);

        // 封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 每一个一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getCategoryEntities(entityList, v.getCatId());
            // 封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), l2.getName(), l2.getCatId().toString(), null);
                    // 找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getCategoryEntities(entityList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            // 封装成指定格式
                            Catelog3Vo catelog3Vo = new Catelog3Vo(l3.getCatId().toString(), l3.getName(), l2.getCatId().toString());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));

        // 查到的数据再放入缓存
        String s = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catelogJSON",s,1,TimeUnit.DAYS);
        return parent_cid;
    }

    // 从数据库查询并封装分类数据
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithLocalLock() {
        // 解决缓存击穿问题
        synchronized (this) {
            return getDataFromDB();
        }
    }

    /**
     * 第一次查询的所有 CategoryEntity 然后根据 parent_cid去这里找
     */
    private List<CategoryEntity> getCategoryEntities(List<CategoryEntity> entityList, Long parent_cid) {

        return entityList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
    }

}