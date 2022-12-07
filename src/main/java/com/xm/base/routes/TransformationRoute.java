package com.xm.base.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import com.base.xm.dto.RequestFile;
import com.base.xm.process.BuildResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.xm.base.constant.Constant;

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
                                .wireTap("direct:send-amq-exception")
                                .end();

                onException(Exception.class)
                                .handled(true)
                                .log(LoggingLevel.ERROR, "${headers.stackHeader}; Error general: ${routeId}")
                                .log(LoggingLevel.ERROR,
                                                "${headers.stackHeader}; ExceptionClassName: ${exchangeProperty.CamelExceptionCaught.class.name}")
                                .log(LoggingLevel.ERROR, "${headers.stackHeader};StackTrace: ${exception.stacktrace}")
                                .setHeader("CamelHttpResponseCode", simple("500"))
                                .setBody(simple("${exception.message}"))
                                .wireTap("direct:send-amq-exception")
                                .end();

                from(Constant.ROUTE_INIT)
                                .routeId("ROUTE_INIT")
                                .streamCaching()
                                .to("direct:logRequestMS")
                                .setProperty("variable", header("varname"))
                                .setProperty("fecha", simple("${date:now:yyyy-MM-dd}"))
                                .setProperty(Constant.FILE_ID, header("id_file"))
                                .unmarshal().json(JsonLibrary.Jackson, RequestFile.class)
                                .setBody(simple("${body.payload}"))
                                .convertBodyTo(String.class)
                                .to("direct:call-external-ws")
                                .to(Constant.ROUTE_VALIDATE_RESPONSE)
                                .to("direct:logResponseMS")
                                .end();
                
                from(Constant.ROUTE_VALIDATE_RESPONSE)
		                .routeId("ROUTE_VALIDATE_RESPONSE")
		                .streamCaching()
		                .setProperty("SAMResponseXML").xpath("//result/text()")
		                .choice()
		                	.when(simple("${exchangeProperty.SAMResponseXML} == 'ERROR' "))
		                		.setHeader(Constant.CAUSE_ERROR).xpath("//description/text()")
		                		.process(new BuildResponse())
		                		.throwException(Exception.class,"${body}")
		                	.endChoice()
		                	.when(simple("${exchangeProperty.SAMResponseXML} == 'OK' "))
		                		.process(new BuildResponse())
	                		.endChoice()
	                		.otherwise()
	                			.throwException(Exception.class,"Excepcion en el envìo del XML a SAM")
	                		.endChoice()
		                .end()
		                .end();

        }
}
