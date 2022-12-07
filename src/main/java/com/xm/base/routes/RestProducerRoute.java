package com.xm.base.routes;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.base.xm.process.BuildResponse;
import com.xm.base.config.RestProducerConfig;
import com.xm.base.constant.Constant;

@Component
public class RestProducerRoute extends RouteBuilder {

    @Autowired
    private RestProducerConfig restProducerConfig;

    @Override
    public void configure() throws Exception {


        onException(ConnectException.class )
                .handled(true)
                .log(LoggingLevel.ERROR, "${headers}; Error Conexión con el endpoint externo: ${routeId}")
                .log(LoggingLevel.ERROR,  "${headers.stackHeader}; ExceptionClassName: ${exchangeProperty.CamelExceptionCaught.class.name}")
                .log(LoggingLevel.ERROR, "${headers.stackHeader};StackTrace: ${exception.stacktrace}")
                .setBody(simple("Error de Comunicacion "))
                .setHeader("CamelHttpResponseCode", simple("500"))
                .wireTap("direct:send-amq-exception")
                .process(new BuildResponse())
                .end();

        onException(SocketTimeoutException.class )
                .handled(true)
                .log(LoggingLevel.ERROR, "${headers}; Error TimeOut con el endpoint externo: ${routeId}")
                .log(LoggingLevel.ERROR,  "${headers.stackHeader}; ExceptionClassName: ${exchangeProperty.CamelExceptionCaught.class.name}")
                .log(LoggingLevel.ERROR, "${headers.stackHeader};StackTrace: ${exception.stacktrace}")
                .setBody(simple("Error de Comunicacion "))
                .setHeader("CamelHttpResponseCode", simple("500"))
                .wireTap("direct:send-amq-exception")
                .process(new BuildResponse())
                .end();

        onException(HttpOperationFailedException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${headers}; Error arrojado por el enpoint externo: ${routeId}")
                .log(LoggingLevel.ERROR,  "${headers.stackHeader}; ExceptionClassName: ${exchangeProperty.CamelExceptionCaught.class.name}")
                .log(LoggingLevel.ERROR, "${headers.stackHeader};StackTrace: ${exception.stacktrace}")
                .setBody(simple("${exception.responseBody}"))
                .setHeader("CamelHttpResponseCode", simple("${exception.statusCode}"))
                .wireTap("direct:send-amq-exception")
                .process(new BuildResponse())
                .end();

        onException(UnknownHostException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${headers}; Error arrojado por el enpoint externo: ${routeId}")
                .log(LoggingLevel.ERROR,  "${headers.stackHeader}; ExceptionClassName: ${exchangeProperty.CamelExceptionCaught.class.name}")
                .log(LoggingLevel.ERROR, "${headers.stackHeader};StackTrace: ${exception.stacktrace}")
                .setBody(simple("Host no reconocido"))
                .setHeader("CamelHttpResponseCode", simple("500"))
                .wireTap("direct:send-amq-exception")
                .process(new BuildResponse())
                .end();

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${headers}; Error general en el consumo del enpoint externo: ${routeId}")
                .log(LoggingLevel.ERROR,  "${headers.stackHeader}; ExceptionClassName: ${exchangeProperty.CamelExceptionCaught.class.name}")
                .log(LoggingLevel.ERROR, "${headers.stackHeader};StackTrace: ${exception.stacktrace}")
                .setBody(simple("Error Inesperado"))
                .setHeader("CamelHttpResponseCode", simple("500"))
                .wireTap("direct:send-amq-exception")
                .process(new BuildResponse())
                .end();

        from("direct:call-external-ws")
                .routeId("call-external-ws")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, simple(restProducerConfig.getMethod()))
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.TEXT_XML_VALUE))
                .setHeader(Exchange.HTTP_URI, simple(restProducerConfig.getProtocol()+"://"
                        +restProducerConfig.getHost()+restProducerConfig.getContext()))
                .setHeader(Constant.OPERATION_NAME, simple("{{rest.produces.operationname}}"))
                .setHeader("FILE_ID", simple("${header.id_file}"))
                .setHeader(Constant.VARIABLE_NAME, simple("${header.varname}"))
                .to("velocity:templates/request.vm?loaderCache=false")
                .to("direct:logRequestExternalService")
                .to("http://call")
                .convertBodyTo(String.class)
                .to("direct:logResponseExternalService")
                .end();

    }
}

