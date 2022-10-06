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
                .log("Request External Service -->> ${body}");

        from("direct:logResponseExternalService")
                .log("Response External Service <<-- ${body} ");

    }
}
