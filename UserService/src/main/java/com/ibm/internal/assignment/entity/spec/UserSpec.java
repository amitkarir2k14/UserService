package com.ibm.internal.assignment.entity.spec;


public class UserSpec {

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getFinPortfolioId() {
		return finPortfolioId;
	}
	public void setFinPortfolioId(String finPortfolioId) {
		this.finPortfolioId = finPortfolioId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public UserSpec(Long id, String fname, String lname, String emailAddress, String username, String pwd,
			String finPortfolioId, String address, String status, String type) {
		this.id = id;
		this.fname = fname;
		this.lname = lname;
		this.emailAddress = emailAddress;
		this.username = username;
		this.pwd = pwd;
		this.finPortfolioId = finPortfolioId;
		this.address = address;
		this.status = status;
		this.type = type;
	}
	public UserSpec(){}
	
	public UserSpec(String fname, String lname, String emailAddress, String username, String pwd,
			String finPortfolioId, String address, String status, String type) {
		super();
		this.fname = fname;
		this.lname = lname;
		this.emailAddress = emailAddress;
		this.username = username;
		this.pwd = pwd;
		this.finPortfolioId = finPortfolioId;
		this.address = address;
		this.status = status;
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	private Long id;
	private String fname;
	private String lname;
	private String emailAddress;
	private String username;
	private String pwd;
	private String finPortfolioId;
	private String address;
	private String status;
	private String type;
}

	

