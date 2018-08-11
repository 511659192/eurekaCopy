package com.netflix.eureka.registry.rule;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.eureka.lease.Lease;

import java.util.ArrayList;
import java.util.List;

/**
 * This rule takes an ordered list of rules and returns the result of the first match or the
 * result of the {@link AlwaysMatchInstanceStatusRule}.
 *
 * Created by Nikos Michalakis on 7/13/16.
 */
public class FirstMatchWinsCompositeRule implements InstanceStatusOverrideRule {

    private final InstanceStatusOverrideRule[] rules;
    private final InstanceStatusOverrideRule defaultRule;
    private final String compositeRuleName;

    public FirstMatchWinsCompositeRule(InstanceStatusOverrideRule... rules) {
        this.rules = rules;
        // 设置默认的匹配规则
        this.defaultRule = new AlwaysMatchInstanceStatusRule();
        // Let's build up and "cache" the rule name to be used by toString();
        List<String> ruleNames = new ArrayList<>(rules.length+1);
        for (int i = 0; i < rules.length; ++i) {
            ruleNames.add(rules[i].toString());
        }
        ruleNames.add(defaultRule.toString());
        compositeRuleName = ruleNames.toString();
    }

    @Override
    public StatusOverrideResult apply(InstanceInfo instanceInfo,
                                      Lease<InstanceInfo> existingLease,
                                      boolean isReplication) {
        for (int i = 0; i < this.rules.length; ++i) {
            // 调用具体规则匹配状态，其实就是构造方法里面传入的那三个规则
            StatusOverrideResult result = this.rules[i].apply(instanceInfo, existingLease, isReplication);
            // 匹配成功，则返回
            if (result.matches()) {
                return result;
            }
        }
        // 如果以上都没有匹配成功，则使用该规则进行匹配
        return defaultRule.apply(instanceInfo, existingLease, isReplication);
    }

    @Override
    public String toString() {
        return this.compositeRuleName;
    }
}
