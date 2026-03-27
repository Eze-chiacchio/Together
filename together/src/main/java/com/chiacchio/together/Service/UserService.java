package com.chiacchio.together.Service;

import com.chiacchio.together.Model.Usuario;
import com.chiacchio.together.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(Usuario user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return usuarioRepository.save(user);
    }
    public Usuario save(Usuario user) {
        return usuarioRepository.save(user);
    }
}