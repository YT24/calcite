package com.example.calcite;

import com.example.calcite.entity.ColumnMapping;
import com.example.calcite.utils.CalciteUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

@SpringBootTest
class CalciteApplicationTests {

    @Test
    void contextLoads() throws Exception {

        String sql = /*"with t1 as (select * from sy_dataworks.catalog_transform_sql cts)\n" +*/
                /*"select t1.id,t1.config_id,t1.`sql`,t1.md5 from t1\n" +
                "union all\n" +*/
                "select cts.id - 1 ,cts.config_id ,cts.`sql` ,concat('2_',cts.md5) as newMd5  from sy_dataworks.catalog_transform_sql cts";

        String sql2 = "select\n" +
                "\tcts.id - 1,\n" +
                "\t(cts.id - 1) as id_,\n" +
                "\tcts.`sql` ,\n" +
                "\tsum(cts.id) as sum_ids,\n" +
                "\tconcat('123_',cts.md5,1) as new_md5,\n" +
                "\tcase cts.status when 1 then 'success' when 0 then 'not start' else 'null' end new_statue,\n" +
                "\tmax(cts.id) as max_id,\n" +
                "\tcast(cts.id as char) as str_id\n" +
                "from\n" +
                "\t(select ccc.id,ccc.`sql`,ccc.md5,status from sy_dataworks.catalog_transform_sql ccc) cts";

        String sql3 = "select\n" +
                "\tcts.id - 1,\n" +
                "\t(cts.id - 1) as id_,\n" +
                "\tcts.`sql` ,\n" +
                "\tsum(cts.id) as sum_ids,\n" +
                "\tconcat('123_',cts.md5,1) as new_md5,\n" +
                "\tcase cts.status when 1 then 'success' when 0 then 'not start' else 'null' end new_statue,\n" +
                "\tmax(cts.id) as max_id,\n" +
                "\tcast(cts.id as char) as str_id\n" +
                "from\n" +
                "\t(select ccc.id,ccc.`sql`,ccc.md5,ccc.status from (select id,`sql`,md5,status from sy_dataworks.catalog_transform_sql) ccc) cts";

        String sql4 = "select\n" +
                "\tcts.id - 1,\n" +
                "\t(cts.id - 1) as id_,\n" +
                "\tcts.`sql` ,\n" +
                "\tsum(cts.id) as sum_ids,\n" +
                "\tconcat('123_',cts.md5,1) as new_md5,\n" +
                "\tcase cts.status when 1 then 'success' when 0 then 'not start' else 'null' end new_statue,\n" +
                "\tmax(cts.id) as max_id,\n" +
                "\tcast(cts.id as char) as str_id,\n" +
                "\tcc.id as catalog_id,\n" +
                "\tcd.id as db_id\n" +
                "from\n" +
                "\t(select ccc.id,ccc.`sql`,ccc.md5,ccc.status from (select id,`sql`,md5,status from sy_dataworks.catalog_transform_sql) ccc) cts\n" +
                "inner join catalog_catalog cc on cc.id = cts.id\n" +
                "inner join (select id,name from catalog_db) cd on cd.id = cc.id";
        List<ColumnMapping> dataLine = CalciteUtils.getDataLineage(sql4);
        dataLine.forEach(System.out::println);
    }

    @Test
    void test() throws Exception {
        String sql = "SELECT * FROM person LATERAL VIEW EXPLODE(ARRAY(30, 60)) tableName AS c_age";
        List<ColumnMapping> dataLine = CalciteUtils.getDataLineage(sql);
    }

}
