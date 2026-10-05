package model;

public class User extends Account {
    private String password;
    private int userId;

    public User(String username, String password, String fullName) {
        super(username, fullName);
        this.password = password;
        this.userId = 0;
    }

    public User(int userId, String username, String password, String fullName) {
        super(username, fullName);
        this.password = password;
        this.userId = userId;
    }

    public User() {
        super("default", "Default Name");
    }

    public String getPassword() {
        return password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String getRole() {
        if ("admin".equalsIgnoreCase(this.username)) {
            return "Administrator";
        }
        return "Home Cook";
    }

    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public boolean checkPassword(char[] inputPassword) {
        String passString = new String(inputPassword);
        return this.password.equals(passString);
    }

    public boolean isAdmin() {
        return "Administrator".equals(getRole());
    }


    public void setPassword(String newPassword) throws UserException {
        if (newPassword.length() < 4) {
            throw new UserException("Password is too short! It must be at least 4 characters.");
        }
        this.password = newPassword;
    }

}