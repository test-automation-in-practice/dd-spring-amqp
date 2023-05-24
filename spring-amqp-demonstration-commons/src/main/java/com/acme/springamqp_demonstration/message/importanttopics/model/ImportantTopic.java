package com.acme.springamqp_demonstration.message.importanttopics.model;

import java.io.Serializable;

public record ImportantTopic(String messageContent, String currentDateTime) implements Serializable {
}
