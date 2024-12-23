package com.example.calcite.handler.select;

import org.apache.calcite.sql.SqlKind;

import java.util.HashMap;
import java.util.Map;

/**
 * SqlHandlerFactory is a factory class to get the SqlHandler for a given SqlNode.
 */
public class SqlHandlerFactory  {

    public static final Map<SqlKind, SqlHandler> HANDLERS = new HashMap<>();

    public SqlHandlerFactory() {
        HANDLERS.put(SqlKind.SELECT, new SqlSelectHandler());
        HANDLERS.put(SqlKind.WITH, new SqlWithHandler());
        HANDLERS.put(SqlKind.UNION, new SqlUnionHandler());
    }

    public SqlHandler getHandler(SqlKind sqlKind) {
        return HANDLERS.get(sqlKind);
    }

}
