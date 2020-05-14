package com.mybatishelper.core.wrapper.delete;

import com.mybatishelper.core.wrapper.IDeleteWrapper;
import com.mybatishelper.core.wrapper.bridge.AbstractConditionWrapper;
import com.mybatishelper.core.wrapper.bridge.AbstractJoinerWrapper;
import com.mybatishelper.core.wrapper.factory.PropertyConditionWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DeleteWrapper<C extends AbstractConditionWrapper>
        extends AbstractJoinerWrapper<C, DeleteWrapper<C>>
        implements IDeleteWrapper<DeleteWrapper<C>,C> {
    Set<String> deleteAlias = new HashSet<>(1<<1);
    public DeleteWrapper(C where){
        super(where,new ArrayList<>(AbstractConditionWrapper.DEFAULT_CONDITION_ELEMENTS_SIZE));
    }

    public static IDeleteWrapper<DeleteWrapper<PropertyConditionWrapper>, PropertyConditionWrapper> build(){
        return new DeleteWrapper<>(new PropertyConditionWrapper());
    }
    @Override
    public DeleteWrapper<C> delete(String alias) {
        deleteAlias.add(alias);
        return this;
    }
}