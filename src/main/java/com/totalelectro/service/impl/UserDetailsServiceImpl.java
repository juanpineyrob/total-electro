package com.totalelectro.service.impl;

import com.totalelectro.model.User;
import com.totalelectro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("❌ Usuario no encontrado con email: {}", email);
                        return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                    });

            logger.info("✅ Usuario encontrado: {} {} (ID: {})", user.getFirstName(), user.getLastName(), user.getId());
            logger.info("📋 Roles del usuario (tamaño del conjunto): {}", user.getRoles().size());
            
            if (user.getRoles().isEmpty()) {
                logger.warn("⚠️ El usuario no tiene roles asignados");
            } else {
                logger.info("📋 Conjunto de roles del usuario: {}", user.getRoles());
            }
            
            user.getRoles().forEach(role -> {
                logger.info("📋 Rol encontrado: {} (ID: {})", role.getName(), role.getId());
                logger.info("📋 Detalles del rol: {}", role);
            });

            var authorities = user.getRoles().stream()
                    .map(role -> {
                        String authority = "ROLE_" + role.getName();
                        logger.info("🔑 Asignando autoridad: {} al usuario {}", authority, email);
                        return new SimpleGrantedAuthority(authority);
                    })
                    .collect(Collectors.toList());

            logger.info("🎯 Autoridades finales para {}: {}", email, authorities);
            logger.info("🎯 Tamaño de la lista de autoridades: {}", authorities.size());

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    authorities
            );
            
            logger.info("✅ UserDetails creado exitosamente para {} con authorities: {}", email, userDetails.getAuthorities());
            logger.info("✅ Tamaño de authorities en UserDetails: {}", userDetails.getAuthorities().size());
            logger.info("=== FIN DE AUTENTICACIÓN ===");
            
            return userDetails;
        } catch (Exception e) {
            logger.error("❌ Error al cargar usuario: {}", e.getMessage(), e);
            logger.error("❌ Stack trace completo:", e);
            throw e;
        }
    }
} 