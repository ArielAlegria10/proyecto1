package com.utc.proyecto1.repository;

import com.utc.proyecto1.entity.Supervisa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupervisaRepository extends JpaRepository<Supervisa, Long> {
    
    // ============= BÚSQUEDAS POR CÓDIGOS =============
    
    // Buscar por código de cargo sup (campo directo)
    List<Supervisa> findByCodigoCargoSup(Long codigoCargoSup);
    
    // Buscar por cargo (relación) - usando la clave foránea
    List<Supervisa> findByCargoCodigoCar(Long codigoCar);
    
    // ============= CONSULTAS CON JOIN FETCH (evita N+1) =============
    
    // Query personalizada para obtener supervisiones con datos de cargo
    @Query("SELECT s FROM Supervisa s LEFT JOIN FETCH s.cargo c WHERE c.codigoCar = :codigoCargo")
    List<Supervisa> findWithCargoByCargoId(@Param("codigoCargo") Long codigoCargo);
    
    // Obtener todas las supervisiones con sus cargos (evita N+1 en listados)
    @Query("SELECT s FROM Supervisa s LEFT JOIN FETCH s.cargo")
    List<Supervisa> findAllWithCargo();
    
    // ============= ORDENAMIENTO =============
    
    // Todas las supervisiones ordenadas por fecha de creación descendente
    List<Supervisa> findAllByOrderByFechaCreadoSupDesc();
    
    // ============= BÚSQUEDAS POR FECHA =============
    
    // Supervisiones en un rango de fechas
    List<Supervisa> findByFechaCreadoSupBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Supervisiones del día actual
    @Query("SELECT s FROM Supervisa s WHERE DATE(s.fechaCreadoSup) = CURRENT_DATE")
    List<Supervisa> findToday();
    
    // ============= CONTEO =============
    
    // Contar supervisiones por cargo
    Long countByCargoCodigoCar(Long codigoCar);
    
    // ============= ELIMINACIÓN =============
    
    // Eliminar supervisiones por cargo
    void deleteByCargoCodigoCar(Long codigoCar);
}