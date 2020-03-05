package club.yourbatis.hi.wrapper.seg;

import club.yourbatis.hi.base.Item;
import club.yourbatis.hi.enums.ConditionType;
import club.yourbatis.hi.wrapper.bridge.AbstractQueryWrapper;

public class BetweenConditionSeg extends SimpleConditionSeg {
    private BetweenConditionSeg(Item... items) {
        super(ConditionType.BETWEEN,items);
    }

    public static BetweenConditionSeg valueOf(Item... items) {
        return new BetweenConditionSeg(items[0],items[1],items[2]);
    }

    @Override
    public String createSql(AbstractQueryWrapper wrapper) {
        StringBuilder sb = new StringBuilder(ConditionType.LEFTWRAPPER.getOpera());
        sb.append(wrapItemSql(items[0],wrapper));
        sb.append(type.getOpera());
        sb.append(wrapItemSql(items[1],wrapper));
        sb.append(ConditionType.AND.getOpera());
        sb.append(wrapItemSql(items[2],wrapper));
        sb.append(ConditionType.RIGHTWRAPPER.getOpera());
        return sb.toString();
    }
}