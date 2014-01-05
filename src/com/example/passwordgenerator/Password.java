package com.example.passwordgenerator;

public class Password {

	private long id;
	private String password;
	
	public long getID() {
		return id;
	}

	public void setID(long id) {
		this.id = id;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return password;
	}
}
