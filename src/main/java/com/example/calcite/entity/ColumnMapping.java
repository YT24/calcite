package com.example.calcite.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ColumnMapping {

    /**
     * 列名
     */
    private String column;

    /**
     * 父列名
     */
    private Set<String> parentColumns;
}
