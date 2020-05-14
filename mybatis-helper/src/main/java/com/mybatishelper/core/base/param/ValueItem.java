package com.mybatishelper.core.base.param;

import com.mybatishelper.core.base.Item;
import com.mybatishelper.core.enums.ItemType;
import lombok.Getter;

/**
 * 原值参数
 * @param <T>
 */
@Getter
public class ValueItem<T> implements Item<T> {
    private T value;
    public ValueItem(T value){
        this.value = value;
    }
    public static <T> ValueItem<T> valueOf(T value){
        return new ValueItem<>(value);
    }

    @Override
    public ValueItem<T> withValue(T value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return value.toString();
    }
    @Override
    public ItemType getType() {
        return ItemType.VALUE;
    }
}