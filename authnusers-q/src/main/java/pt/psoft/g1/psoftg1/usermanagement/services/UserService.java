/*
 * Copyright (c) 2022-2024 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package pt.psoft.g1.psoftg1.usermanagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.idgeneratormanagement.IdGenerator;
import pt.psoft.g1.psoftg1.shared.repositories.ForbiddenNameRepository;
import pt.psoft.g1.psoftg1.shared.services.Page;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserTempRepository;

import pt.psoft.g1.psoftg1.usermanagement.dto.UserDTO;
import pt.psoft.g1.psoftg1.usermanagement.dto.RoleDTO;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Based on https://github.com/Yoh0xFF/java-spring-security-example
 *
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepo;
	private final UserTempRepository userTempRepo;

	private final EditUserMapper userEditMapper;

	private final ForbiddenNameRepository forbiddenNameRepository;

	private final PasswordEncoder passwordEncoder;

	private final IdGenerator idGenerator;

	public User getUser(final String id) {
		return userRepo.getById(id);
	}

	public Optional<User> findByUsername(final String username) { return userRepo.findByUsername(username); }

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return user;
	}

    public void createEvent(UserDTO userDTO)
    {
		User user = User.newUser(userDTO.username, userDTO.password, userDTO.fullname);
        user.setId(userDTO.id);
        user.setEnabled(userDTO.enabled);
        user.setVersion(userDTO.version);

        // Converte RoleDTO -> Role
        for (RoleDTO roleDTO : userDTO.authorities) 
        {
            user.addAuthority(new Role(roleDTO.getAuthority()));
        }
		/* Persistir o User */
        userRepo.save(user);
    }

    public void updateEvent(UserDTO userDTO)
    {
		User user = User.newUser(userDTO.username, userDTO.password, userDTO.fullname);
        user.setId(userDTO.id);
        user.setEnabled(userDTO.enabled);
        user.setVersion(userDTO.version);
            
        // Converte RoleDTO -> Role
        for (RoleDTO roleDTO : userDTO.authorities) 
        {
            user.addAuthority(new Role(roleDTO.getAuthority()));
        }

       	/* Persistir o User Updated */
        userRepo.save(user);
    }

    public void deleteEvent(String userId)
    {
        User user = getUser(userId);

		user.setEnabled(false);

		userRepo.save(user);
    }
}
