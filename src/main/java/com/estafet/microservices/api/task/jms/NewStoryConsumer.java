package com.estafet.microservices.api.task.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.estafet.microservices.api.task.event.MessageEventHandler;
import com.estafet.microservices.api.task.model.Story;
import com.estafet.microservices.api.task.service.TaskService;

import io.opentracing.Tracer;

@Component
public class NewStoryConsumer {

    public final static String TOPIC = "new.story.topic";

    @Autowired
    private Tracer tracer;

    @Autowired
    private TaskService taskService;

    @Autowired
    private MessageEventHandler messageEventHandler;

    @JmsListener(destination = TOPIC, containerFactory = "myFactory")
    public void onMessage(String message, @Header("message.event.interaction.reference") String reference) {
        try {
            if (messageEventHandler.isValid(TOPIC, reference)) {
                taskService.newStory(Story.fromJSON(message));
            }
        } finally {
            if (tracer.activeSpan() != null) {
                tracer.activeSpan().close();
            }
        }
    }

}
