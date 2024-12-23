package com.example.calcite.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.calcite.sql.SqlNode;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectItem {

    /**
     * SqlNode of the select item.
     */
    private SqlNode sqlNode;

    /**
     * Column mappings of the select item.
     */
    private List<ColumnMapping> columnMappings;

    /**
     * Whether the select item is a subquery.
     */
    private boolean subQuery;

    /**
     * The table alias of the select item.
     */
    private String fromTableAlias;
}
