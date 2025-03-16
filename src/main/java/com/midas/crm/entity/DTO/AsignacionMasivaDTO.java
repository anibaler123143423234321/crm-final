package com.midas.crm.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionMasivaDTO {
    private Long coordinadorId;
    private List<Long> asesorIds;
}