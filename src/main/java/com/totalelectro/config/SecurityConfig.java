package com.totalelectro.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class  SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configurando SecurityFilterChain");
        
        // Detectar si el perfil activo es 'dev' para permitir H2-console y deshabilitar frameOptions solo en desarrollo
        boolean isDev = java.util.Arrays.asList(org.springframework.core.env.AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME)
                .contains("dev");

        if (isDev) {
            http.headers(headers -> headers.frameOptions().disable());
            http.authorizeHttpRequests(auth -> auth.requestMatchers("/h2-console/**").permitAll());
        }
        // Habilitar CSRF (por defecto está habilitado, así que no lo deshabilitamos)
        // http.csrf(csrf -> csrf.disable()); // Eliminado para mayor seguridad

        http
            .authorizeHttpRequests(auth -> {
                logger.info("Configurando autorizaciones HTTP");
                auth
                    // Rutas públicas (sin autenticación)
                    .requestMatchers(
                        new AntPathRequestMatcher("/"),
                        new AntPathRequestMatcher("/product/**"),  // Todas las rutas de productos
                        new AntPathRequestMatcher("/register"),
                        new AntPathRequestMatcher("/login"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**"),
                        new AntPathRequestMatcher("/images/**"),
                        new AntPathRequestMatcher("/favicon.ico")
                    ).permitAll()
                    // Rutas que requieren autenticación
                    .requestMatchers(
                        new AntPathRequestMatcher("/cart/**"),
                        new AntPathRequestMatcher("/profile/**"),
                        new AntPathRequestMatcher("/orders/**")
                    ).authenticated()
                    // Rutas de administración
                    .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                    // Cualquier otra ruta requiere autenticación
                    .anyRequest().authenticated();

                // Agregar logging para depuración
                logger.info("Rutas públicas configuradas:");
                logger.info("- /product/** está permitida");
                logger.info("Autorizaciones HTTP configuradas");
            })
            .formLogin(form -> {
                logger.info("Configurando login form");
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error=true")
                    .permitAll();
                logger.info("Login form configurado");
            })
            .logout(logout -> {
                logger.info("Configurando logout");
                logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .permitAll();
                logger.info("Logout configurado");
            })
            .sessionManagement(session -> {
                logger.info("Configurando gestión de sesión");
                session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(1)
                    .expiredUrl("/login?expired=true")
                    .maxSessionsPreventsLogin(false)
                    .and()
                    .invalidSessionUrl("/login?invalid=true")
                    .sessionFixation().newSession()
                    .enableSessionUrlRewriting(false);
                logger.info("Gestión de sesión configurada");
            })
            .authenticationProvider(authenticationProvider());
        
        // ADVERTENCIA: Revisa los logs en producción para evitar fuga de información sensible
        // ADVERTENCIA: Revisa las rutas públicas para evitar exposición de datos sensibles
        logger.info("SecurityFilterChain configurado completamente");
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 