package com.fuqiang.secondskill.api;

import com.fuqiang.basecommons.common.BaseController;
import com.fuqiang.basecommons.common.ResultEntity;
import com.fuqiang.secondskill.model.param.GoodParam;
import com.fuqiang.secondskill.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.locks.ReentrantLock;

/**
 * <p> Title: GoodsApi </p>
 * <p> Description: GoodsApi </p>
 * <p> Copyright: Xi An BestTop Technologies, ltd. Copyright(c) 2020 </p>
 * TODO
 *
 * @Author Fuqiang
 * @Version 0.0.1
 * Created by Fu on 2020/6/29 0029 17:00
 */
@RestController
@RequestMapping("/goods")
public class GoodsApi extends BaseController {

    @Autowired
    private GoodsService goodsService;

    /**
     * @param []
     * @return com.fuqiang.basecommons.common.ResultEntity
     * @description TODO     全查
     * @author Fuqiang
     * @date 2020/6/29 0029 17:01
     */
    @GetMapping("/query")
    public ResultEntity query() {
        return success(goodsService.query());
    }

    /**
     * @param [param]
     * @return com.fuqiang.basecommons.common.ResultEntity
     * @description TODO     新增
     * @author Fuqiang
     * @date 2020/6/29 0029 17:40
     */
    @PostMapping("/add")
    public ResultEntity add(@RequestBody GoodParam param) {
        goodsService.addGood(param);
        return success();
    }

    /**
     * @param param
     * @return com.fuqiang.basecommons.common.ResultEntity
     * @description TODO     秒杀
     * @author Fuqiang
     * @date 2020/6/30 0030 9:45
     */
    @PostMapping("/kill")
    public ResultEntity kill(@RequestBody GoodParam param) {
        return success(goodsService.secondKill(param));
    }

    /**
     * @param param
     * @return com.fuqiang.basecommons.common.ResultEntity
     * @description TODO     基于redis分布式锁秒杀
     * @author Fuqiang
     * @date 2020/7/2 0002 10:27
     */
    @PostMapping("/killRedis")
    public ResultEntity killRedis(@RequestBody GoodParam param) {
        goodsService.secondKillRedis(param);
        return success();
    }

    /**
     * @param param
     * @return com.fuqiang.basecommons.common.ResultEntity
     * @description TODO     基于redisson分布式锁秒杀
     * @author Fuqiang
     * @date 2020/7/2 0002 10:29
     */
    @PostMapping("/killRedisson")
    public ResultEntity killRedisson(@RequestBody GoodParam param) {
        goodsService.secondKillRedisson(param);
        return success();
    }
}
