package com.net128.app.ssl.mutual;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(-1000)
public class ActuatorWebSecurityConfigurationAdapter /*extends WebSecurityConfigurerAdapter*/ {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//            .authorizeRequests()
//                .anyRequest()
//                .permitAll();
////                .requestMatchers(EndpointRequest.to("info")).permitAll()
////                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")
////                .antMatchers("/**").hasRole("USER")
// //               .antMatchers("/**").permitAll()
// //           .and()
//  //              .httpBasic();
//    }
}

