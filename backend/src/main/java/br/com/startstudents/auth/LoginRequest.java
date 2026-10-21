package br.com.startstudents.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record LoginRequest(@NotBlank @Pattern(regexp="^[A-Za-z0-9]{8,}$")String usuario,@NotBlank @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d).{8,20}$")String senha){}