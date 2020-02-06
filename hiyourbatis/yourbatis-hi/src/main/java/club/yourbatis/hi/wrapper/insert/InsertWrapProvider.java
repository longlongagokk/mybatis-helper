package club.yourbatis.hi.wrapper.insert;

import club.yourbatis.hi.annotation.Column;
import club.yourbatis.hi.base.meta.TableMetaInfo;
import club.yourbatis.hi.util.Assert;
import club.yourbatis.hi.util.ContextUtil;
import club.yourbatis.hi.util.TableInfoHelper;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 后期记得加缓存
 */
@Slf4j
public class InsertWrapProvider {
    public String insert(Object entity)throws Exception{
        return _insert(entity,false);
    }

    public String insertSelective(Object entity)throws Exception{
        return _insert(entity,true);
    }

    private String _insert(Object entity,boolean skipNull)throws Exception{
        Assert.notNull(entity,"entity could not be null!");
        TableMetaInfo tableMetaInfo = TableInfoHelper.getTableInfoFromEntityClass(entity.getClass());
        StringBuilder insertSql = new StringBuilder("insert into ").append(tableMetaInfo.getTableName()).append("(");
        Field[] fields = ContextUtil.getAllColumnFieldOfObject(entity.getClass());
        StringBuilder fieldSql = new StringBuilder();
        StringBuilder valueSql = new StringBuilder();
        for (Field field : fields) {
            field.setAccessible(true);
            if (skipNull && field.get(entity) == null) {
                continue;
            }
            fieldSql.append(field.getAnnotation(Column.class).value()).append(",");
            valueSql.append("#{").append(field.getName()).append("},");
        }
        if(fieldSql.length() == 0){
            throw new IllegalArgumentException("no field to insert ！");
        }
        fieldSql.deleteCharAt(fieldSql.length() - 1);
        valueSql.deleteCharAt(valueSql.length() - 1);
        return insertSql.append(fieldSql).append(") VALUES (").append(valueSql).append(")").toString();
    }

}
