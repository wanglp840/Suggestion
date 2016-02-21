package com.suggestion.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * @Auther wanglp
 * @Time 16/1/2 下午3:51
 * @Email wanglp840@nenu.edu.cn
 */

@Getter
@Setter
public class Rule {
    private int ruleId;
    private String expression;
    private Double weight;


    public Rule(int ruleId, String expression, Double weight) {
        this.ruleId = ruleId;
        this.expression = expression;
        this.weight = weight;
    }
}
