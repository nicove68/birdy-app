package com.nicodev.birdyapp.configuration;


import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.nicodev.birdyapp.exception.RestTemplateResponseErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableAsync
@EnableScheduling
@EnableMongoRepositories(basePackages = "com.nicodev.birdyapp.repository")
public class AppConfiguration implements WebMvcConfigurer {

  @Value("${spring.data.mongodb.uri}")
  private String mongoUri;

  @Bean
  public MongoDbFactory mongoDbFactory() {
    return new SimpleMongoClientDbFactory(this.mongoUri);
  }

  @Bean
  public MongoTemplate mongoTemplate() {
    return new MongoTemplate(mongoDbFactory());
  }

  @Bean @Primary
  public ObjectMapper objectMapperSnakeCase() {
    return new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(Include.NON_NULL)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .registerModule(new Jdk8Module());
  }

  @Bean
  public ObjectMapper objectMapperCamelCase() {
    return new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(Include.NON_NULL)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .registerModule(new Jdk8Module());
  }

  private MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter(ObjectMapper objectMapper) {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(objectMapper);
    return converter;
  }

  @Bean
  public RestTemplate restTemplateGoogleApi() {
    return new RestTemplateBuilder()
            .errorHandler(new RestTemplateResponseErrorHandler())
            .additionalMessageConverters(mappingJacksonHttpMessageConverter(objectMapperSnakeCase()))
            .additionalInterceptors(new CustomClientHttpRequestInterceptor())
            .build();
  }

  @Bean
  public RestTemplate restTemplateGooglePeopleApi() {
    return new RestTemplateBuilder()
            .errorHandler(new RestTemplateResponseErrorHandler())
            .additionalMessageConverters(mappingJacksonHttpMessageConverter(objectMapperCamelCase()))
            .additionalInterceptors(new CustomClientHttpRequestInterceptor())
            .build();
  }
}
