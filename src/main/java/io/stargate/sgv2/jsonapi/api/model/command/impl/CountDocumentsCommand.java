package io.stargate.sgv2.jsonapi.api.model.command.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.stargate.sgv2.jsonapi.api.model.command.Filterable;
import io.stargate.sgv2.jsonapi.api.model.command.NoOptionsCommand;
import io.stargate.sgv2.jsonapi.api.model.command.ReadCommand;
import io.stargate.sgv2.jsonapi.api.model.command.clause.filter.FilterClause;
import jakarta.validation.Valid;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    description =
        "Command that returns count of documents in a collection based on the collection.")
@JsonTypeName("countDocuments")
public record CountDocumentsCommand(@Valid @JsonProperty("filter") FilterClause filterClause)
    implements ReadCommand, NoOptionsCommand, Filterable {}
