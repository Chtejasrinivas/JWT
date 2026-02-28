package com.example.jwt.config;

import com.example.jwt.filters.JwtFilter;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
/**
 * This class is created to tell spring security that you don't do the security related stuff let me
 * take care of it. Like overriding the spring security default configuration and create your own configuration.
 */
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;


    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) {

        // we are disabling the CSRF now for the POST requests we can send the request without the CSRF token
        // now create the customer order without the CSRF token and you will see the request is successful
        security.csrf(csrf -> csrf.disable());

        // here we are telling spring that all the request should be authenticated
        security.authorizeHttpRequests(
            requests -> requests.requestMatchers("/user/login", "/user/register", "/user/refresh-token")
                .permitAll()
                .anyRequest().authenticated());

        // here we are telling spring to use the basic authentication for the authentication process
//        security.httpBasic(Customizer.withDefaults());

        // since we are doing the JWT based authentication we don't need the basic authentication so we are disabling it

        /**
         * here we are telling spring to use the stateless session management policy,
         * which means that spring will not create a session for the user and will not store any information about the user in the session.
         * This is useful for REST ful APIs where we want to be stateless and not store any information about the user in the session.
         * once you mention stateless you can see the session id is being changes for every request to test this
         * open the home page and do refresh everytime you will get a new session id.
         **/
        security.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // here we are telling spring to use the jwt filter before the username password authentication filter
        security.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return security.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);


//        This is a default password encoder which does not do any encoding and stores the password in plain text,
//        which is not recommended for production use.
//        authenticationProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());

        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

}
