package com.homedepot.interns;

public class Greeting {

	private int id;
	private String message;
	private String sender;
	private String recipient;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setMessage(String msg){
		this.message = msg;
	}
	
	public void setSender(String s){
		this.sender = s;
	}
	public void setRecipient(String r){
		this.recipient = r; 
	}
	public String getMessage(){
		return this.message;
	}
	public String getSender(){
		return this.sender;
	}
	public String getRecipient(){
		return this.recipient;
	}
	
}
