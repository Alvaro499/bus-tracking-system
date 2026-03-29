package com.bustracking.admin.infrastructure.web.error;

import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;

/*
// Solo pafa visualizrar el uso de las excepciones en el controlador, no es un código real

public class EmpresaController {

    public Object crearEmpresa(Request req) {

        try {
            useCase.execute(req);
            return "OK";

        } catch (ApplicationException ex) {
            return new ErrorResponse(
                ex.getCode().name(),
                ex.getMessage()
            );

        } catch (Exception ex) {
            return new ErrorResponse(
                "INTERNAL_ERROR",
                "Error inesperado"
            );
        }
    }
}
     */