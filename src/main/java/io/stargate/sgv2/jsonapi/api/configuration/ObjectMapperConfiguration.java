package io.stargate.sgv2.jsonapi.api.configuration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.quarkus.jackson.ObjectMapperCustomizer;
import io.stargate.sgv2.jsonapi.config.DocumentLimitsConfig;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

/** Configures the {@link ObjectMapper} instance that going to be injectable and used in the app. */
public class ObjectMapperConfiguration {
  /** Replaces the CDI producer for ObjectMapper built into Quarkus. */
  @Singleton
  @Produces
  ObjectMapper objectMapper(
      DocumentLimitsConfig documentLimitsConfig, Instance<ObjectMapperCustomizer> customizers) {
    ObjectMapper mapper = createMapper(documentLimitsConfig);

    // apply all ObjectMapperCustomizer beans (incl. Quarkus)
    for (ObjectMapperCustomizer customizer : customizers) {
      customizer.customize(mapper);
    }

    return mapper;
  }

  private ObjectMapper createMapper(DocumentLimitsConfig documentLimitsConfig) {
    int maxNumLen = documentLimitsConfig.maxNumberLength();

    // Number token limit handled by lower-level parser factory, need to construct first:
    JsonFactory jsonFactory =
        JsonFactory.builder()
            .streamReadConstraints(
                StreamReadConstraints.builder().maxNumberLength(maxNumLen).build())
            .build();
    return JsonMapper.builder(jsonFactory)
        // important for retaining number accuracy!
        .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)

        // case-insensitive enums, so "before" will match to "BEFORE" in an enum
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)

        // Verify uniqueness of JSON Object properties
        .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)

        // Prevent use of Engineering Notation with trailing zeroes:
        .enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN)
        .build();
  }
}
