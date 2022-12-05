package com.xm.base.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xm.base.config.RestConsumerConfig;
import com.xm.base.config.RestProducerConfig;
import com.xm.base.constant.Constant;

/**
 * Clase que representa la ruta que expone el servicio REST
 */
@Component
public class RestConsumerRoute extends RouteBuilder {
	
	@Autowired
	private RestConsumerConfig restConfig;

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.off)
                .apiContextPath("/api-doc")
                .enableCORS(true)
                .apiProperty("cors", "true")
                .clientRequestValidation(true);

        rest(restConfig.getApiPath())
                .get()
                .to(Constant.ROUTE_INIT);

    }
}
