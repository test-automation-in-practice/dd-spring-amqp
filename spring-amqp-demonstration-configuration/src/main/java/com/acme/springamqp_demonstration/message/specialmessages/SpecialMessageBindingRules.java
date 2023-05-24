package com.acme.springamqp_demonstration.message.specialmessages;

import java.util.Map;

final class SpecialMessageBindingRules {
  static final Map<String, Object> ruleAny = Map.of(SpecialMessageProperties.HEADER_KEY_FROM, "sales", SpecialMessageProperties.HEADER_KEY_PRICING_MODEL, 1);
  static final Map<String, Object> ruleAll = Map.of(SpecialMessageProperties.HEADER_KEY_FROM, "customer", SpecialMessageProperties.HEADER_KEY_PRICING_MODEL, 2);
}
