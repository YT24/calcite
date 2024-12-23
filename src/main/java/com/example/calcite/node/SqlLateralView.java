package com.example.calcite.node;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;

import java.util.Arrays;
import java.util.List;

public class SqlLateralView extends SqlCall {
    private final SqlCall tableFunction;
    private final SqlIdentifier alias;

    public SqlLateralView(SqlParserPos pos, SqlCall tableFunction, SqlIdentifier alias) {
        super(pos);
        this.tableFunction = tableFunction;
        this.alias = alias;
    }

    @Override
    public SqlOperator getOperator() {
        return SqlStdOperatorTable.LATERAL;
    }

    @Override
    public List<SqlNode> getOperandList() {
        return Arrays.asList(tableFunction, alias);
    }
}
