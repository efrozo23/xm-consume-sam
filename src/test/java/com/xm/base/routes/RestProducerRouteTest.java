package com.xm.base.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.w3c.dom.Document;

import com.base.xm.dto.RequestFile;
import com.xm.base.constant.Constant;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RestProducerRouteTest {
	
	@Autowired
    private ProducerTemplate template;
	
	private static ClientAndServer mockServer;

	@BeforeAll()
	public static void startServer() {
		mockServer = ClientAndServer.startClientAndServer(8090);
	}
	
//	@BeforeEach()
//	public void setUp() {
//		mockServer.when(HttpRequest.request().withPath("/rest/v1/search"))
//		.respond(HttpResponse.response(new String("ok")).withStatusCode(400));
//	}
    
    
    @Test
    void whenSendFileToSAMIsOk() throws Exception {
    	RequestFile body = new RequestFile();
    	body.setPayload("ok");
    	URI pathFileResponse = this.getClass().getResource("/response/response.xml").toURI();
		byte[] bodyResponse = Files.readAllBytes(new File(pathFileResponse).toPath());
		mockServer.clear(HttpRequest.request().withPath("/rest/v1/search"));
    	mockServer.when(HttpRequest.request().withPath("/rest/v1/search"))
		.respond(HttpResponse.response(new String(bodyResponse)).withStatusCode(200));
    	final String ret = template.requestBodyAndHeader("direct:call-external-ws",body.getPayload(), "id", "1", String.class);
        assertEquals(new String(bodyResponse), ret);
    }
    
    @Test
    void whenSendFileToSAMIsERROR() throws Exception {
    	RequestFile body = new RequestFile();
    	body.setPayload("ok");
    	URI pathFileResponse = this.getClass().getResource("/response/response.xml").toURI();
		byte[] bodyResponse = Files.readAllBytes(new File(pathFileResponse).toPath());
    	mockServer.when(HttpRequest.request().withPath("/rest/v1/search"))
		.respond(HttpResponse.response(new String(bodyResponse)).withStatusCode(400));
    	final String ret = template.requestBodyAndHeader("direct:call-external-ws",body.getPayload(), "id", "1", String.class);
        assertEquals("{\"estado\":\"ERROR GENERAL\"}", ret);
    }
    
    @Test
	public void whenValidResponseIsOk() throws Exception{
    	
		URI pathFileResponse = this.getClass().getResource("/response/responseok.xml").toURI();
		byte[] bodyResponse = Files.readAllBytes(new File(pathFileResponse).toPath());
		DocumentBuilderFactory fábricaCreadorDocumento = DocumentBuilderFactory.newInstance();
		DocumentBuilder creadorDocumento = fábricaCreadorDocumento.newDocumentBuilder();
		Document documento = creadorDocumento.parse(new ByteArrayInputStream(bodyResponse));
		final String ret = template.requestBodyAndHeader(Constant.ROUTE_VALIDATE_RESPONSE,documento, "id", "1", String.class);
		assertEquals("{\"estado\":\"OK\"}",ret);
	}
    
    @AfterAll
	public static void stopServer() {
		mockServer.stop();
		
	}
}
