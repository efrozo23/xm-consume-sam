package com.base.xm.dto;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("")
public class ResponseSAM {

	private String estado;

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}
