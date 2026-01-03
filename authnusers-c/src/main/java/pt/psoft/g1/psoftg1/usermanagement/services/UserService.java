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

import com.fasterxml.jackson.databind.ObjectMapper;

import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.idgeneratormanagement.IdGenerator;
import pt.psoft.g1.psoftg1.shared.repositories.ForbiddenNameRepository;
import pt.psoft.g1.psoftg1.shared.services.Page;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;
import pt.psoft.g1.psoftg1.usermanagement.repositories.OutboxEventRepository;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserTempRepository;

import pt.psoft.g1.psoftg1.usermanagement.dto.RealUserDTO;
import pt.psoft.g1.psoftg1.usermanagement.dto.UserDTO;
import pt.psoft.g1.psoftg1.usermanagement.dto.RoleDTO;
import pt.psoft.g1.psoftg1.shared.model.AuthNUsersEvents;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.OutboxEvent;
import pt.psoft.g1.psoftg1.usermanagement.model.OutboxEnum;

import pt.psoft.g1.psoftg1.exceptions.NotFoundException;


import com.fasterxml.jackson.databind.ObjectMapper;
import pt.psoft.g1.psoftg1.usermanagement.publishers.AuthNUsersEventsPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper; // Para serializar payload

    @Transactional
    public User create(final CreateUserRequest request) {
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new ConflictException("Username already exists!");
        }

        Iterable<String> words = List.of(request.getName().split("\\s+"));
        for (String word : words){
            if(!forbiddenNameRepository.findByForbiddenNameIsContained(word).isEmpty()) {
                throw new IllegalArgumentException("Name contains a forbidden word");
            }
        }

        User user;
        switch(request.getRole()) {
            case Role.READER: {
                // TODO> Aqui mandar msg para o microservico do reader
                // user = Reader.newReader(request.getUsername(), request.getPassword(), request.getName());
                user = null;
                break;
            }
            case Role.LIBRARIAN: {
                user = Librarian.newLibrarian(request.getUsername(), request.getPassword(), request.getName());
                break;
            }
            default: {
                return null;
            }
        }


        if (user == null)
        {
            throw new IllegalArgumentException("Invalid role");
        }

        //final User user = userEditMapper.create(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        //user.addAuthority(new Role(request.getRole()));
        user.setId(idGenerator.generateId());
        return userRepo.save(user);
    }

    @Transactional
    public User update(final String id, final EditUserRequest request) {
        final User user = userRepo.getById(id);
        userEditMapper.update(request, user);

        User userUpdated = userRepo.save(user);

        /* Dizemos ao Servico Querys para alterar do lado deles também */
        // publisher.publishUserUpdatedEvent(userUpdated);

        return userUpdated;
    }

    @Transactional
    public User delete(final String id) {
        final User user = userRepo.getById(id);

        // user.setUsername(user.getUsername().replace("@", String.format("_%s@",
        // user.getId().toString())));
        user.setEnabled(false);
        User userChanged = userRepo.save(user);

        /* Dizemos ao Servico Querys para alterar do lado deles também */
        // publisher.publishUserDeletedEvent(user.getId());

        return userChanged;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }

    public boolean usernameExists(final String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    public User getUser(final String id) {
        return userRepo.getById(id);
    }

    public Optional<User> findByUsername(final String username) { return userRepo.findByUsername(username); }

    public List<User> searchUsers(Page page, SearchUsersQuery query) {
        if (page == null) {
            page = new Page(1, 10);
        }
        if (query == null) {
            query = new SearchUsersQuery("", "");
        }
        return userRepo.searchUsers(page, query);
    }

    public User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AccessDeniedException("User is not logged in");
        }

        // split is present because jwt is storing the id before the username, separated by a comma
        String loggedUsername = jwt.getClaimAsString("sub").split(",")[1];

        Optional<User> loggedUser = findByUsername(loggedUsername);
        if (loggedUser.isEmpty()) {
            throw new AccessDeniedException("User is not logged in");
        }

        return loggedUser.get();
    }

    public void createUserTemp(UserDTO userDTO)
    {
        /* Primeiro verifico se nao existe nenhum User igual ja no repo Temporario ou Permanente */
        if (userTempRepo.findByUsername(userDTO.username).isPresent() || userRepo.findByUsername(userDTO.username).isPresent())
        {
            throw new ConflictException("Username already exists!");
        }

        /* Confirmar que o nome é valido */
        Iterable<String> words = List.of(userDTO.fullname.split("\\s+"));
        for (String word : words)
        {
            if(!forbiddenNameRepository.findByForbiddenNameIsContained(word).isEmpty())
            {
                throw new IllegalArgumentException("Name contains a forbidden word");
            }
        }

        /* Persistir o User temporariamente */
        User user = User.newUser(userDTO.username, passwordEncoder.encode(userDTO.password), userDTO.fullname, Role.READER);
        user.setId(idGenerator.generateId());

        userTempRepo.save(user);

        // Em vez de publicar diretamente, gravamos no Outbox
        try
        {
            UserDTO dto = new UserDTO(user.getId(), userDTO.readerId, user.getUsername(), user.getPassword(), user.getName().getName(), user.getVersion());
            String payload = objectMapper.writeValueAsString(dto);

            OutboxEvent event = new OutboxEvent();
            event.setAggregateId(user.getId());
            event.setEventType(AuthNUsersEvents.TEMP_USER_CREATED);
            event.setPayload(payload);
            event.setStatus(OutboxEnum.NEW);

            outboxEventRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar evento Outbox", e);
        }
    }

    public void persistTemporary(String userId)
    {
        User userToPresist = userTempRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Nao ha nenhum user temporario com o id:" + userId));

        /* Armazenar o userId correspondente */
        userToPresist.setId(userId);

        /* Primeiro persistimos o User */
        User userSaved = userRepo.save(userToPresist);

        /* Agora eliminamor o User temporario */
        userTempRepo.delete(userId);

        System.out.println("User com o id(" + userId + ") persistido permanentemente e eliminado do repositorio temporario");

        // Gravar evento Outbox para USER_CREATED
        try
        {
            RealUserDTO dto = new RealUserDTO(userSaved.getId(), userSaved.getUsername(), userSaved.getPassword(), userSaved.getName().getName(), userSaved.getVersion(), userSaved.isEnabled(),
                    userSaved.getAuthorities()
                            .stream()
                            .map(role -> new RoleDTO(role.getAuthority()))
                            .collect(Collectors.toSet())
            );
            String payload = objectMapper.writeValueAsString(dto);

            OutboxEvent event = new OutboxEvent();
            event.setAggregateId(userSaved.getId());
            event.setEventType(AuthNUsersEvents.USER_CREATED);
            event.setPayload(payload);
            event.setStatus(OutboxEnum.NEW);

            outboxEventRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar evento Outbox", e);
        }
    }

    @Transactional
    public List<RealUserDTO> usersToDTO()
    {
        List<User> users = userRepo.findAll();
        List<RealUserDTO> userDTOs = new ArrayList<>();

        for (User user: users)
        {
            RealUserDTO dto = new RealUserDTO(user.getId(), user.getUsername(), user.getPassword(),
                    user.getName().getName(), user.getVersion(), user.isEnabled(),
                    user.getAuthorities()
                            .stream()
                            .map(role -> new RoleDTO(role.getAuthority()))
                            .collect(Collectors.toSet())
            );
            userDTOs.add(dto);
        }

        return userDTOs;
    }
}