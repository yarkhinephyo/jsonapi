package io.stargate.sgv2.jsonapi.service.operation.model.impl;

import io.smallrye.mutiny.Uni;
import io.stargate.bridge.proto.QueryOuterClass;
import io.stargate.sgv2.jsonapi.api.model.command.CommandResult;
import io.stargate.sgv2.jsonapi.service.bridge.executor.QueryExecutor;
import io.stargate.sgv2.jsonapi.service.operation.model.Operation;
import java.util.function.Supplier;

/**
 * Operation that creates a new Cassandra keyspace that serves as a namespace for the JSON API.
 *
 * @param name Name of the namespace to create.
 * @param replicationMap A replication json, see
 *     https://docs.datastax.com/en/cql-oss/3.3/cql/cql_reference/cqlCreateKeyspace.html#Table2.Replicationstrategyclassandfactorsettings.
 */
public record CreateNamespaceOperation(String name, String replicationMap) implements Operation {

  // simple pattern for the cql
  private static final String CREATE_KEYSPACE_CQL =
      "CREATE KEYSPACE IF NOT EXISTS \"%s\" WITH REPLICATION = %s;";

  /** {@inheritDoc} */
  @Override
  public Uni<Supplier<CommandResult>> execute(QueryExecutor queryExecutor) {
    QueryOuterClass.Query query =
        QueryOuterClass.Query.newBuilder()
            .setCql(String.format(CREATE_KEYSPACE_CQL, name, replicationMap))
            .build();

    // execute
    return queryExecutor
        .executeSchemaChange(query)

        // if we have a result always respond positively
        .map(any -> new SchemaChangeResult(true));
  }
}
