package edu.iastate.coms309.cyschedulebackend.configuration;

import edu.iastate.coms309.cyschedulebackend.Service.AccountService;
import edu.iastate.coms309.cyschedulebackend.Service.UserTokenService;
import edu.iastate.coms309.cyschedulebackend.security.filter.TokenFilter;
import edu.iastate.coms309.cyschedulebackend.security.provider.TokenAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Order(1)
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    final AccountService accountService;

    final PasswordEncoder passwordEncoder;

    final UserTokenService userTokenService;

    final TokenAuthenticationProvider tokenAuthenticationProvider;

    public SpringSecurityConfiguration(AccountService accountService,
                                       PasswordEncoder passwordEncoder,
                                       UserTokenService userTokenService,
                                       TokenAuthenticationProvider tokenAuthenticationProvider) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.userTokenService = userTokenService;
        this.tokenAuthenticationProvider = tokenAuthenticationProvider;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        //Provider for jwtToken Authention
        authenticationManagerBuilder.authenticationProvider(tokenAuthenticationProvider);

        //Provider for session Login
        authenticationManagerBuilder
                .userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web){
        //ignoring static objects
        web.ignoring()
                .antMatchers("/error")
                .antMatchers("/javadoc/**")
                .antMatchers("/index.html")
                .antMatchers("/websocket/**")
                .antMatchers("/api/v1/auth/**")
                .antMatchers("/v2/api-docs", "/swagger-ui*.*");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated();

        //disable csrf protection for post return 403
        http
                .csrf().disable();

        //RememberMe configuration
        http
                .rememberMe().userDetailsService(userDetailsService());

        //logout configuration
        http
                .logout();

        //login page configuration
        http
                .formLogin();

         //Add our custom JWT security filter
        TokenFilter tokenFilter = new TokenFilter(authenticationManagerBean());
        http.addFilterBefore(tokenFilter,UsernamePasswordAuthenticationFilter.class);
    }
}
