package com.xm.base.routes;

import com.fasterxml.jackson.core.JsonParseException;
import com.xm.base.constant.Constant;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class TransformationRoute extends RouteBuilder {

        @Override
        public void configure() throws Exception {

                onException(JsonParseException.class).handled(true)
                                .log(LoggingLevel.ERROR, "Error General en la ruta  ${routeId}")
                                .log(LoggingLevel.ERROR,
                                                "ExceptionClass: ${exchangeProperty.CamelExceptionCaught.class}")
                                .log(LoggingLevel.ERROR,
                                                "ExceptionClassName: ${exchangeProperty.CamelExceptionCaught.class.name}")
                                .log(LoggingLevel.ERROR, "StackTrace: ${exception.stacktrace}")
                                .setBody(simple("${exception.message}"))
                                .setHeader("CamelHttpResponseCode", simple("400"))
                                .end();

                onException(Exception.class)
                                .handled(true)
                                .log(LoggingLevel.ERROR, "${headers.stackHeader}; Error general: ${routeId}")
                                .log(LoggingLevel.ERROR,
                                                "${headers.stackHeader}; ExceptionClassName: ${exchangeProperty.CamelExceptionCaught.class.name}")
                                .log(LoggingLevel.ERROR, "${headers.stackHeader};StackTrace: ${exception.stacktrace}")
                                .setHeader("CamelHttpResponseCode", simple("500"))
                                .setBody(simple("${exception.message}"))
                                .end();

                from(Constant.ROUTE_INIT)
                                .routeId("ROUTE_INIT")
                                .to("direct:logRequestMS")
                                .to("direct:call-external-ws")
                                .to("direct:logResponseMS")
                                .end();

        }
}
