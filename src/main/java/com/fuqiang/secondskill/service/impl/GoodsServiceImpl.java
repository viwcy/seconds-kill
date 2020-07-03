package com.fuqiang.secondskill.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuqiang.basecommons.util.IDWorkerUtil;
import com.fuqiang.basecommons.util.RedisLockUtil;
import com.fuqiang.secondskill.common.Constants;
import com.fuqiang.secondskill.mapper.GoodsMapper;
import com.fuqiang.secondskill.model.param.GoodParam;
import com.fuqiang.secondskill.model.pojo.Goods;
import com.fuqiang.secondskill.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p> Title: GoodsServiceImpl </p>
 * <p> Description: GoodsServiceImpl </p>
 * <p> Copyright: Xi An BestTop Technologies, ltd. Copyright(c) 2020 </p>
 * TODO
 *
 * @Author Fuqiang
 * @Version 0.0.1
 * Created by Fu on 2020/6/29 0029 16:57
 */
@Service
@Slf4j
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private IDWorkerUtil idWorkerUtil;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * @param []
     * @return java.util.List<com.fuqiang.secondskill.model.pojo.Goods>
     * @description TODO     全查
     * @author Fuqiang
     * @date 2020/6/29 0029 16:58
     */
    @Override
    public List<Goods> query() {
        return goodsMapper.selectList(new QueryWrapper<>());
    }

    /**
     * @param [param]
     * @return void
     * @description TODO     新增
     * @author Fuqiang
     * @date 2020/6/29 0029 17:37
     */
    @Override
    public void addGood(GoodParam param) {
        Goods goods = new Goods();
        param.setId(idWorkerUtil.getIdStr());
        BeanUtils.copyProperties(param, goods);
        goodsMapper.insert(goods);
        log.info("MySQL存储成功: {}", JSON.toJSONString(goods));
        redisTemplate.opsForValue().set(Constants.REDIS_GOOD_PREFIX + goods.getId() + ":" + goods.getName(), goods.getNum() + "");
        log.info("redis缓存成功");
    }

    /**
     * @param id
     * @return java.lang.String
     * @description TODO     秒杀
     * @author Fuqiang
     * @date 2020/6/30 0030 9:31
     */
    @Override
    public String secondKill(GoodParam param) {
        String result;
        synchronized (this) {
            String value = redisTemplate.opsForValue().get(Constants.REDIS_GOOD_PREFIX + param.getId() + ":" + param.getName()).toString();
            int anInt = Integer.parseInt(value);
            if (anInt > 0) {
                redisTemplate.opsForValue().set(Constants.REDIS_GOOD_PREFIX + param.getId() + ":" + param.getName(), (anInt - 1) + "");
                Goods goods = goodsMapper.selectOne(new QueryWrapper<Goods>().eq("id", param.getId()));
                goods.setNum(anInt - 1);
                goodsMapper.updateById(goods);

                //生成订单，其他处理逻辑
                //...

                log.info("抢购成功，商品id: {}，商品余量: {}", param.getId(), anInt - 1);
                result = "抢购成功，商品剩余: " + (anInt - 1);
            } else {
                log.info("商品已经售馨，欢迎下次抢购！");
                result = "商品已经售馨，欢迎下次抢购！";
            }
        }
        return result;
    }

    /**
     * @param param
     * @return void
     * @description TODO     基于redis分布式锁秒杀
     * @author Fuqiang
     * @date 2020/6/30 0030 15:39
     */
    @Override
    public void secondKillRedis(GoodParam param) {
        String id = new Random().nextInt(100) + "";
        boolean tryLock = redisLockUtil.tryLock(param.getId(), id, 1L, TimeUnit.SECONDS);
        if (tryLock) {
            log.info("用户{}获取锁成功", id);
            String value = redisTemplate.opsForValue().get(Constants.REDIS_GOOD_PREFIX + param.getId() + ":" + param.getName()).toString();
            int anInt = Integer.parseInt(value);
            if (anInt <= 0) {
                log.info("商品已售罄，欢迎下次抢购!");
            } else {
                redisTemplate.opsForValue().set(Constants.REDIS_GOOD_PREFIX + param.getId() + ":" + param.getName(), (anInt - 1) + "");
                log.info("用户{}抢购成功，商品id: {}", id, param.getId());
            }
            boolean releaseLock = redisLockUtil.releaseLock(param.getId(), id);
            if (releaseLock) {
                log.info("用户{}释放锁成功", id);
                System.out.println();
            } else {
                log.info("用户{}释放锁失败", id);
            }
        }
    }

    /**
     * @param param
     * @return void
     * @description TODO     redisson分布式锁秒杀
     * @author Fuqiang
     * @date 2020/7/3 0003 9:29
     */
    @Override
    public void secondKillRedisson(GoodParam param) {
        String id = new Random().nextInt(100) + "";
        RLock lock = redissonClient.getLock(param.getId());

        boolean abSent = false;
        try {
            abSent = lock.tryLock(1L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (abSent) {
            log.info("用户{}获取锁成功", id);
            String value = redisTemplate.opsForValue().get(Constants.REDIS_GOOD_PREFIX + param.getId() + ":" + param.getName()).toString();
            int anInt = Integer.parseInt(value);
            if (anInt <= 0) {
                log.info("商品已售罄，欢迎下次抢购!");
            } else {
                redisTemplate.opsForValue().set(Constants.REDIS_GOOD_PREFIX + param.getId() + ":" + param.getName(), (anInt - 1) + "");
                log.info("用户{}抢购成功，商品id: {}", id, param.getId());
            }
            lock.unlock();
            log.info("用户{}释放锁成功", id);
            System.out.println();
        }

    }
}
