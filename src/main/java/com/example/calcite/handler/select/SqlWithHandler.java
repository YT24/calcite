package com.example.calcite.handler.select;

import com.example.calcite.entity.ColumnMapping;
import org.apache.calcite.sql.SqlNode;

import java.util.List;

public class SqlWithHandler implements SqlHandler {
    @Override
    public void extractFromSqlNode(SqlNode sqlNode, List<ColumnMapping> columnsMapping,String parentFromTableAlias, boolean subQuery) {

    }
}
