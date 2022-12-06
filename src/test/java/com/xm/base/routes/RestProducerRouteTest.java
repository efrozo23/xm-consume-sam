package com.xm.base.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.base.xm.dto.RequestFile;

@SpringBootTest
@CamelSpringBootTest

public class RestProducerRouteTest {
	
	@Autowired
    private ProducerTemplate template;
	
	private static ClientAndServer mockServer;

	@BeforeAll()
	public static void startServer() {
		mockServer = ClientAndServer.startClientAndServer(8090);
	}
    
    
    @Test
    void whenSendBody_thenGreetingReceivedSuccessfully() throws Exception {
    	RequestFile body = new RequestFile();
    	body.setPayload("ok");
    	URI pathFileResponse = this.getClass().getResource("/response/response.xml").toURI();
		byte[] bodyResponse = Files.readAllBytes(new File(pathFileResponse).toPath());
    	mockServer.when(HttpRequest.request().withPath("/rest/v1/search"))
		.respond(HttpResponse.response(new String(bodyResponse)).withStatusCode(200));
    	final String ret = template.requestBodyAndHeader("direct:call-external-ws",body.getPayload(), "id", "1", String.class);
        assertEquals(new String(bodyResponse), ret);
    }
    
    @AfterAll
	public static void stopServer() {
		mockServer.stop();
		
	}
}
