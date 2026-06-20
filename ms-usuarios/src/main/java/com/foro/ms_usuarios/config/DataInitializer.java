package com.foro.ms_usuarios.config;

import org.springframework.beans.factory.annotation.Autowired; // 👈 AQUÍ ESTÁ LA HERRAMIENTA QUE FALTABA
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.foro.ms_usuarios.model.Usuario;
import com.foro.ms_usuarios.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired // 👈 Ahora sí funcionará sin chistar
    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔧 Abriendo a la fuerza las puertas de la pista...");
        
        String claveBlindada = passwordEncoder.encode("admin123"); 
        
        try {
            Usuario admin1 = new Usuario(null, "Javier_Drift", "javier@zerogrip.com", claveBlindada, "Admin", "JDM", "Zero Grip Society");
            usuarioRepository.save(admin1);
            System.out.println("🏁 ¡Piloto Javier_Drift inyectado y blindado con éxito!");
        } catch (Exception e) {
            System.out.println("🚦 Javier_Drift ya estaba registrado en la pista.");
        }

        try {
            Usuario admin2 = new Usuario(null, "Alejandro_V8", "alejandro@zerogrip.com", claveBlindada, "Admin", "Muscle", "Zero Grip Society");
            usuarioRepository.save(admin2);
            System.out.println("🏁 ¡Piloto Alejandro_V8 inyectado y blindado con éxito!");
        } catch (Exception e) {
            System.out.println("🚦 Alejandro_V8 ya estaba registrado en la pista.");
        }
    }
}