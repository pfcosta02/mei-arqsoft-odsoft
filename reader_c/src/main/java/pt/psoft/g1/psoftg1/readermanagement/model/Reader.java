package pt.psoft.g1.psoftg1.readermanagement.model;

import pt.psoft.g1.psoftg1.shared.model.Name;

public class Reader {
    private String readerId;  // ID próprio do Reader
    private String userId;    // Referência ao User no outro microserviço
    private Name name;
    private String email;

    protected Reader() {}

    public Reader(String readerId, String userId, String name, String email) {
        this.readerId = readerId;
        this.userId = userId;
        setName(name);
        this.email = email;
    }

    public String getReaderId() { return readerId; }
    public String getUserId() { return userId; }
    public Name getName() { return name; }
    public String getEmail() { return email; }

    public void setName(String name) { this.name = new Name(name);}
    public void setUserId(String userId) { this.userId = userId; }

    public static Reader newReader(final String readerId, final String userId, final String name, final String email) {
        return new Reader(readerId, userId, name, email);
    }
}