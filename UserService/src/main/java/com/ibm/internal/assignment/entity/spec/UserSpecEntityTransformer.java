package com.ibm.internal.assignment.entity.spec;

import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.internal.assignment.entity.User;

public class UserSpecEntityTransformer {

	
	private User user;

	private UserSpec userSpec;

	public UserSpecEntityTransformer(UserSpec userSpec) {
		this.userSpec = userSpec;
		this.user = new User();
		
	}

	public User transformSpecToEntity() {
		if (userSpec.getId() != null)
			user.setId(userSpec.getId());
		if (userSpec.getAddress() != null)
			user.setAddress(userSpec.getAddress());
		if (userSpec.getEmailAddress() != null)
			user.setEmail(userSpec.getAddress());
		if (userSpec.getFinPortfolioId() != null)
			user.setPortfolioId((userSpec.getFinPortfolioId()));
		if (userSpec.getFname() != null)
			user.setFirstname(userSpec.getFname());
		if (userSpec.getLname() != null)
			user.setLastname(userSpec.getLname());
		if (userSpec.getPwd() != null)
			user.setPwd(userSpec.getPwd());
		if (userSpec.getStatus() != null)
			user.setStatus(userSpec.getStatus());
		if (userSpec.getType() != null)
			user.setType(userSpec.getType());
		if (userSpec.getUsername() != null)
			user.setUname(userSpec.getUsername());
		return user;
	}

}
