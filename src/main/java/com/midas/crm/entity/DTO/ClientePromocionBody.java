package com.midas.crm.entity.DTO;

import com.midas.crm.entity.ClienteResidencial;
import lombok.Data;

@Data
public class ClientePromocionBody {
    private ClienteResidencial clienteResidencial;
    private Long usuarioId;
}
