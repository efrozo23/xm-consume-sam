package com.base.xm.dto;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("")
public class RequestFile {
	private String payload;

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

}
