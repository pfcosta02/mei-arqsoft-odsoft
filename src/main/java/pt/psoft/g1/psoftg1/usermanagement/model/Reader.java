package pt.psoft.g1.psoftg1.usermanagement.model;

public class Reader extends User {

    protected Reader()
    {

    }

    public Reader(String username, String password) {
        super(username, password);
        this.addAuthority(new Role(Role.READER));
    }

    /**
     * Factory method para criação de Reader.
     *
     * @param username
     * @param password
     * @param name
     * @return novo Reader com ROLE_READER
     */
    public static Reader newReader(final String username, final String password, final String name) {
        final var u = new Reader(username, password);
        u.setName(name);
        return u;
    }
}
