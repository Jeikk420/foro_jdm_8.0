package foro.ms_reacciones.repository;

import foro.ms_reacciones.model.Reaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReaccionRepository extends JpaRepository<Reaccion, Long> {}