SqlNode SqlLateralView() :
{
    SqlParserPos pos;
    String table;        // 表名
    SqlCall tableFunction;    // EXPLODE 函数
    String alias; // 别名
}
{
    pos = getPos()
    <LATERAL> <VIEW>
    tableFunction = SqlFunctionCall()
    <AS>
    alias = <IDENTIFIER>
    {
        return new SqlLateralView(pos.plus(getPos()), tableFunction, alias);
    }
}