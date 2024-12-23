package com.example.calcite.context;

import com.example.calcite.entity.ColumnMapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SqlSelectMappingContext {

    private Set<ColumnMapping> mappings;

    private Set<ColumnMapping> subQueryMappings;
}
