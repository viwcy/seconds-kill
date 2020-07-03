package com.fuqiang.secondskill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuqiang.secondskill.model.param.GoodParam;
import com.fuqiang.secondskill.model.pojo.Goods;

import java.util.List;

/**
 * <p> Title: GoodsService </p>
 * <p> Description: GoodsService </p>
 * <p> Copyright: Xi An BestTop Technologies, ltd. Copyright(c) 2020 </p>
 * TODO
 *
 * @Author Fuqiang
 * @Version 0.0.1
 * Created by Fu on 2020/6/29 0029 16:56
 */
public interface GoodsService extends IService<Goods> {

    /**
     * @param []
     * @return java.util.List<com.fuqiang.secondskill.model.pojo.Goods>
     * @description TODO     全查
     * @author Fuqiang
     * @date 2020/6/29 0029 16:58
     */
    List<Goods> query();

    /**
     * @param [param]
     * @return void
     * @description TODO     新增
     * @author Fuqiang
     * @date 2020/6/29 0029 17:36
     */
    void addGood(GoodParam param);

    /**
     * @param id
     * @return java.lang.String
     * @description TODO     秒杀
     * @author Fuqiang
     * @date 2020/6/30 0030 9:30
     */
    String secondKill(GoodParam param);

    /**
     * @param param
     * @return void
     * @description TODO    基于redis分布式锁秒杀
     * @author Fuqiang
     * @date 2020/6/30 0030 15:39
     */
    void secondKillRedis(GoodParam param);

    /**
     * @param param
     * @return void
     * @description TODO     redisson分布式锁秒杀
     * @author Fuqiang
     * @date 2020/7/3 0003 9:28
     */
    void secondKillRedisson(GoodParam param);
}
