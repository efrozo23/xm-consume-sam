package com.xm.base.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

/**
 * Clase que representa la ruta que expone el servicio REST
 */
@Component
public class RestConsumerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.off)
                .apiContextPath("/api-doc")
                .enableCORS(true)
                .apiProperty("cors", "true")
                .clientRequestValidation(true);

        rest("user")
                .get()
                .to("direct:getUsers");

    }
}
