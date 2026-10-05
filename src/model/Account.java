package model;

public abstract class Account {
    protected String username;
    protected String fullName;

    public Account(String username, String fullName) {
        this.username = username;
        this.fullName = fullName;
    }

    // Encapsulation
    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    // ABSTRACT METHOD
    public abstract String getRole();

    // Concrete method
    public void displayWelcome() {
        System.out.println("Welcome, " + fullName + "!");
    }
}