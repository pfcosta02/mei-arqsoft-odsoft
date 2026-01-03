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
package pt.psoft.g1.psoftg1.usermanagement.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.psoft.g1.psoftg1.shared.model.Name;

/**
 * Based on https://github.com/Yoh0xFF/java-spring-security-example
 *
 */
public class User implements UserDetails
{
    private String id;
    private Long version;
    private boolean enabled = true;
    private String username;
    private String password;
    private Name name;
    private final Set<Role> authorities = new HashSet<>();

    protected User() {}

    public User(final String username, final String password)
    {
        setUsername(username);
        setPassword(password);

        this.version = 0L;
    }

    public static User newUser(final String username, final String password, final String name)
    {
        final var u = new User(username, password);
        u.setName(name);
        return u;
    }

    public static User newUser(final String username, final String password, final String name, final String role)
    {
        final var u = new User(username, password);
        u.setName(name);
        u.addAuthority(new Role(role));
        return u;
    }

    public void setId(String id) { this.id = id; }

    public void setPassword(final String password)
    {
        // Password passwordCheck = new Password(password);
        if (password != null && !password.startsWith("$2a$"))
        {
            final PasswordEncoder encoder = new BCryptPasswordEncoder();
            this.password = encoder.encode(password);
        }
        else
        {
            this.password = password;
        }

    }

    public void setUsername(final String username)
    {
        this.username = username;
    }


    public void addAuthority(final Role r)
    {
        authorities.add(r);
    }

    public void setName(String name)
    {
        this.name = new Name(name);
    }

    public void setEnabled(boolean value)
    {
        this.enabled = value;
    }

    // getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Name getName() { return name; }
    public boolean isEnabled() { return enabled; }
    @Override
    public Set<Role> getAuthorities() { return authorities; }
    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }
    // public Long getId() { return id; }
    public Long getVersion() { return version; }
}