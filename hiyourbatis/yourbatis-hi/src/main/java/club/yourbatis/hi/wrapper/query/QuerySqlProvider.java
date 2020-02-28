package club.yourbatis.hi.wrapper.query;

import club.yourbatis.hi.base.Field;
import club.yourbatis.hi.base.Page;
import club.yourbatis.hi.base.Primary;
import club.yourbatis.hi.base.Sortable;
import club.yourbatis.hi.base.field.SelectField;
import club.yourbatis.hi.base.meta.TableMetaInfo;
import club.yourbatis.hi.base.param.FieldItem;
import club.yourbatis.hi.consts.ConstValue;
import club.yourbatis.hi.enums.ConditionType;
import club.yourbatis.hi.util.Assert;
import club.yourbatis.hi.util.TableInfoHelper;
import club.yourbatis.hi.wrapper.ICountWrapper;
import club.yourbatis.hi.wrapper.ISelectorWrapper;
import club.yourbatis.hi.wrapper.bridge.AbsSqlProvider;
import club.yourbatis.hi.wrapper.condition.ExchangeFieldItem;
import club.yourbatis.hi.wrapper.condition.LinkSelectItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 后期记得加缓存
 */
@Slf4j
public class QuerySqlProvider extends AbsSqlProvider {

    public String selectItemByPrimaryKey(ProviderContext context, Primary primary) {
        TableMetaInfo tableMetaInfo = TableInfoHelper.getTableInfoByProviderContext(context);
        Assert.notNull(tableMetaInfo.getPrimary(),String.format("table {0} do not have any primaryKey!", tableMetaInfo.getTableName()));
        StringBuilder sb = new StringBuilder("select ");

        Iterator<Map.Entry<String, String>> it = tableMetaInfo.getFieldWithColumns().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            sb
                    .append(entry.getValue())
                    .append(ConstValue.BLANK)
                    .append(ConstValue.BACKTRICKS)
                    .append(entry.getKey())
                    .append(ConstValue.BACKTRICKS)
                    .append(ConstValue.COMMA)
            ;
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(" from ")
        .append(tableMetaInfo.getTableName())
        .append(" where ")
        .append(tableMetaInfo.getPrimary().getName())
        .append(" = #{value}")
        .toString();
        return sb.toString();
    }
    public String selectOne(ProviderContext context, ISelectorWrapper wrapper) {
        return select(context, wrapper) + " limit 1";
    }

    public String selectList(ProviderContext context, ISelectorWrapper wrapper) {
        String sql = select(context,wrapper);
        Page page = wrapper.getPage();
        if (page != null) {
            return sql + " limit " + (page.getPageSize() * (page.getPageIndex() - 1)) + "," + page.getPageSize();
        }
        return sql;
    }
    private String select(ProviderContext context, ISelectorWrapper wrapper){
        SelectWrapper selectWrapper = (SelectWrapper)wrapper;
        Map<String, TableMetaInfo> tableMetaInfoMap = checkAndReturnFromTables(context,selectWrapper);
        boolean selectOwn = selectWrapper.selectMain;
        Set<SelectField> selectFields = selectWrapper.selectItems;
        StringBuilder selectSql = new StringBuilder("select ");
        //#region select items
        //select selectItems first
        if(!CollectionUtils.isEmpty(selectFields)){
            for(SelectField field:selectFields){
                if(field.isOriginal()){
                    selectSql.append(field.getOriginalInfo()).append(ConstValue.COMMA);
                    continue;
                }
                selectSql
                        .append(LinkSelectItem.valueOf(ConditionType.DONOTHINE,
                                FieldItem.valueOf(field),
                                FieldItem.valueOf(field.getColumnAlias())).createSql(selectWrapper))
                        .append(ConstValue.COMMA)
                ;
            }
        }
        //select own next
        if (selectOwn || CollectionUtils.isEmpty(selectFields)) {
            TableMetaInfo mainMeta = tableMetaInfoMap.get(ConstValue.MAIN_ALIAS);
            Iterator<Map.Entry<String, String>> it = mainMeta.getFieldWithColumns().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                selectSql
                        .append(ConstValue.MAIN_ALIAS)
                        .append(ConstValue.DOT)
                        .append(entry.getValue())
                        .append(ConstValue.BLANK)
                        .append(ConstValue.BACKTRICKS)
                        .append(entry.getKey())
                        .append(ConstValue.BACKTRICKS)
                        .append(ConstValue.COMMA)
                ;
            }
        }
        selectSql.deleteCharAt(selectSql.length() - 1);
        //#endregion
        selectSql.append(" from ");
        //from tables
        createFromTableSql(selectSql,selectWrapper);

        //join infos
        createJoinInfoSql(selectSql,selectWrapper);

        //conditions
        createWhereSql(selectSql,selectWrapper);

        //order
        Set<Sortable> sortItems = selectWrapper.sortItems;
        if(!sortItems.isEmpty()){
            selectSql.append(" order by ");
            for(Sortable s:sortItems){
                for(Field f:s.getSortFields()){
                    selectSql
                            .append(ExchangeFieldItem.valueOf(ConditionType.DONOTHINE,FieldItem.valueOf(f)).createSql(selectWrapper))
                            .append(ConstValue.BLANK)
                            .append(s.getOrder())
                            .append(ConstValue.COMMA);
                }
            }
            selectSql.deleteCharAt(selectSql.length() - 1);
        }

        return selectSql.toString();
    }

    public String selectCount(ProviderContext context, ICountWrapper wrapper) {
        CountWrapper countWrapper = (CountWrapper)wrapper;
        checkAndReturnFromTables(context,countWrapper);
        StringBuilder countSql = new StringBuilder("select count(1) from ");

        //from tables
        createFromTableSql(countSql,countWrapper);

        //join infos
        createJoinInfoSql(countSql,countWrapper);

        //conditions
        createWhereSql(countSql,countWrapper);

        return countSql.toString();
    }

}
