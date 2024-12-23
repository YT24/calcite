package com.example.calcite.handler.select;

import com.example.calcite.entity.ColumnMapping;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlNode;

import java.util.List;

public class SqlJoinHandler implements SqlHandler {

    @Override
    public void extractFromSqlNode(SqlNode sqlNode, List<ColumnMapping> columnsMapping,String parentFromTableAlias, boolean subQuery) {
        SqlJoin sqlJoin = (SqlJoin) sqlNode;
        SqlNode left = sqlJoin.getLeft();
        if (left instanceof SqlJoin) {
            //this.extractFromSqlNode(sqlNode, columnsMapping, parentFromTableAlias, subQuery);
        }

        SqlNode right = sqlJoin.getRight();
    }
}
