package com.acme.springamqp_demonstration.message;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CurrentDateTimeProvider {

  public String getCurrentDateTime() {
    return LocalDateTime.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME);
  }
}
