package com.vb.easy.chat.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSocketMessageBroker
public class MessageConfigurer implements WebSocketMessageBrokerConfigurer {
/* DOCS (github : https://github.com/Vishal-Bala907/Spring-Boot-Advance-Topics/blob/main/WebSocket2/src/main/java/com/vb/websoc/config/WebSocketConfig.java)
 * 
 * 
 *
 */
	 @Override
	    public void configureMessageBroker(MessageBrokerRegistry config) {
	        config.enableSimpleBroker("/user");  // Messages will be sent to the "/topic" end point
	        config.setApplicationDestinationPrefixes("/app");  // Message mappings start with "/app"
	        config.setUserDestinationPrefix("/user");
	    }

	    @Override
	    public void registerStompEndpoints(StompEndpointRegistry registry) {
	        registry.addEndpoint("/chat").setAllowedOrigins("http://localhost:5173").withSockJS(); // SockJS fallback
	    }

		@Override
		public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
			// TODO Auto-generated method stub
			DefaultContentTypeResolver typeResolver = new DefaultContentTypeResolver();
			typeResolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
			
			MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
			converter.setObjectMapper(new ObjectMapper());
			converter.setContentTypeResolver(typeResolver);
			
			messageConverters.add(converter);
			
			
			return false;
		}
	    
	    
}
