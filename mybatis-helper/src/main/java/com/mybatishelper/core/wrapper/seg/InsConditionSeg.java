package com.mybatishelper.core.wrapper.seg;

import com.mybatishelper.core.base.Item;
import com.mybatishelper.core.consts.ConstValue;
import com.mybatishelper.core.enums.ConditionType;
import com.mybatishelper.core.wrapper.bridge.AbstractQueryWrapper;

public class InsConditionSeg extends SimpleConditionSeg {
    private InsConditionSeg(ConditionType type, Item... items) {
        super(type, items);
    }
    public static InsConditionSeg valueOf(ConditionType type, Item... items) {
        return new InsConditionSeg(type,items);
    }

    @Override
    public String createSql(AbstractQueryWrapper wrapper) {
        if (items == null || items.length < 2) {
            throw new IllegalArgumentException("argument is not math with condition["+type.getOpera()+"]");
        }
        StringBuilder sb = new StringBuilder(wrapItemSql(items[0], wrapper));
        sb.append(type.getOpera());
        sb.append(ConditionType.LEFT_WRAPPER.getOpera());
        for (int i = 1; i < items.length - 1; ++i) {
            sb.append(wrapItemSql(items[i], wrapper));
            sb.append(ConstValue.COMMA);
        }
        sb.append(wrapItemSql(items[items.length - 1], wrapper));
        sb.append(ConditionType.RIGHT_WRAPPER.getOpera());
        return sb.toString();
    }
}
