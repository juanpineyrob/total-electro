package com.totalelectro.service.impl;

import com.totalelectro.model.User;
import com.totalelectro.projection.UserDetailsProjection;
import com.totalelectro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("=== INICIO DE AUTENTICACIÓN ===");
        logger.info("Intentando cargar usuario con email: {}", email);
        
        try {
            // Verificar si el email está vacío
            if (email == null || email.trim().isEmpty()) {
                logger.error("❌ Email proporcionado está vacío");
                throw new UsernameNotFoundException("El email no puede estar vacío");
            }

            // Buscar el usuario y sus roles usando la consulta nativa
            List<UserDetailsProjection> userDetails = userRepository.searchUserAndRolesByEmail(email);
            
            if (userDetails.isEmpty()) {
                logger.error("❌ Usuario no encontrado con email: {}", email);
                throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
            }

            logger.info("✅ Usuario encontrado: {}", email);
            
            // Debug detallado de roles
            logger.info("=== DEBUG DE ROLES ===");
            logger.info("📋 Número total de roles: {}", userDetails.size());
            
            // Crear autoridades
            var authorities = userDetails.stream()
                    .map(projection -> {
                        String authority = "ROLE_" + projection.getAuthority();
                        logger.info("🔑 Creando autoridad: {} para el usuario {}", authority, email);
                        return new SimpleGrantedAuthority(authority);
                    })
                    .collect(Collectors.toList());

            logger.info("📋 Lista final de autoridades:");
            authorities.forEach(auth -> logger.info("  - {}", auth.getAuthority()));
            logger.info("📋 Total de autoridades: {}", authorities.size());

            // Crear UserDetails usando el primer resultado (todos tienen la misma contraseña)
            UserDetails userDetailsObj = new org.springframework.security.core.userdetails.User(
                    userDetails.get(0).getUsername(),
                    userDetails.get(0).getPassword(),
                    authorities
            );
            
            logger.info("✅ UserDetails creado exitosamente");
            logger.info("  - Email: {}", userDetailsObj.getUsername());
            logger.info("  - Contraseña: [PROTEGIDA]");
            logger.info("  - Cuenta no expirada: {}", userDetailsObj.isAccountNonExpired());
            logger.info("  - Cuenta no bloqueada: {}", userDetailsObj.isAccountNonLocked());
            logger.info("  - Credenciales no expiradas: {}", userDetailsObj.isCredentialsNonExpired());
            logger.info("  - Cuenta habilitada: {}", userDetailsObj.isEnabled());
            logger.info("  - Autoridades finales: {}", userDetailsObj.getAuthorities());
            
            logger.info("=== FIN DE AUTENTICACIÓN ===");
            return userDetailsObj;
            
        } catch (Exception e) {
            logger.error("❌ Error durante la autenticación: {}", e.getMessage());
            logger.error("❌ Tipo de excepción: {}", e.getClass().getName());
            logger.error("❌ Stack trace completo:", e);
            throw e;
        }
    }
} 