package com.mybatishelper.core.wrapper.query;

import com.mybatishelper.core.wrapper.bridge.AbstractConditionWrapper;
import com.mybatishelper.core.wrapper.bridge.AbstractQueryWrapper;

import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class QueryWrapper<C extends AbstractConditionWrapper>
        extends AbstractQueryWrapper<C, QueryWrapper<C>>
{
    public QueryWrapper(C where){
        super(where,new ArrayList<>(AbstractConditionWrapper.DEFAULT_CONDITION_ELEMENTS_SIZE));
    }
    public QueryWrapper(C where,AbstractQueryWrapper absWrapper) {
        super(where,absWrapper);
    }
}
