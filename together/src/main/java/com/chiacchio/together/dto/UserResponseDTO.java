package com.chiacchio.together.dto;

import com.chiacchio.together.Model.Usuario;

import java.time.LocalDate;

public class UserResponseDTO {
    private String nombre;
    private String segundoNombre;
    private String apellido;
    private String email;
    private String nroDocumento;
    private String provincia;
    private LocalDate fechaNacimiento;
    private String profileImageUrl;

    public UserResponseDTO(Usuario user) {
        this(user, null);
    }

    public UserResponseDTO(Usuario user, String profileImageUrl) {
        this.nombre = user.getNombre();
        this.segundoNombre = user.getSegundoNombre();
        this.apellido = user.getApellido();
        this.email = user.getEmail();
        this.nroDocumento = user.getNroDocumento();
        this.provincia = user.getProvincia().toString(); // Convertimos el Enum a String
        this.fechaNacimiento = user.getFechaNacimiento();
        this.profileImageUrl = profileImageUrl;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
