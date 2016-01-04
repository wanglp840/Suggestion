package common.entity;

/**
 * @Auther wanglp
 * @Time 16/1/2 下午3:51
 * @Email wanglp840@nenu.edu.cn
 */

public class Rule {
    private int ruleId;
    private String expression;
    private Double weight;


    public Rule(int ruleId, String expression, Double weight){
        this.ruleId = ruleId;
        this.expression = expression;
        this.weight = weight;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
