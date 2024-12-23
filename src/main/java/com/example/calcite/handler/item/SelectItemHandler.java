package com.example.calcite.handler.item;

import com.example.calcite.entity.ColumnMapping;
import com.example.calcite.entity.SelectItem;
import com.google.common.collect.Sets;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlCase;

import java.util.*;
import java.util.function.Consumer;

public class SelectItemHandler {


    public static final Map<String, Consumer<SelectItem>> FUNCTION_MAP = new HashMap<>();

    public SelectItemHandler() {
        FUNCTION_MAP.put("org.apache.calcite.sql.SqlIdentifier", this::handleSqlIdentifier);
        FUNCTION_MAP.put("org.apache.calcite.sql.SqlBasicCall", this::handleSqlBasicCall);
    }


    public void handleExpression(SelectItem selectItem) {
        Optional.ofNullable(FUNCTION_MAP.get(selectItem.getSqlNode().getClass().getName()))
                .ifPresent(consumer -> consumer.accept(selectItem));
    }

    private void handleSqlIdentifier(SelectItem selectItem) {
        SqlIdentifier sqlIdentifier = (SqlIdentifier) selectItem.getSqlNode();
        String parentColumnName;
        String columnName;
        if (sqlIdentifier.names.size() == 2) {
            if (selectItem.isSubQuery()) {
                columnName = String.join(".", sqlIdentifier.names);
                parentColumnName = String.format("%s.%s", sqlIdentifier.names.get(0), sqlIdentifier.names.get(1));
            } else {
                columnName = sqlIdentifier.names.get(1);
                parentColumnName = String.format("%s.%s", sqlIdentifier.names.get(0), sqlIdentifier.names.get(1));
            }
        } else {
            if (selectItem.isSubQuery()) {
                columnName = String.format("%s.%s", selectItem.getFromTableAlias(), sqlIdentifier.names.get(0));
                parentColumnName = columnName;
            } else {
                columnName = sqlIdentifier.names.get(0);
                parentColumnName = String.format("%s.%s", selectItem.getFromTableAlias(), columnName);
            }
        }

        ColumnMapping mapping = ColumnMapping.builder().column(columnName)
                .parentColumns(Sets.newHashSet(parentColumnName)).build();
        selectItem.getColumnMappings().add(mapping);
    }

    private void handleSqlBasicCall(SelectItem selectItem) {
        SqlBasicCall sqlBasicCall = (SqlBasicCall) selectItem.getSqlNode();
        if (sqlBasicCall.getKind() == SqlKind.AS) {
            List<SqlNode> operandList = sqlBasicCall.getOperandList();
            SqlIdentifier as = (SqlIdentifier) operandList.get(1);
            String columnName = as.names.get(0);
            Set<String> parentColumnNames = new HashSet<>();
            buildParentColumnNames(operandList.get(0), parentColumnNames);
            ColumnMapping mapping = ColumnMapping.builder().column(columnName)
                    .parentColumns(parentColumnNames).build();
            selectItem.getColumnMappings().add(mapping);
        } else if (sqlBasicCall.getKind() == SqlKind.MINUS) {
            List<SqlNode> operandList = sqlBasicCall.getOperandList();
            String columnName = sqlBasicCall.toString();
            Set<String> parentColumnNames = new HashSet<>();
            operandList.forEach(node -> buildParentColumnNames(node, parentColumnNames));
            ColumnMapping mapping = ColumnMapping.builder().column(columnName)
                    .parentColumns(parentColumnNames).build();
            selectItem.getColumnMappings().add(mapping);
        }
    }

    public void buildParentColumnNames(SqlNode sqlNode, Set<String> parentColumnNames) {
        if (sqlNode instanceof SqlIdentifier) {
            parentColumnNames.add(sqlNode.toString());
        } else if (sqlNode instanceof SqlBasicCall) {
            List<SqlNode> operandList = ((SqlBasicCall) sqlNode).getOperandList();
            operandList.forEach(node -> buildParentColumnNames(node, parentColumnNames));
        } else if (sqlNode instanceof SqlCase) {
            SqlCase sqlCase = (SqlCase) sqlNode;
            sqlCase.getWhenOperands().forEach(whenOperand -> buildParentColumnNames(whenOperand, parentColumnNames));
            sqlCase.getThenOperands().forEach(thenOperand -> buildParentColumnNames(thenOperand, parentColumnNames));
            buildParentColumnNames(sqlCase.getElseOperand(), parentColumnNames);
        }

    }


}
