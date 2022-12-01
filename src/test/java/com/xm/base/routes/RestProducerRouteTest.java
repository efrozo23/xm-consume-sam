package com.xm.base.routes;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

@SpringBootTest
@CamelSpringBootTest
@MockEndpoints("http://call")
public class RestProducerRouteTest {
	
	@Autowired
    private ProducerTemplate template;

    @EndpointInject("mock://http://call")
    private MockEndpoint mock;
    
    @Test
    void whenSendBody_thenGreetingReceivedSuccessfully() throws InterruptedException {
        mock.expectedBodiesReceived("Hello Baeldung Readers!");
        template.sendBody("direct:call-external-ws", null);
        mock.assertIsSatisfied();
    }
}
