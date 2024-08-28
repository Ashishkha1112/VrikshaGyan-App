
package np.edu.nast.vrikshagyanserver.config;

import np.edu.nast.vrikshagyanserver.security.JWTAuthenticationFilter;
import np.edu.nast.vrikshagyanserver.security.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityFilterConfig {

	private final UserDetailsService userDetailService;
	private final PasswordEncoder passwordEncoder;
	private final JWTAuthenticationFilter filter;
	private final JwtAuthenticationEntryPoint point;

	public SecurityFilterConfig(UserDetailsService userDetailService, PasswordEncoder passwordEncoder,
			JwtAuthenticationEntryPoint point, JWTAuthenticationFilter filter) {
		this.userDetailService = userDetailService;
		this.passwordEncoder = passwordEncoder;
		this.point = point;
		this.filter = filter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
		return security.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/user/**").hasRole("USER")
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.requestMatchers("/authenticate","/api/send/{email}","/api/verify","/api/change","/resources/static/**","/api/provinces","/api/provinces/{id}","/api/district/{id}","/api/signupUser").permitAll()
						.anyRequest().authenticated())
				.exceptionHandling(ex -> ex.authenticationEntryPoint(point))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));	
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

}

