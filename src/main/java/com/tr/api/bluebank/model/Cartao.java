package com.tr.api.bluebank.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Cartao {

    private double limite;
    private double fatura;
    private Integer senha;
    private List<SeguroCartao> seguros;
    
    public Cartao() {
    	this.seguros = new ArrayList<>();
    }

	public double getLimite() {
		return limite;
	}

	public void setLimite(double limite) {
		this.limite = limite;
	}

	public double getFatura() { return fatura; }

	public void setFatura(double fatura) {
		this.fatura = fatura;
	}

	public Integer getSenha() {
		return senha;
	}

	public void setSenha(Integer senha) {
		this.senha = senha;
	}

	public List<SeguroCartao> getSeguros() {
		return seguros;
	}

	public void setSeguros(List<SeguroCartao> seguros) {
		this.seguros = seguros;
	}

}
