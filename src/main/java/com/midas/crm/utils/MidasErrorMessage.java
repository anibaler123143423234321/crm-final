package com.midas.crm.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MidasErrorMessage {

    // USUARIO
    USUARIO_ALREADY_EXISTS(101, "El usuario ya existe"),
    USUARIO_NOT_FOUND(102, "El usuario no existe"),
    USUARIO_INVALID_LOGIN(103, "Correo o contraseña inválidos"),
    USUARIO_INVALID_DATA(104, "Los datos del usuario son inválidos"),
    // CLIENTE RESIDENCIAL
    CLIENTERESIDENCIAL_ALREADY_EXISTS(201, "El cliente residencial ya existe"),
    CLIENTERESIDENCIAL_NOT_FOUND(202, "El cliente residencial no existe"),
    CLIENTERESIDENCIAL_INVALID_DATA(203, "Los datos del cliente residencial son inválidos"),

    // COORDINADOR
    COORDINADOR_ALREADY_EXISTS(301, "El coordinador ya existe"),
    COORDINADOR_NOT_FOUND(302, "El coordinador no existe"),
    COORDINADOR_NOT_AVAILABLE(303, "El coordinador no está disponible"),
    ASIGNAR_COORDINADOR_FAILED(304, "No se pudo asignar el coordinador"),

    // ASESOR
    ASESOR_NOT_FOUND(401, "El asesor no existe"),
    ASESOR_ALREADY_EXISTS(402, "El asesor ya existe"),
    ASESOR_NOT_AVAILABLE(403, "El asesor seleccionado no está disponible"),
    ASIGNACION_ASESOR_FAILED(404, "No se pudo realizar la asignación del asesor"),

    // GENERAL
    ERROR_INTERNAL(500, "Ha ocurrido un error en el servidor");

    private final Integer errorCode;

    private final String errorMessage;
}