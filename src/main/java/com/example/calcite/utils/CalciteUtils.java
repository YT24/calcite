package com.example.calcite.utils;

import com.example.calcite.entity.ColumnMapping;
import com.example.calcite.handler.item.SelectItemHandler;
import com.example.calcite.handler.select.SqlHandlerFactory;
import org.apache.calcite.config.Lex;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CalciteUtils {

    public static final SqlHandlerFactory sqlHandlerFactory = new SqlHandlerFactory();

    /**
     * 获取 SQL 语句的数据血缘信息
     *
     * @param sql SQL 语句
     * @return 数据血缘信息
     * @throws Exception 异常
     */
    public static List<ColumnMapping> getDataLineage(String sql) throws Exception {
        SqlNode sqlNode = parseSql(sql);
        return extractFromSqlNode(sqlNode, false);

    }

    /**
     * 从 SQL 节点中提取数据血缘信息
     *
     * @param sqlNode  SQL 节点
     * @param subQuery 是否为子查询
     */
    public static List<ColumnMapping> extractFromSqlNode(SqlNode sqlNode, boolean subQuery) {
        List<ColumnMapping> columnsMapping = new ArrayList<>();
        sqlHandlerFactory.getHandler(sqlNode.getKind()).extractFromSqlNode(sqlNode, columnsMapping, null,subQuery);
        return columnsMapping;
    }


    /**
     * 解析 SQL 语句
     *
     * @param sql SQL 语句
     * @return SqlNode
     * @throws Exception 异常
     */
    public static SqlNode parseSql(String sql) throws Exception {
        // 创建 Schema
        Connection connection = DriverManager.getConnection("jdbc:calcite:");
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        FrameworkConfig frameworkConfig = Frameworks.newConfigBuilder()
                .parserConfig(SqlParser.configBuilder().setLex(Lex.MYSQL).build())
                .defaultSchema(rootSchema) // 设置根 Schema
                .build();
        Planner planner = Frameworks.getPlanner(frameworkConfig);
        return planner.parse(sql);
    }
}
