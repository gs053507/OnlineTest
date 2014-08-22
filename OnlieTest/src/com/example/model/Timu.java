package com.example.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;


public class Timu implements Serializable{
	
	@Expose
	String id;
	@Expose
	String catalog;
	public Timu(String id, String catalog) {
		super();
		this.id = id;
		this.catalog = catalog;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	
	

}
