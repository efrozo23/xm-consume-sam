package com.xm.base.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class LogRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        from("direct:logRequestMS")
                .log(LoggingLevel.INFO, " -->> Request MS -->> ${body}");

        from("direct:logResponseMS")
                .log(LoggingLevel.INFO, " Response MS  ${body}");

        from("direct:logRequestExternalService")
        		.log("estado=SENDING_TO_SAM | variable=${exchangeProperty.variable} | fecha_proceso= ${exchangeProperty.fecha} | fileId=${exchangeProperty.FILE_ID} | message=Sending To SAM ")
                .log("Request External Service -->> ${body}");

        from("direct:logResponseExternalService")
        		.log("estado=RESPONSE_SAM_CXF | variable=${exchangeProperty.variable} | fecha_proceso= ${exchangeProperty.fecha} | fileId=${exchangeProperty.FILE_ID} | message=Response SAM CamelCxfMessage: ${headers.CamelHttpResponseCode}")
                .log("Response External Service <<-- ${body} ");

    }
}
