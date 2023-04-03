package com.anthill.ofhelperredditmvc.config;

import com.anthill.ofhelperredditmvc.security.jwt.JwtConfigurer;
import com.anthill.ofhelperredditmvc.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/auth/login").permitAll()
                .antMatchers("/auth/signUp").permitAll()

                .antMatchers("/v3/api-docs/**", "/swagger-ui.html",
                                        "/swagger-ui/**", "/configuration/ui",
                                        "/swagger-resources/**", "/configuration/**",
                                        "/webjars/**").permitAll()

                .antMatchers("/user/profile").hasAnyRole("USER", "LIKER", "POSTER")
                .antMatchers("/upVoteGroup/**", "/user/upVoteBalance").hasAnyRole("USER", "LIKER")
                .antMatchers("/user/**", "/useragent/**", "/redditAccount/**", "/statistic/**").hasRole("ADMIN")
                .antMatchers("/redditAccountProfile/**", "/group/**", "/proxy/**").hasAnyRole("USER", "POSTER")
                .antMatchers("/bot/**").hasRole("BOT")
                .antMatchers("/telegram/**").hasAnyRole("ADMIN", "TELEGRAM_BOT")
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }
}
