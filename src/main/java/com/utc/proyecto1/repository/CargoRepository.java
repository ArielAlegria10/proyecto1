package com.utc.proyecto1.repository;

import com.utc.proyecto1.entity.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {
    // No necesitas implementar findById, ya viene incluido en JpaRepository
    
    // Puedes agregar métodos personalizados si los necesitas
    Optional<Cargo> findByNombreCargo(String nombreCargo);
}