package com.vb.easy.chat.events;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventListnerKeycloak implements EventListenerProvider {
	
	private final Logger LOGGER = LoggerFactory.getLogger(EventListnerKeycloak.class);

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEvent(Event event) {
		if(event.getType() == EventType.REGISTER) {
			LOGGER.info("USER REGISTERD {} " + event);
		}
		
	}

	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {
		// TODO Auto-generated method stub
		
	}

}
