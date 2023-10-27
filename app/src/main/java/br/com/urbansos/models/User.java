package br.com.urbansos.models;

public class User {
    private String name;
    private String email;
    private String cpf;
    private String username;
    private String password;

    public User(String name, String email, String cpf, String username, String password) {
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.username = username;
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
