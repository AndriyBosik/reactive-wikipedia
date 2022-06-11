package com.example.wiki.reactive.config;

import com.example.wiki.reactive.meta.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
@RequiredArgsConstructor
public class WebConfig implements WebFluxConfigurer {
  private final Converter<String, Period> periodConverter;

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(periodConverter);
  }
}
