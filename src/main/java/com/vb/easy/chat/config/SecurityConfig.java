package com.vb.easy.chat.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	/*
	 * private static final String REALM_ACCESS_CLAIM = "realm_access"; private
	 * static final String ROLES_CLAIM = "roles";
	 * 
	 * @Bean protected SessionAuthenticationStrategy sessionAuthenticationStrategy()
	 * { return new RegisterSessionAuthenticationStrategy(new
	 * SessionRegistryImpl()); }
	 */

	@Bean
	SecurityFilterChain chain(HttpSecurity security) throws Exception {

		security.csrf(AbstractHttpConfigurer::disable);

		security.authorizeHttpRequests(req -> req.requestMatchers("/rest/**").authenticated().anyRequest().permitAll());
		security.oauth2ResourceServer(authServer -> authServer.jwt(Customizer.withDefaults()));
		security.cors(Customizer.withDefaults());
		return security.build();
	}

	@Bean
	WebMvcConfigurer corsConfigure() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				// TODO Auto-generated method stub
				registry.addMapping("/rest/**").allowedOrigins("http://localhost:5173/").allowedMethods("GET", "POST",
						"PUT", "DELETE");
			}

		};
	}

	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}
	@Bean
    JwtAuthenticationConverter jwtAuthenticationConverterForKeycloak() {
        Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = jwt -> {
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

            Object client = resourceAccess.get("easy-chat");

            LinkedTreeMap<String, List<String>> clientRoleMap = (LinkedTreeMap<String, List<String>>) client;

            List<String> clientRoles = new ArrayList<>(clientRoleMap.get("roles"));
            System.out.println("Client roles {}  " +clientRoles);
            return clientRoles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        };

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
	
	/*
	 * @Bean
	 * 
	 * @SuppressWarnings("unchecked") GrantedAuthoritiesMapper
	 * userAuthoritiesMapperForKeycloak() { return authorities -> {
	 * Set<GrantedAuthority> mappedAuthorities = new HashSet<>(); var authority =
	 * authorities.iterator().next(); boolean isOidc = authority instanceof
	 * OidcUserAuthority;
	 * 
	 * if (isOidc) { var oidcUserAuthority = (OidcUserAuthority) authority; var
	 * userInfo = oidcUserAuthority.getUserInfo();
	 * 
	 * if (userInfo.hasClaim(REALM_ACCESS_CLAIM)) { var realmAccess =
	 * userInfo.getClaimAsMap(REALM_ACCESS_CLAIM); var roles = (Collection<String>)
	 * realmAccess.get(ROLES_CLAIM);
	 * mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles)); } } else { var
	 * oauth2UserAuthority = (OAuth2UserAuthority) authority; Map<String, Object>
	 * userAttributes = oauth2UserAuthority.getAttributes();
	 * 
	 * if (userAttributes.containsKey(REALM_ACCESS_CLAIM)) { var realmAccess =
	 * (Map<String, Object>) userAttributes.get(REALM_ACCESS_CLAIM); var roles =
	 * (Collection<String>) realmAccess.get(ROLES_CLAIM);
	 * mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles)); } } return
	 * mappedAuthorities; }; }
	 * 
	 * Collection<GrantedAuthority> generateAuthoritiesFromClaim(Collection<String>
	 * roles) { return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_"
	 * + role)).collect(Collectors.toList()); }
	 */
	/*
	 * @Bean GrantedAuthoritiesMapper userAuthoritiesMapper() {
	 * SimpleAuthorityMapper mapper = new SimpleAuthorityMapper();
	 * mapper.setPrefix(""); // Remove the "ROLE_" prefix //
	 * mapper.setConvertToUpperCase(true); // Optional: Convert all roles to
	 * uppercase return mapper; }
	 */
}