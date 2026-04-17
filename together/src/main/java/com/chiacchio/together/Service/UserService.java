package com.chiacchio.together.Service;

import com.chiacchio.together.Model.Usuario;
import com.chiacchio.together.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class UserService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(Usuario user) {

        if (usuarioRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (usuarioRepository.existsByNroDocumento(user.getNroDocumento())) {
            throw new RuntimeException("El documento ya está registrado");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(false);

        try {
            return usuarioRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Datos duplicados");
        }
    }
    public Usuario save(Usuario user) {
        return usuarioRepository.save(user);
    }
}
