package ms_hilos.ms_hilos.repository;

import ms_hilos.ms_hilos.model.Hilo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HiloRepository extends JpaRepository<Hilo, Long> {
}
