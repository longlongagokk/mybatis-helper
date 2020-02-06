package club.yourbatis.hi.wrapper;

import club.yourbatis.hi.base.FieldValue;
import club.yourbatis.hi.base.Item;
import club.yourbatis.hi.enums.ConditionType;
import club.yourbatis.hi.wrapper.factory.FlexibleConditionWrapper;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @param <L> 操作符左项
 * @param <R> 操作符右项
 * @param <S> 自身
 */
@SuppressWarnings("unused")
public interface IConditioner<L,R,S> {
    /**
     * 等于
     *
     */
    S eq(L left, R right);
    S eq(FieldValue fv);

    /**
     * 大于
     *
     */
    S gt(L left, R right);
    S gt(FieldValue fv);

    /**
     * 小于
     *
     */
    S lt(L left, R right);
    S lt(FieldValue fv);

    /**
     * 大于等于
     *
     */
    S ge(L left, R right);
    S ge(FieldValue fv);

    /**
     * 小于等于
     *
     */
    S le(L left, R right);
    S le(FieldValue fv);

    /**
     * 不等于
     *
     */
    S neq(L left, R right);
    S neq(FieldValue fv);

    /**
     * like
     *
     */
    S like(L left, R right);
    S like(FieldValue fv);

    /**
     * 空
     *
     */
    S isNull(L left);

    /**
     * 非空
     *
     */
    S notNull(L left);

    /**
     * item in(item0,item1...)
     * warn: if use {@link FlexibleConditionWrapper} wrapper,
     * you must make sure that the collection's obj type to {@link Item},otherwise it will throw an exception
     * @param left wrapper left opera
     * @param values in values
     * @return caller
     */
    S in(L left, Collection<?> values);

    /**
     * not in,like in,see {@link #in}
     *
     */
    S notIn(L left, Collection<?> values);

    /**
     *
     */
    S between(L left, R r0, R r1);

    /**
     * variable condition query
     * warn：see {@link #in}
     * @param type ConditionType
     * @param left left opera
     * @param right something to wrapper
     * @return caller
     */
    S where(ConditionType type, L left, Collection<?> right);
    S and(Consumer<S> consumer);
    S or(Consumer<S> consumer);
}
