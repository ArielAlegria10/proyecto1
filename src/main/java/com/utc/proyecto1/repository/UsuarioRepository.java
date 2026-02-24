package com.utc.proyecto1.repository;

import com.utc.proyecto1.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;  // ← NUEVO IMPORT

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUsername(String username);
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // ← NUEVO MÉTODO: Buscar usuarios por rol
    List<Usuario> findByRol(String rol);
}