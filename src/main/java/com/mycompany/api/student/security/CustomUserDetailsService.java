package com.mycompany.api.student.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mycompany.api.student.repository.User;
import com.mycompany.api.student.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<User> findByUsername = userRepo.findByUsername(username);
		
		if(!findByUsername.isPresent()) {
			throw new UsernameNotFoundException("No user found with the given user name ");
		}
		return new CustomUserDetails(findByUsername.get());
	}

}
