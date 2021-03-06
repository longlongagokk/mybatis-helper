package com.mybatishelper.core.wrapper.bridge;

import com.mybatishelper.core.base.Item;
import com.mybatishelper.core.base.meta.ItemPar;
import com.mybatishelper.core.base.param.ParamItem;
import com.mybatishelper.core.base.param.ValueItem;
import com.mybatishelper.core.enums.ConditionType;
import com.mybatishelper.core.enums.ItemType;
import com.mybatishelper.core.util.LinkStack;
import com.mybatishelper.core.util.StringUtils;
import com.mybatishelper.core.wrapper.IConditioner;
import com.mybatishelper.core.wrapper.IQueryWrapper;
import com.mybatishelper.core.wrapper.ISqlSegment;
import com.mybatishelper.core.wrapper.IWrapper;
import com.mybatishelper.core.wrapper.factory.FlexibleConditionWrapper;
import com.mybatishelper.core.wrapper.factory.PropertyConditionWrapper;
import com.mybatishelper.core.wrapper.query.QueryWrapper;
import com.mybatishelper.core.wrapper.seg.*;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractConditionWrapper<L,R, S extends AbstractConditionWrapper>
        implements IWrapper, IConditioner<L,R,S>,Cloneable {
    static final Logger logger = LoggerFactory.getLogger(AbstractQueryWrapper.class);
    public final static int DEFAULT_CONDITION_ELEMENTS_SIZE = 1 << 3;
    AbstractQueryWrapper caller;
    protected List<AbstractQueryWrapper> existsQueries;
    private StringBuilder where = new StringBuilder();

    protected String paramAlias;
    protected List<Item> params;
    protected List<ISqlSegment> fields;
    protected LinkStack<ConditionType> closure;
    private volatile boolean barrier;
    private boolean sqlCreated;
    public AbstractConditionWrapper(int paramSize) {
        this(paramSize,null);
    }
    public AbstractConditionWrapper(int paramSize,AbstractQueryWrapper caller) {
        this(paramSize,caller,"");
    }
    public AbstractConditionWrapper(int paramSize,AbstractQueryWrapper caller,String paramAlias) {
        reset(paramSize,caller,paramAlias);
    }
    void reset(int paramSize,AbstractQueryWrapper caller,String paramAlias){
        this.params = new ArrayList<>(paramSize);
        this.fields = new ArrayList<>(params.size());
        this.caller = caller;
        this.existsQueries = new ArrayList<>(1<<2);
        this.paramAlias = paramAlias;
        this.sqlCreated = false;
        this.barrier = true;
        //this.where.delete(0,this.where.length());
        this.where = new StringBuilder();
        this.closure = new LinkStack<>();
        closure.push(ConditionType.AND);
    }
    public AbstractConditionWrapper(AbstractConditionWrapper copy) {
        this(DEFAULT_CONDITION_ELEMENTS_SIZE);
        this.paramAlias = copy.paramAlias;
        this.fields = copy.fields;
        this.closure = copy.closure;
        this.params = copy.params;
        this.barrier = copy.barrier;
        this.caller = copy.caller;
        this.existsQueries = copy.existsQueries;
    }
    public String getConditionSql() {
        if(sqlCreated){
            return where.toString();
        }
        sqlCreated = true;
        for(ISqlSegment e:fields){
            where.append(e.createSql(caller));
        }
        return where.toString();
    }

    private int currentExistsInfo = 0;
    public String getExistsFullSql() {
        AbstractQueryWrapper currentExistsFull = existsQueries.get(currentExistsInfo++);
        StringBuilder existsSql = new StringBuilder(" select 1 from ");
        //from tables
        AbsSqlProvider.createFromTableSql(existsSql,currentExistsFull);

        //join infos
        AbsSqlProvider.createJoinInfoSql(existsSql,currentExistsFull);

        //conditions
        AbsSqlProvider.createWhereSql(existsSql,currentExistsFull);

        return existsSql.toString();
    }

    @Override
    public S eq(L left,R right) {
        return exchangeItems(ConditionType.EQ, left, Collections.singletonList(right));
    }
    @Override
    public S eq(ItemPar fv) {
        return exchangeItems(ConditionType.EQ,fv);
    }

    @Override
    public S gt(L left,R right) {
        return exchangeItems(ConditionType.GT, left,Collections.singletonList(right));
    }
    @Override
    public S gt(ItemPar fv) {
        return exchangeItems(ConditionType.GT,fv);
    }

    @Override
    public S lt(L left,R right) {
        return exchangeItems(ConditionType.LT, left,Collections.singletonList(right));
    }
    @Override
    public S lt(ItemPar fv) {
        return exchangeItems(ConditionType.LT,fv);
    }

    @Override
    public S ge(L left,R right) {
        return exchangeItems(ConditionType.GE, left,Collections.singletonList(right));
    }
    @Override
    public S ge(ItemPar fv) {
        return exchangeItems(ConditionType.GE,fv);
    }

    @Override
    public S le(L left,R right) {
        return exchangeItems(ConditionType.LE, left,Collections.singletonList(right));
    }
    @Override
    public S le(ItemPar fv) {
        return exchangeItems(ConditionType.LE,fv);
    }

    @Override
    public S neq(L left,R right) {
        return exchangeItems(ConditionType.NEQ, left,Collections.singletonList(right));
    }
    @Override
    public S neq(ItemPar fv) {
        return exchangeItems(ConditionType.NEQ,fv);
    }

    @Override
    public S like(L left,R right) {
        return exchangeItems(ConditionType.LIKE, left,Collections.singletonList(right));
    }
    @Override
    public S like(ItemPar fv) {
        return exchangeItems(ConditionType.LIKE,fv);
    }

    @Override
    public S isNull(L left) {
        return exchangeItems(ConditionType.ISNULL,left,Collections.emptyList());
    }
    @Override
    public S notNull(L left) {
        return exchangeItems(ConditionType.NOTNULL,left,Collections.emptyList());
    }

    @Override
    public S in(L left, Collection<?> values) {
        return exchangeItems(ConditionType.IN,left,values);
    }

    @Override
    public S notIn(L left, Collection<?> values) {
        return exchangeItems(ConditionType.NOT_IN,left,values);
    }

    @Override
    public S exists(String originalSql,Object...params) {
        return existsFull(originalSql,ConditionType.EXISTS,params);
    }
    @Override
    public S notExists(String originalSql,Object...params) {
        return existsFull(originalSql,ConditionType.NOT_EXISTS,params);
    }

    @Override
    public S between(L left, R r0, R r1) {
        List<R> list = new ArrayList<>();
        list.add(r0);
        list.add(r1);
        return exchangeItems(ConditionType.BETWEEN,left,list);
    }
    @Override
    public S where(ConditionType type,L left, Collection<?> right) {
        return exchangeItems(type,left,right);
    }

    @Override
    public S and(Consumer<S> consumer) {
        return closure(consumer,ConditionType.AND);
    }

    @Override
    public S or(Consumer<S> consumer) {
        return closure(consumer,ConditionType.OR);
    }

    //*****************************switch*************************************//

    public PropertyConditionWrapper d() {
        PropertyConditionWrapper wrapper = new PropertyConditionWrapper(this);
        this.barrier = false;
        return wrapper;
    }

    public FlexibleConditionWrapper f() {
        FlexibleConditionWrapper wrapper = new FlexibleConditionWrapper(this);
        this.barrier = false;
        return wrapper;
    }


    protected S toTheMoon(ConditionType type, Item...items) {
        switch (type){
            case EQ:
            case NEQ:
            case GE:
            case GT:
            case LE:
            case LT:
            case LIKE:
            case ISNULL:
            case NOTNULL:
            case DO_NOTHING:
                addElement(SimpleConditionSeg.valueOf(type,items));
                break;
            case IN:
            case NOT_IN:
                if(items.length > 1) {
                    addElement(InsConditionSeg.valueOf(type, items));
                }
                break;
            case OR:
            case AND:
            case CLOSURE:
            case LEFT_WRAPPER:
            case RIGHT_WRAPPER:
                break;
            case BETWEEN:
                addElement(BetweenConditionSeg.valueOf(items));
                break;
        }
        return (S)this;
    }
    protected S existsFull(Consumer<QueryWrapper<S>> consumer,AbstractQueryWrapper queryWrapper,ConditionType conditionType) {
        queryWrapper.aliasTables = this.caller.aliasTables;
        queryWrapper.thisParamAlias = this.caller.thisParamAlias + "where.existsQueries["+existsQueries.size()+"].";
        queryWrapper.where.paramAlias = this.caller.where.paramAlias + "existsQueries["+existsQueries.size()+"].where.";
        this.existsQueries.add(queryWrapper);
        addElement(ExistsFullConditionSeg.valueOf(conditionType,null));
        consumer.accept((QueryWrapper<S>)queryWrapper);
        return (S)this;
    }

    protected S existsFull(String originalSql,ConditionType conditionType,Object...params) {
        if(StringUtils.isEmpty(originalSql)){
            return (S)this;
        }
        StringBuilder existsSql = new StringBuilder(conditionType.getOpera()).append("(");
        int qi = 0,ln = originalSql.length(),pi = 0;
        while(qi != ln){
            char c = originalSql.charAt(qi++);
            if(c == '?'){
                Item item = wrapParamByValue(params[pi++]);//set into param
                existsSql.append(item.toString());
            }else{
                existsSql.append(c);
            }
        }
        existsSql.append(")");
        toTheMoon(ConditionType.DO_NOTHING,ValueItem.valueOf(existsSql));
        return (S)this;
    }

    protected void addElement(ISqlSegment element){
        if(barrier){
            barrier = false;
        }else{
            final ConditionType _closureType = closure.peek();
            fields.add(w -> _closureType.getOpera());
        }
        fields.add(element);
    }
    protected Item wrapItemIfHasParam(Item it){
        if(it.getType() == ItemType.PARAM){
            return wrapParamByValue(it.getValue());
        }
        return it;
    }
    protected Item wrapParamByValue(Object value){
        int index = params.size();
        params.add(ValueItem.valueOf(value));
        return ParamItem.valueOf(paramAlias + "params",index);
    }

    protected abstract S exchangeItems(ConditionType type,L left, Collection<?> rights);
    private S exchangeItems(ConditionType type, ItemPar fv){
        Item right = wrapItemIfHasParam(fv.getValue());
        Item left = wrapItemIfHasParam(fv.getKey());
        return toTheMoon(type,left,right);
    }

    @Override
    protected S clone() throws CloneNotSupportedException {
        return (S)super.clone();
    }

    private S closure(Consumer<S> consumer, ConditionType type) {
        if(!barrier){
            barrier = true;
            final ConditionType _closureType = closure.peek();
            fields.add(w -> _closureType.getOpera());
        }
        fields.add(w -> ConditionType.LEFT_WRAPPER.getOpera());
        closure.push(type);
        consumer.accept((S)this);
        fields.add(w -> ConditionType.RIGHT_WRAPPER.getOpera());
        closure.pop();
        return (S)this;
    }





}
