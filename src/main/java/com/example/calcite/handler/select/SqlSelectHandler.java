package com.example.calcite.handler.select;

import com.example.calcite.entity.ColumnMapping;
import com.example.calcite.entity.SelectItem;
import com.example.calcite.handler.item.SelectItemHandler;
import org.apache.calcite.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SqlSelectHandler implements SqlHandler {

    public static final SelectItemHandler selectItemHandler = new SelectItemHandler();

    private static final SqlJoinHandler sqlJoinHandler = new SqlJoinHandler();

    @Override
    public void extractFromSqlNode(SqlNode sqlNode, List<ColumnMapping> columnsMapping, String parentFromTableAlias, boolean subQuery) {
        SqlSelect sqlSelect = (SqlSelect) sqlNode;
        String fromTableAlias = this.getFromAliasName(sqlSelect.getFrom(), parentFromTableAlias, subQuery);
        // 处理selectItem
        sqlSelect.getSelectList().forEach(selectItem -> selectItemHandler.handleExpression(SelectItem.builder()
                .subQuery(subQuery)
                .sqlNode(selectItem)
                .fromTableAlias(fromTableAlias)
                .columnMappings(columnsMapping).build()));

        // 处理from
        extractFromTableNode(columnsMapping, sqlSelect, fromTableAlias);


    }

    private void extractFromTableNode(List<ColumnMapping> columnsMapping, SqlSelect sqlSelect, String subQueryParentAlias) {
        SqlNode selectFrom = sqlSelect.getFrom();
        if (selectFrom instanceof SqlBasicCall) {
            SqlBasicCall sqlBasicCall = (SqlBasicCall) selectFrom;
            assert sqlBasicCall != null;
            SqlNode tableNode = sqlBasicCall.getOperandList().get(0);
            if (tableNode instanceof SqlIdentifier) {
                SqlIdentifier tableNode1 = (SqlIdentifier) tableNode;

                String dbAndTableName = String.join(".", ((SqlIdentifier) tableNode).names);
                String tableAlias = sqlBasicCall.getOperandList().get(1).toString();
                columnsMapping.forEach(columnMapping -> {
                    Set<String> parentColumns = columnMapping.getParentColumns();
                    Set<String> newParentColumns = parentColumns.stream()
                            .map(parentColumn -> parentColumn.replace(tableAlias, dbAndTableName))
                            .collect(Collectors.toSet());
                    columnMapping.setParentColumns(newParentColumns);
                });
            } else if (tableNode instanceof SqlSelect) {
                SqlSelect subSelect = (SqlSelect) tableNode;
                List<ColumnMapping> subColumnsMapping = new ArrayList<>();
                this.extractFromSqlNode(subSelect, subColumnsMapping, subQueryParentAlias, true);
                replaceSubQueryFromAlias(subColumnsMapping, subQueryParentAlias);
                mergeSubToMainColumnsMapping(columnsMapping, subColumnsMapping);
            }
        } else if (selectFrom instanceof SqlIdentifier) {
            SqlIdentifier sqlIdentifier = (SqlIdentifier) selectFrom;
            com.google.common.collect.ImmutableList<String> names = sqlIdentifier.names;
            String dbAndTableName = String.join(".", sqlIdentifier.names);
            columnsMapping.forEach(columnMapping -> {
                Set<String> parentColumns = columnMapping.getParentColumns();
                Set<String> newParentColumns = parentColumns.stream()
                        .map(parentColumn -> parentColumn.replace(subQueryParentAlias, dbAndTableName))
                        .collect(Collectors.toSet());
                columnMapping.setParentColumns(newParentColumns);
            });
        } else if (selectFrom instanceof SqlJoin) {
            SqlJoin sqlJoin = (SqlJoin) selectFrom;
            sqlJoinHandler.extractFromSqlNode(sqlJoin, columnsMapping, null, true);
        } else {
            throw new RuntimeException("暂不支持的SqlNode类型：" + selectFrom.getClass());
        }
    }

    private void mergeSubToMainColumnsMapping(List<ColumnMapping> columnsMapping, List<ColumnMapping> subColumnsMapping) {
        columnsMapping.forEach(mainColumnMapping -> {
            Set<String> parentColumns = mainColumnMapping.getParentColumns();
            Map<String, Set<String>> subColumnMap = subColumnsMapping.stream().collect(Collectors.toMap(ColumnMapping::getColumn, ColumnMapping::getParentColumns));
            List<Set<String>> parnetColumnsList = parentColumns.stream().map(parentColumn -> subColumnMap.get(parentColumn)).collect(Collectors.toList());
            Set<String> newParentColumns = parnetColumnsList.stream().flatMap(Set::stream).collect(Collectors.toSet());
            mainColumnMapping.setParentColumns(newParentColumns);
        });
    }

    private void replaceSubQueryFromAlias(List<ColumnMapping> subColumnsMapping, String subQueryParentAlias) {
        subColumnsMapping.forEach(columnMapping -> {
            String column = columnMapping.getColumn();
            String[] split = column.split("\\.");
            if (split.length == 1) {
                columnMapping.setColumn(String.format("%s.%s", subQueryParentAlias, column));
            } else if (split.length == 2) {
                columnMapping.setColumn(String.format("%s.%s", subQueryParentAlias, split[1]));
            }
        });
    }

    /**
     * 获取SqlNode中的From子句的别名
     *
     * @param from SqlNode
     * @return 别名
     */
    private String getFromAliasName(SqlNode from, String parentFromTableAlias, boolean subQuery) {
        if (from instanceof SqlIdentifier) {
            return parentFromTableAlias;
        } else if (from instanceof SqlBasicCall) {
            SqlBasicCall sqlBasicCall = (SqlBasicCall) from;
            return sqlBasicCall.getKind() == SqlKind.AS ? sqlBasicCall.getOperandList().get(1).toString()
                    : sqlBasicCall.getOperandList().get(0).toString();
        } else if (from instanceof SqlJoin) {
            return null;
        } else {
            throw new RuntimeException("暂不支持的SqlNode类型：" + from.getClass());
        }

    }
}
