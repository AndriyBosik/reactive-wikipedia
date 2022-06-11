package com.example.wiki.reactive.config;

import com.example.wiki.reactive.meta.Period;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPeriodConverter implements Converter<String, Period> {
  @Override
  public Period convert(String source) {
    return Period.valueOf(source.toUpperCase());
  }
}
