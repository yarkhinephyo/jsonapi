package io.stargate.sgv2.jsonapi.service.schema.model;

import static org.assertj.core.api.Assertions.assertThat;

import io.stargate.bridge.proto.QueryOuterClass;
import io.stargate.bridge.proto.Schema;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JsonapiTableMatcherTest {

  JsonapiTableMatcher tableMatcher = new JsonapiTableMatcher();

  @Nested
  class PredicateTest {

    // NOTE: happy path asserted in the integration test

    @Test
    public void partitionColumnTypeNotMatching() {
      Schema.CqlTable table =
          Schema.CqlTable.newBuilder()
              .addPartitionKeyColumns(
                  QueryOuterClass.ColumnSpec.newBuilder()
                      .setName("key")
                      .setType(
                          QueryOuterClass.TypeSpec.newBuilder()
                              .setBasic(QueryOuterClass.TypeSpec.Basic.INT)
                              .build())
                      .build())
              .build();

      boolean result = tableMatcher.test(table);

      assertThat(result).isFalse();
    }

    @Test
    public void partitionColumnsTooMany() {
      Schema.CqlTable table =
          withCorrectPartitionColumns()
              .addPartitionKeyColumns(
                  QueryOuterClass.ColumnSpec.newBuilder().setName("key2").build())
              .build();

      boolean result = tableMatcher.test(table);

      assertThat(result).isFalse();
    }

    @Test
    public void clusteringColumnsCountNotMatching() {
      Schema.CqlTable table =
          withCorrectPartitionColumns()
              .addClusteringKeyColumns(
                  QueryOuterClass.ColumnSpec.newBuilder().setName("cluster").build())
              .build();

      boolean result = tableMatcher.test(table);

      assertThat(result).isFalse();
    }

    @Test
    public void columnsCountTooLess() {
      Schema.CqlTable.Builder tableBuilder = withCorrectPartitionColumns();
      for (int i = 0; i < 10; i++) {
        tableBuilder.addColumns(
            QueryOuterClass.ColumnSpec.newBuilder().setName("c%s".formatted(i)).build());
      }
      Schema.CqlTable table = tableBuilder.build();

      boolean result = tableMatcher.test(table);

      assertThat(result).isFalse();
    }

    @Test
    public void columnsCountTooMuch() {
      Schema.CqlTable.Builder tableBuilder = withCorrectPartitionColumns();
      for (int i = 0; i < 12; i++) {
        tableBuilder.addColumns(
            QueryOuterClass.ColumnSpec.newBuilder().setName("c%s".formatted(i)).build());
      }
      Schema.CqlTable table = tableBuilder.build();

      boolean result = tableMatcher.test(table);

      assertThat(result).isFalse();
    }

    @Test
    public void columnsNotMatching() {
      Schema.CqlTable.Builder tableBuilder = withCorrectPartitionColumns();
      for (int i = 0; i < 11; i++) {
        tableBuilder.addColumns(
            QueryOuterClass.ColumnSpec.newBuilder().setName("c%s".formatted(i)).build());
      }
      Schema.CqlTable table = tableBuilder.build();

      boolean result = tableMatcher.test(table);

      assertThat(result).isFalse();
    }

    @NotNull
    private Schema.CqlTable.Builder withCorrectPartitionColumns() {
      return Schema.CqlTable.newBuilder()
          .addPartitionKeyColumns(
              QueryOuterClass.ColumnSpec.newBuilder()
                  .setName("key")
                  .setType(
                      QueryOuterClass.TypeSpec.newBuilder()
                          .setBasic(QueryOuterClass.TypeSpec.Basic.VARCHAR)
                          .build())
                  .build());
    }

    @Test
    public void nullTable() {
      boolean result = tableMatcher.test(null);

      assertThat(result).isFalse();
    }
  }
}
