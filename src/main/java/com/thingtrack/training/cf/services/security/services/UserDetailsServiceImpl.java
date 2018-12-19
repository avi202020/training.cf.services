package com.thingtrack.training.cf.services.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.thingtrack.training.cf.services.domain.User;
import com.thingtrack.training.cf.services.repository.UserRepository;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 Optional<User> user = userRepository.findByUsername(username);
		 
	     if (user == null)
	    	 throw new UsernameNotFoundException("User Not Found with -> username or email : " + username);	     
	     
	     return UserPrinciple.build(user.get());
	}
}
