package io.stargate.sgv2.jsonapi.api.model.command.impl;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.stargate.sgv2.jsonapi.api.model.command.NamespaceCommand;
import jakarta.validation.constraints.*;
import javax.annotation.Nullable;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Command that creates a collection.")
@JsonTypeName("createCollection")
public record CreateCollectionCommand(
    @NotNull
        @Size(min = 1, max = 48)
        @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9_]*")
        @Schema(description = "Name of the collection")
        String name,
    @JsonInclude(JsonInclude.Include.NON_NULL)
        @Nullable
        @Schema(
            description = "Configuration for the collection",
            type = SchemaType.OBJECT,
            implementation = Options.class)
        Options options)
    implements NamespaceCommand {
  public record Options(

      // limit of returned documents
      @Schema(
              description = "Vector search index configuration for the collection",
              type = SchemaType.OBJECT,
              implementation = VectorSearchConfig.class)
          VectorSearchConfig vector,
      @JsonInclude(JsonInclude.Include.NON_NULL)
          @Nullable
          @Schema(
              description = "Embedding api configuration to support `$vectorize`",
              type = SchemaType.OBJECT,
              implementation = VectorSearchConfig.class)
          VectorizeConfig vectorize) {

    public record VectorSearchConfig(
        @Positive(message = "dimension should be greater than `0`")
            @Schema(
                description = "Dimension of the vector field",
                type = SchemaType.INTEGER,
                implementation = Integer.class)
            @JsonProperty("dimension")
            @JsonAlias("size") // old name
            Integer dimension,
        @Nullable
            @Pattern(
                regexp = "(dot_product|cosine|euclidean)",
                message = "function name can only be 'dot_product', 'cosine' or 'euclidean'")
            @Schema(
                description =
                    "Similarity function algorithm that needs to be used for vector search",
                defaultValue = "cosine",
                type = SchemaType.STRING,
                implementation = String.class)
            @JsonProperty("metric")
            @JsonAlias("function") // old name
            String metric) {
      public VectorSearchConfig(Integer dimension, String metric) {
        this.dimension = dimension;
        this.metric = metric == null ? "cosine" : metric;
      }
    }

    public record VectorizeConfig(
        @NotNull
            @Schema(
                description = "Registered Embedding service name",
                type = SchemaType.STRING,
                implementation = String.class)
            String service,
        @NotNull
            @Schema(
                description = "Model options for the embedding service call",
                type = SchemaType.OBJECT,
                implementation = VectorizeOptions.class)
            VectorizeOptions options) {
      public record VectorizeOptions(
          @NotNull
              @Schema(
                  description = "Model name used for embedding data",
                  type = SchemaType.STRING,
                  implementation = String.class)
              String modelName) {}
    }
  }
}
