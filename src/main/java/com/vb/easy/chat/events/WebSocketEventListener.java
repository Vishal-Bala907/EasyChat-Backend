package com.vb.easy.chat.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class WebSocketEventListener {
	public static final Logger LOGGER  = LoggerFactory.getLogger(WebSocketEventListener.class);
	
	// Listen for the event connected event
	/*
	 *  Published shortly after a SessionConnectEvent when the broker has sent a STOMP 
	 *  CONNECTED frame in response to the CONNECT. At this point, the STOMP session can be
	 *  considered fully established.
	 * */
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent connectedEvent) {
		//https://docs.spring.io/spring-framework/reference/web/websocket/stomp/handle-annotations.html#supported-method-arguments
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(connectedEvent.getMessage());
		  // Get the "userName" header from the nativeHeaders map
		LOGGER.info("Some user connected..."+accessor);
	}
}
