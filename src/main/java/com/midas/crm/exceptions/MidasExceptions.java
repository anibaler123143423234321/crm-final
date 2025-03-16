package com.midas.crm.exceptions;


import com.midas.crm.utils.MidasErrorMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MidasExceptions extends RuntimeException {
    private final MidasErrorMessage errorMessage;

    private final Integer errorCode;

    public MidasExceptions(final MidasErrorMessage errorMessage) {
        super(errorMessage.getErrorMessage());
        this.errorMessage = errorMessage;
        this.errorCode = errorMessage.getErrorCode();
    }
}
