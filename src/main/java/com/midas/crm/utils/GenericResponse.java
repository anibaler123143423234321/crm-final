package com.midas.crm.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private Integer rpta; // 1 para éxito, 0 para error
    private String msg;   // Mensaje de respuesta
    private T data;       // Datos de respuesta
}