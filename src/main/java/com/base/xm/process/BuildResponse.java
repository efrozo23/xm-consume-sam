package com.base.xm.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.MediaType;

import com.base.xm.dto.ResponseSAM;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BuildResponse implements Processor{

	@Override
	public void process(Exchange e) throws Exception {
		if(e.getProperty("SAMResponseXML") == null) {
			e.setProperty("SAMResponseXML", "ERROR GENERAL");
		}
		ResponseSAM r = new ResponseSAM();
		r.setEstado(e.getProperty("SAMResponseXML",String.class));
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(r);
		e.getIn().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		e.getIn().setBody(json);
	}

}
