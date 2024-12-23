package com.example.calcite.handler.select;

import com.example.calcite.entity.ColumnMapping;
import org.apache.calcite.sql.SqlNode;

import java.util.List;

public interface SqlHandler {

    void extractFromSqlNode(SqlNode sqlNode, List<ColumnMapping> columnsMapping,String parentFromTableAlias, boolean subQuery);

}
