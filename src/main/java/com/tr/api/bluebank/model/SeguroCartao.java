package com.tr.api.bluebank.model;
import lombok.Data;

@Data
public class SeguroCartao {
	
    private String tipo;
    private double valor;
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}

}
