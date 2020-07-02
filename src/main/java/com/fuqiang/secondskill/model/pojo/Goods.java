package com.fuqiang.secondskill.model.pojo;

import com.fuqiang.basecommons.pojo.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p> Title: Goods </p>
 * <p> Description: Goods </p>
 * <p> Copyright: Xi An BestTop Technologies, ltd. Copyright(c) 2020 </p>
 * TODO
 *
 * @Author Fuqiang
 * @Version 0.0.1
 * Created by Fu on 2020/6/29 0029 16:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods extends BaseEntity<Goods> {

    private String name;
    private int num;
}
