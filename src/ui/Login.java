package ui;

import model.*;
import service.*;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private BufferedImage backgroundImage;

    public Login() {
        loadBackgroundImage();
        initializeUI();
    }

    private void loadBackgroundImage() {
        try {
            java.io.InputStream imgStream = getClass().getResourceAsStream("/image/Food_image.png");
            if (imgStream != null) {
                backgroundImage = ImageIO.read(imgStream);
            } else {
                File imgFile = new File("src/image/Food_image.png");
                if (imgFile.exists()) {
                    backgroundImage = ImageIO.read(imgFile);
                } else {
                    System.out.println("Background image not found.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }
    }

    private void initializeUI() {
        setTitle("RecipeApp - Login");
        setSize(MainFrame.LOGIN_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (backgroundImage != null) {
                    g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setColor(MainFrame.BACKGROUND_WARM);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(null);

        createBrandingElements(mainPanel);

        JPanel loginCard = createLoginCard();

        int cardWidth = 440;
        int cardHeight = 480;
        int cardX = 1000 - cardWidth - 80;
        int cardY = (650 - cardHeight) / 2;

        loginCard.setBounds(cardX, cardY, cardWidth, cardHeight);
        mainPanel.add(loginCard);

        add(mainPanel);
    }

    private void createBrandingElements(JPanel mainPanel) {
        JLabel brandName = new JLabel("RecipeApp");

        brandName.setFont(new Font("Segoe UI", Font.BOLD, 40));
        brandName.setForeground(new Color(205, 165, 130));

        // Am ajustat ușor poziția Y pentru a-l centra vertical în forma maro, dat fiind fontul mai mic
        brandName.setBounds(85, 165, 400, 50);
        mainPanel.add(brandName);

        JLabel tagline = new JLabel("Cook. Create. Share.");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        tagline.setForeground(new Color(205, 165, 130, 220));
        tagline.setBounds(85, 215, 400, 30);
        mainPanel.add(tagline);
    }

    private JPanel createLoginCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 30));
                g2.fill(new RoundRectangle2D.Double(4, 4, getWidth() - 8, getHeight() - 8, 24, 24));

                // Fundal Bej Cald
                g2.setColor(new Color(245, 238, 225));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 8, getHeight() - 8, 24, 24));

                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 40, 10, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        welcomeLabel.setForeground(new Color(80, 80, 80));
        gbc.gridy = 0;
        gbc.insets = new Insets(40, 40, 35, 40);
        card.add(welcomeLabel, gbc);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setForeground(MainFrame.TEXT_SECONDARY);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 40, 5, 40);
        gbc.anchor = GridBagConstraints.WEST;
        card.add(usernameLabel, gbc);

        usernameField = createCleanTextField("Username");
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 40, 10, 40);
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setForeground(MainFrame.TEXT_SECONDARY);
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 40, 5, 40);
        gbc.anchor = GridBagConstraints.WEST;
        card.add(passwordLabel, gbc);

        passwordField = createCleanPasswordField("Password");
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 40, 18, 40);
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(passwordField, gbc);

        JButton loginButton = MainFrame.createPrimaryButton("Login");
        loginButton.setPreferredSize(new Dimension(360, 50));
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridy = 5;
        gbc.insets = new Insets(8, 40, 25, 40);
        card.add(loginButton, gbc);

        JPanel createAccountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        createAccountPanel.setOpaque(false);
        JLabel newToLabel = new JLabel("New to RecipeApp?");
        newToLabel.setFont(MainFrame.SMALL_FONT);
        newToLabel.setForeground(MainFrame.TEXT_SECONDARY);
        JButton createAccountLink = MainFrame.createTextLink("Create an account →");
        createAccountLink.addActionListener(e -> showRegisterDialog());
        createAccountPanel.add(newToLabel);
        createAccountPanel.add(createAccountLink);

        gbc.gridy = 6;
        gbc.insets = new Insets(8, 40, 40, 40);
        card.add(createAccountPanel, gbc);

        passwordField.addActionListener(e -> handleLogin());

        return card;
    }

    private JTextField createCleanTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), MainFrame.RADIUS_MEDIUM, MainFrame.RADIUS_MEDIUM);
                if (isFocusOwner()) {
                    g2.setColor(MainFrame.PRIMARY_COLOR);
                    g2.setStroke(new BasicStroke(2f));
                } else {
                    g2.setColor(MainFrame.BORDER_COLOR);
                    g2.setStroke(new BasicStroke(1.5f));
                }
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, MainFrame.RADIUS_MEDIUM, MainFrame.RADIUS_MEDIUM);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(MainFrame.NORMAL_FONT);
        field.setForeground(MainFrame.TEXT_LIGHT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(15, 18, 15, 18));
        field.setOpaque(false);
        field.setText(placeholder);
        field.setPreferredSize(new Dimension(360, 50));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(MainFrame.TEXT_PRIMARY);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(MainFrame.TEXT_LIGHT);
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    private JPasswordField createCleanPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), MainFrame.RADIUS_MEDIUM, MainFrame.RADIUS_MEDIUM);
                if (isFocusOwner()) {
                    g2.setColor(MainFrame.PRIMARY_COLOR);
                    g2.setStroke(new BasicStroke(2f));
                } else {
                    g2.setColor(MainFrame.BORDER_COLOR);
                    g2.setStroke(new BasicStroke(1.5f));
                }
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, MainFrame.RADIUS_MEDIUM, MainFrame.RADIUS_MEDIUM);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(MainFrame.NORMAL_FONT);
        field.setForeground(MainFrame.TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(15, 18, 15, 18));
        field.setOpaque(false);
        field.setEchoChar((char)0);
        field.setPreferredSize(new Dimension(360, 50));
        return field;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || username.equals("Username")) {
            MainFrame.showErrorDialog(this, "Please enter your username and password!", "Error");
            return;
        }

        String query = "SELECT UserID, FullName, Password FROM Users WHERE Username = ?";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPass = rs.getString("Password");
                String fullName = rs.getString("FullName");
                int userId = rs.getInt("UserID");

                if (password.equals(dbPass)) {
                    MainFrame.showSuccessDialog(this, "Welcome back, " + fullName + "!", "Login Successful");

                    User loggedInUser = new User(username, password, fullName);
                    loggedInUser.setUserId(userId);

                    SwingUtilities.invokeLater(() -> {
                        RecipeApp app = new RecipeApp(loggedInUser);
                        app.setVisible(true);
                    });
                    dispose();
                } else {
                    MainFrame.showErrorDialog(this, "Invalid password!", "Login Failed");
                    passwordField.setText("");
                }
            } else {
                MainFrame.showErrorDialog(this, "User not found!", "Login Failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MainFrame.showErrorDialog(this, "Database Error: " + e.getMessage(), "Error");
        }
    }

    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Create Account", true);
        dialog.setSize(MainFrame.REGISTER_SIZE);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(MainFrame.PADDING_LARGE, MainFrame.PADDING_LARGE));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                MainFrame.PADDING_XLARGE, MainFrame.PADDING_XLARGE,
                MainFrame.PADDING_XLARGE, MainFrame.PADDING_XLARGE
        ));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(MainFrame.HEADING_FONT);
        titleLabel.setForeground(MainFrame.TEXT_PRIMARY);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(MainFrame.PADDING_MEDIUM, 0, MainFrame.PADDING_MEDIUM, 0);

        JTextField newEmailField = MainFrame.createModernTextField("Username", null);
        JPasswordField newPasswordField = MainFrame.createModernPasswordField("Password", null);
        JTextField fullNameField = MainFrame.createModernTextField("Full Name", null);

        gbc.gridy = 0;
        formPanel.add(fullNameField, gbc);
        gbc.gridy = 1;
        formPanel.add(newEmailField, gbc);
        gbc.gridy = 2;
        formPanel.add(newPasswordField, gbc);

        JButton registerBtn = MainFrame.createPrimaryButton("Create Account");
        registerBtn.setPreferredSize(new Dimension(350, 50));

        registerBtn.addActionListener(e -> {
            String username = newEmailField.getText().trim();
            String password = new String(newPasswordField.getPassword());
            String fullName = fullNameField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() ||
                    username.equals("Username") || fullName.equals("Full Name")) {
                MainFrame.showErrorDialog(dialog, "All fields are required!", "Error");
                return;
            }

            try (Connection conn = DataBase.getConnection()) {
                String checkSql = "SELECT UserID FROM Users WHERE Username = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, username);
                    if (checkStmt.executeQuery().next()) {
                        MainFrame.showErrorDialog(dialog, "Username already exists!", "Error");
                        return;
                    }
                }

                String insertSql = "INSERT INTO Users (Username, Password, FullName) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, password);
                    insertStmt.setString(3, fullName);
                    insertStmt.executeUpdate();

                    MainFrame.showSuccessDialog(dialog, "Account created! You can now login.", "Success");
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                MainFrame.showErrorDialog(dialog, "Database Error: " + ex.getMessage(), "Error");
            }
        });

        gbc.gridy = 3;
        gbc.insets = new Insets(MainFrame.PADDING_LARGE, 0, 0, 0);
        formPanel.add(registerBtn, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}