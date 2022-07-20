package br.com.chat.controller;

import br.com.chat.model.ChatMessage;
import br.com.chat.model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListen {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);

    @Autowired
    private SimpMessageSendingOperations sendingOperations;

    @EventListener //catch the event when the user connect
    public void handleWebSocketConnectListener(final SessionConnectedEvent event){
        LOGGER.info("We have a little connection");
    }

    @EventListener //catch the event when the user disconnect
    public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event){
        LOGGER.info("User disconnected");

        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        final String username = (String) headerAccessor.getSessionAttributes().get("username");

        final ChatMessage chatMessage = ChatMessage.builder()
                .type(MessageType.DISCONNECT)
                .sender(username)
                .build();

        sendingOperations.convertAndSend("/topic/public", chatMessage);
    }
}
