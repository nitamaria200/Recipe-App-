package ui;

import model.*;
import service.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.text.SimpleDateFormat;

public class RecipeApp extends JFrame {
    private RecipeService recipeService;
    private JPanel recipesContainer;
    private JTextField searchField;
    private List<Recipe> currentRecipes;
    private User currentUser; // Logged-in user

    // Color Scheme
    private static final Color PRIMARY_RUST = new Color(183, 85, 40);
    private static final Color PRIMARY_DARK = new Color(153, 65, 30);
    private static final Color PRIMARY_LIGHT = new Color(203, 105, 60);
    private static final Color ACCENT_GOLD = new Color(212, 175, 55);
    private static final Color BG_WARM = new Color(255, 251, 245);
    private static final Color BG_BEIGE = new Color(245, 237, 225);
    private static final Color CARD_BG = new Color(255, 251, 245);
    private static final Color TEXT_DARK = new Color(51, 51, 51);
    private static final Color TEXT_SECONDARY = new Color(102, 102, 102);
    private static final Color SUCCESS_GREEN = new Color(130, 145, 95);  // Warm olive green
    private static final Color DANGER_RED = new Color(211, 47, 47);

    // Images folder for recipe photos
    private static final String IMAGES_FOLDER = "recipe_images";

    // Placeholder colors for recipes without images
    private static final Color[] PLACEHOLDER_COLORS = {
            new Color(235, 225, 210),  // Light cream
            new Color(225, 215, 200),  // Soft beige
            new Color(240, 230, 215),  // Ivory
            new Color(230, 220, 205),  // Pale beige
            new Color(220, 210, 195),  // Light taupe
            new Color(238, 228, 213)   // Off-white beige
    };

    public RecipeApp(User user) {
        this.currentUser = user;
        recipeService = new RecipeService();
        createImagesFolder();
        initializeUI();
        loadRecipes();
    }

    public RecipeApp() {
        this(new User("guest", "", "Guest User"));
    }

    private void createImagesFolder() {
        File folder = new File(IMAGES_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private void initializeUI() {
        setTitle("RecipeApp - Welcome, " + currentUser.getFullName());
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_WARM);

        JPanel topBar = createTopBar();
        mainPanel.add(topBar, BorderLayout.NORTH);

        JScrollPane scrollPane = createMainContent();
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, BG_BEIGE),
                BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));

        // Left side - Logo
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        JLabel logoText = new JLabel("RecipeApp");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 30));
        logoText.setForeground(new Color(60, 60, 60));

        JLabel logoIcon = new JLabel();
        ImageIcon hatIcon = new ImageIcon("hat.png");
        Image img = hatIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        logoIcon.setIcon(new ImageIcon(img));

        leftPanel.add(logoIcon);
        leftPanel.add(logoText);

        // Right side - Buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JButton addRecipeBtn = createRoundButton("+ New Recipe", PRIMARY_RUST);
        addRecipeBtn.setPreferredSize(new Dimension(160, 45));
        addRecipeBtn.addActionListener(e -> showAddRecipeDialog());

        JButton myAccountBtn = createRoundButton("My Account", new Color(160, 140, 120));
        myAccountBtn.setPreferredSize(new Dimension(140, 45));
        myAccountBtn.addActionListener(e -> showMyAccountDialog());

        // Admin button
        if (currentUser.isAdmin()) {
            JButton adminBtn = createRoundButton("Admin Panel", DANGER_RED);
            adminBtn.setPreferredSize(new Dimension(140, 45));
            adminBtn.addActionListener(e -> showAdminPanel());
            rightPanel.add(adminBtn);
        }

        JButton logoutBtn = createOutlineButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(100, 45));
        logoutBtn.addActionListener(e -> logout());

        rightPanel.add(addRecipeBtn);
        rightPanel.add(myAccountBtn);
        rightPanel.add(logoutBtn);

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    private JScrollPane createMainContent() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_WARM);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        JPanel exploreSection = createExploreSection();
        exploreSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        contentPanel.add(exploreSection);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel popularLabel = new JLabel("Popular Recipes");
        popularLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        popularLabel.setForeground(TEXT_DARK);

        JButton viewAllBtn = createTextButton("View All →");
        viewAllBtn.addActionListener(e -> loadRecipes());

        headerPanel.add(popularLabel, BorderLayout.WEST);
        headerPanel.add(viewAllBtn, BorderLayout.EAST);
        contentPanel.add(headerPanel);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        recipesContainer = new JPanel(new GridLayout(0, 3, 30, 30));
        recipesContainer.setOpaque(false);
        contentPanel.add(recipesContainer);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
    }

    private JPanel createExploreSection() {
        JPanel explorePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Solid color matching placeholder colors (light cream/beige)
                g2.setColor(new Color(235, 225, 210));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.dispose();
            }
        };
        explorePanel.setLayout(new BorderLayout(30, 0));
        explorePanel.setOpaque(false);
        explorePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel exploreTitle = new JLabel("Welcome, " + currentUser.getFullName() + "!");
        exploreTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        exploreTitle.setForeground(PRIMARY_RUST);
        exploreTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel exploreSubtitle = new JLabel("What would you like to cook today?");
        exploreSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        exploreSubtitle.setForeground(TEXT_SECONDARY);
        exploreSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        exploreSubtitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        textPanel.add(exploreTitle);
        textPanel.add(exploreSubtitle);

        JPanel searchPanel = new JPanel(new BorderLayout(15, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        searchField = createWarmSearchField();
        searchField.setPreferredSize(new Dimension(380, 52));

        JButton searchButton = createRoundButton("Search", PRIMARY_RUST);
        searchButton.setPreferredSize(new Dimension(120, 52));
        searchButton.addActionListener(e -> performSearch());

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        explorePanel.add(textPanel, BorderLayout.WEST);
        explorePanel.add(searchPanel, BorderLayout.EAST);

        return explorePanel;
    }

    //  MY ACCOUNT
    private void showMyAccountDialog() {
        JDialog dialog = new JDialog(this, "My Account", true);
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Same color as My Account button (Warm Taupe)
                g2.setColor(new Color(160, 140, 120));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(900, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JLabel nameLabel = new JLabel(currentUser.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        nameLabel.setForeground(Color.WHITE);

        JButton closeBtn = createRoundButton("Close", new Color(255, 255, 255, 80));
        closeBtn.setPreferredSize(new Dimension(100, 40));
        closeBtn.addActionListener(e -> dialog.dispose());

        headerPanel.add(nameLabel, BorderLayout.WEST);
        headerPanel.add(closeBtn, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(TEXT_DARK);

        // My Recipes Tab
        JPanel myRecipesPanel = createMyRecipesPanel(dialog);
        tabbedPane.addTab("  My Recipes  ", myRecipesPanel);

        // My Reviews Tab
        JPanel myReviewsPanel = createMyReviewsPanel(dialog);
        tabbedPane.addTab("  My Reviews  ", myReviewsPanel);

        // Account Details Tab
        JPanel accountDetailsPanel = createAccountDetailsPanel();
        tabbedPane.addTab("  Account Info  ", accountDetailsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createMyRecipesPanel(JDialog parentDialog) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        List<Recipe> myRecipes = recipeService.getRecipesByUserId(currentUser.getUserId());

        if (myRecipes.isEmpty()) {
            JLabel emptyLabel = new JLabel("You haven't added any recipes yet. Click '+ New Recipe' to get started!");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emptyLabel.setForeground(TEXT_SECONDARY);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            JPanel recipesListPanel = new JPanel();
            recipesListPanel.setLayout(new BoxLayout(recipesListPanel, BoxLayout.Y_AXIS));
            recipesListPanel.setBackground(Color.WHITE);

            for (Recipe recipe : myRecipes) {
                JPanel recipeRow = createRecipeRow(recipe, parentDialog);
                recipesListPanel.add(recipeRow);
                recipesListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            JScrollPane scrollPane = new JScrollPane(recipesListPanel);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createRecipeRow(Recipe recipe, JDialog parentDialog) {
        JPanel row = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        row.setBackground(new Color(250, 248, 245));
        row.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        row.setOpaque(false);

        // Recipe info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(recipe.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_DARK);

        JLabel detailsLabel = new JLabel(recipe.getCategory() + "  |  " + recipe.getPrepTime() + " min  |  " + recipe.getServings() + " servings");
        detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsLabel.setForeground(TEXT_SECONDARY);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        infoPanel.add(detailsLabel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        JButton viewBtn = createSmallButton("View", PRIMARY_RUST);
        viewBtn.addActionListener(e -> {
            parentDialog.dispose();
            showRecipeDetails(recipe);
        });

        JButton editBtn = createSmallButton("Edit", ACCENT_GOLD);
        editBtn.addActionListener(e -> {
            parentDialog.dispose();
            showEditRecipeDialog(recipe);
        });

        JButton deleteBtn = createSmallButton("Delete", DANGER_RED);
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(parentDialog,
                    "Are you sure you want to delete '" + recipe.getName() + "'?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                recipeService.deleteRecipe(recipe.getId());
                parentDialog.dispose();
                loadRecipes();
                showMyAccountDialog();
            }
        });

        buttonPanel.add(viewBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        row.add(infoPanel, BorderLayout.CENTER);
        row.add(buttonPanel, BorderLayout.EAST);

        return row;
    }

    private JPanel createMyReviewsPanel(JDialog parentDialog) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        List<Review> myReviews = recipeService.getReviewsByUserId(currentUser.getUserId());

        if (myReviews.isEmpty()) {
            JLabel emptyLabel = new JLabel("You haven't written any reviews yet. Browse recipes and share your thoughts!");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emptyLabel.setForeground(TEXT_SECONDARY);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            JPanel reviewsListPanel = new JPanel();
            reviewsListPanel.setLayout(new BoxLayout(reviewsListPanel, BoxLayout.Y_AXIS));
            reviewsListPanel.setBackground(Color.WHITE);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

            for (Review review : myReviews) {
                JPanel reviewRow = new JPanel(new BorderLayout(15, 0)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                        g2.dispose();
                    }
                };
                reviewRow.setBackground(new Color(250, 248, 245));
                reviewRow.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
                reviewRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
                reviewRow.setOpaque(false);

                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setOpaque(false);

                JLabel recipeLabel = new JLabel(review.getRecipeName());
                recipeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                recipeLabel.setForeground(TEXT_DARK);

                // Rating
                JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                ratingPanel.setOpaque(false);
                JLabel ratingLabel = new JLabel("  " + review.getRating() + "/5  ");
                ratingLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                ratingLabel.setForeground(Color.WHITE);
                ratingLabel.setOpaque(true);
                ratingLabel.setBackground(ACCENT_GOLD);
                ratingLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                ratingPanel.add(ratingLabel);

                JLabel commentLabel = new JLabel(review.getComment());
                commentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                commentLabel.setForeground(TEXT_SECONDARY);

                JLabel dateLabel = new JLabel(dateFormat.format(review.getDate()));
                dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                dateLabel.setForeground(new Color(150, 150, 150));

                infoPanel.add(recipeLabel);
                infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                infoPanel.add(ratingPanel);
                infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                infoPanel.add(commentLabel);
                infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
                infoPanel.add(dateLabel);

                JButton deleteBtn = createSmallButton("Delete", DANGER_RED);
                deleteBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(parentDialog,
                            "Are you sure you want to delete this review?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        recipeService.deleteReview(review.getId());
                        parentDialog.dispose();
                        showMyAccountDialog();
                    }
                });

                reviewRow.add(infoPanel, BorderLayout.CENTER);
                reviewRow.add(deleteBtn, BorderLayout.EAST);

                reviewsListPanel.add(reviewRow);
                reviewsListPanel.add(Box.createRigidArea(new Dimension(0, 12)));
            }

            JScrollPane scrollPane = new JScrollPane(reviewsListPanel);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createAccountDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Title
        JLabel titleLabel = new JLabel("Account Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_RUST);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        addDetailRow(panel, "Full Name", currentUser.getFullName());
        addDetailRow(panel, "Username", "@" + currentUser.getUsername());
        addDetailRow(panel, "User ID", String.valueOf(currentUser.getUserId()));

        return panel;
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelComp.setForeground(TEXT_SECONDARY);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valueComp.setForeground(TEXT_DARK);

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);

        panel.add(row);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
    }

    //  EDIT RECIPE DIALOG
    private void showEditRecipeDialog(Recipe recipe) {
        JDialog dialog = new JDialog(this, "Edit Recipe", true);
        dialog.setSize(700, 800);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Edit Recipe");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_RUST);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        // Image picker
        addFormLabel(formPanel, "Recipe Image");
        JPanel imagePickerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        imagePickerPanel.setOpaque(false);
        imagePickerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imagePickerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        String currentImageName = recipe.hasImage() ? new File(recipe.getImagePath()).getName() : "No image";
        JLabel selectedImageLabel = new JLabel(currentImageName);
        selectedImageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        selectedImageLabel.setForeground(TEXT_SECONDARY);

        final String[] selectedImagePath = {recipe.getImagePath()};

        JButton chooseImageBtn = createSmallButton("Change Image", PRIMARY_LIGHT);
        chooseImageBtn.setPreferredSize(new Dimension(120, 35));
        chooseImageBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedImagePath[0] = selectedFile.getAbsolutePath();
                selectedImageLabel.setText(selectedFile.getName());
            }
        });

        imagePickerPanel.add(chooseImageBtn);
        imagePickerPanel.add(selectedImageLabel);
        formPanel.add(imagePickerPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Name
        addFormLabel(formPanel, "Recipe Name");
        JTextField nameField = createFormField();
        nameField.setText(recipe.getName());
        formPanel.add(nameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Instructions
        addFormLabel(formPanel, "Instructions");
        JTextArea instructionsArea = new JTextArea(5, 40);
        instructionsArea.setText(recipe.getInstructions());
        instructionsArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane instScroll = new JScrollPane(instructionsArea);
        instScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        instScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(instScroll);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Time & Servings
        JPanel detailsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField timeField = createFormField();
        timeField.setText(String.valueOf(recipe.getPrepTime()));
        JTextField servingsField = createFormField();
        servingsField.setText(String.valueOf(recipe.getServings()));

        JPanel timePanel = new JPanel(new BorderLayout(0, 5));
        timePanel.setOpaque(false);
        JLabel timeLabel = new JLabel("Prep Time (min)");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timePanel.add(timeLabel, BorderLayout.NORTH);
        timePanel.add(timeField, BorderLayout.CENTER);

        JPanel servPanel = new JPanel(new BorderLayout(0, 5));
        servPanel.setOpaque(false);
        JLabel servLabel = new JLabel("Servings");
        servLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        servPanel.add(servLabel, BorderLayout.NORTH);
        servPanel.add(servingsField, BorderLayout.CENTER);

        detailsPanel.add(timePanel);
        detailsPanel.add(servPanel);
        formPanel.add(detailsPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Save button
        JButton saveBtn = createRoundButton("Save Changes", PRIMARY_RUST);
        saveBtn.setPreferredSize(new Dimension(200, 50));
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String instructions = instructionsArea.getText().trim();
                int prepTime = Integer.parseInt(timeField.getText().trim());
                int servings = Integer.parseInt(servingsField.getText().trim());

                if (name.isEmpty() || instructions.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields!");
                    return;
                }

                recipe.setName(name);
                recipe.setInstructions(instructions);
                recipe.setPrepTime(prepTime);
                recipe.setServings(servings);

                // Handle image change
                if (selectedImagePath[0] != null && !selectedImagePath[0].equals(recipe.getImagePath())) {
                    if (!selectedImagePath[0].isEmpty() && !selectedImagePath[0].startsWith(IMAGES_FOLDER)) {
                        String destPath = copyImageToFolder(selectedImagePath[0], name);
                        recipe.setImagePath(destPath);
                    }
                }

                recipeService.updateRecipe(recipe);

                JOptionPane.showMessageDialog(dialog, "Recipe updated successfully!");
                dialog.dispose();
                loadRecipes();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for time and servings!");
            }
        });
        formPanel.add(saveBtn);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    //  ADMIN PANEL
    private void showAdminPanel() {
        JDialog dialog = new JDialog(this, "Admin Panel", true);
        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gradient = new GradientPaint(
                        0, 0, DANGER_RED,
                        getWidth(), getHeight(), new Color(180, 50, 50)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(1000, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel titleLabel = new JLabel("Admin Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JButton closeBtn = createRoundButton("Close", new Color(255, 255, 255, 50));
        closeBtn.setPreferredSize(new Dimension(100, 40));
        closeBtn.addActionListener(e -> dialog.dispose());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(closeBtn, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Manage Users Tab
        JPanel usersPanel = createManageUsersPanel(dialog);
        tabbedPane.addTab("Manage Users", usersPanel);

        // Manage Recipes Tab
        JPanel recipesPanel = createManageRecipesPanel(dialog);
        tabbedPane.addTab("Manage Recipes", recipesPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createManageUsersPanel(JDialog parentDialog) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        List<User> users = recipeService.getAllUsers();

        JPanel usersListPanel = new JPanel();
        usersListPanel.setLayout(new BoxLayout(usersListPanel, BoxLayout.Y_AXIS));
        usersListPanel.setBackground(Color.WHITE);

        for (User user : users) {
            JPanel userRow = new JPanel(new BorderLayout(15, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.dispose();
                }
            };
            userRow.setBackground(new Color(250, 248, 245));
            userRow.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
            userRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
            userRow.setOpaque(false);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);

            JLabel nameLabel = new JLabel(user.getFullName());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            nameLabel.setForeground(TEXT_DARK);

            JLabel usernameLabel = new JLabel("user: " + user.getUsername() + "  |  ID: " + user.getUserId());
            usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            usernameLabel.setForeground(TEXT_SECONDARY);

            infoPanel.add(nameLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            infoPanel.add(usernameLabel);

            JButton deleteBtn = createSmallButton("Delete User", DANGER_RED);
            deleteBtn.setPreferredSize(new Dimension(110, 35));
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(parentDialog,
                        "Are you sure you want to delete user '" + user.getUsername() + "'?\n" +
                                "This will also delete all their recipes and reviews!",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    recipeService.deleteUser(user.getUsername());
                    parentDialog.dispose();
                    loadRecipes();
                    showAdminPanel();
                }
            });

            userRow.add(infoPanel, BorderLayout.CENTER);
            userRow.add(deleteBtn, BorderLayout.EAST);

            usersListPanel.add(userRow);
            usersListPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        JScrollPane scrollPane = new JScrollPane(usersListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createManageRecipesPanel(JDialog parentDialog) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        List<Recipe> recipes = recipeService.getAllRecipes();

        JPanel recipesListPanel = new JPanel();
        recipesListPanel.setLayout(new BoxLayout(recipesListPanel, BoxLayout.Y_AXIS));
        recipesListPanel.setBackground(Color.WHITE);

        for (Recipe recipe : recipes) {
            JPanel row = createAdminRecipeRow(recipe, parentDialog);
            recipesListPanel.add(row);
            recipesListPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        JScrollPane scrollPane = new JScrollPane(recipesListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAdminRecipeRow(Recipe recipe, JDialog parentDialog) {
        JPanel row = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        row.setBackground(new Color(250, 248, 245));
        row.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        row.setOpaque(false);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(recipe.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_DARK);

        JLabel detailsLabel = new JLabel(recipe.getCategory() + "  |  " + recipe.getPrepTime() + " min  |  " + recipe.getServings() + " servings");
        detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsLabel.setForeground(TEXT_SECONDARY);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        infoPanel.add(detailsLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        JButton editBtn = createSmallButton("Edit", ACCENT_GOLD);
        editBtn.addActionListener(e -> {
            parentDialog.dispose();
            showEditRecipeDialog(recipe);
        });

        JButton deleteBtn = createSmallButton("Delete", DANGER_RED);
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(parentDialog,
                    "Are you sure you want to delete '" + recipe.getName() + "'?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                recipeService.deleteRecipe(recipe.getId());
                parentDialog.dispose();
                loadRecipes();
                showAdminPanel();
            }
        });

        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        row.add(infoPanel, BorderLayout.CENTER);
        row.add(buttonPanel, BorderLayout.EAST);

        return row;
    }

    //  LOGOUT
    private void logout() {
        JDialog dialog = new JDialog(this, "Confirm Logout", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Title
        JLabel titleLabel = new JLabel("Confirm Logout");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_RUST);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Message
        JLabel messageLabel = new JLabel("Are you sure you want to logout?");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messageLabel.setForeground(TEXT_DARK);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(messageLabel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        JButton yesBtn = createRoundButton("Yes", PRIMARY_RUST);
        yesBtn.setPreferredSize(new Dimension(100, 40));
        yesBtn.addActionListener(e -> {
            dialog.dispose();
            dispose();
            SwingUtilities.invokeLater(() -> {
                Login login = new Login();
                login.setVisible(true);
            });
        });

        JButton noBtn = createOutlineButton("No");
        noBtn.setPreferredSize(new Dimension(100, 40));
        noBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(yesBtn);
        buttonPanel.add(noBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setVisible(true);
    }

    private JTextField createWarmSearchField() {
        JTextField field = new JTextField("Search recipes...") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 26, 26);

                if (isFocusOwner()) {
                    g2.setColor(PRIMARY_RUST);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 26, 26);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setForeground(TEXT_SECONDARY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        field.setOpaque(false);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals("Search recipes...")) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(TEXT_SECONDARY);
                    field.setText("Search recipes...");
                }
            }
        });

        field.addActionListener(e -> performSearch());

        return field;
    }

    private void loadRecipes() {
        recipesContainer.removeAll();
        currentRecipes = recipeService.getAllRecipes();

        for (Recipe recipe : currentRecipes) {
            JPanel recipeCard = createRecipeCard(recipe);
            recipesContainer.add(recipeCard);
        }

        recipesContainer.revalidate();
        recipesContainer.repaint();
    }

    private JPanel createRecipeCard(Recipe recipe) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 18, 18);

                g2.dispose();
            }
        };

        card.setLayout(new BorderLayout());
        card.setOpaque(false);

        card.setPreferredSize(new Dimension(380, 400));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showRecipeDetails(recipe);
            }
        });

        JPanel imagePanel = new JPanel() {
            private BufferedImage img = loadRecipeImage(recipe);

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (img != null) {
                    // Draw actual image (fills the squarer area nicely)
                    g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                    g2.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Draw placeholder
                    Color placeholderColor = getPlaceholderColor(recipe.getId());
                    GradientPaint gradient = new GradientPaint(
                            0, 0, placeholderColor,
                            getWidth(), getHeight(), placeholderColor.darker()
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                    g2.setColor(new Color(255, 255, 255, 150));
                    // Slightly smaller emoji for smaller card
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
                    FontMetrics fm = g2.getFontMetrics();
                    String emoji = getCategoryEmoji(recipe.getCategory());
                    int x = (getWidth() - fm.stringWidth(emoji)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(emoji, x, y);
                }

                g2.dispose();
            }
        };

        imagePanel.setPreferredSize(new Dimension(380, 280));
        imagePanel.setOpaque(false);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 18, 18, 18));

        JLabel nameLabel = new JLabel(recipe.getName());

        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(TEXT_DARK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


        JLabel categoryLabel = createMetaLabel(recipe.getCategory(), ACCENT_GOLD);
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Time panel with prep, cook, and total time
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        timePanel.setOpaque(false);
        timePanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        timePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel prepLabel = createMetaLabel("Prep: " + formatTime(recipe.getPrepTime()), TEXT_DARK);
        prepLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel cookLabel = createMetaLabel("Cook: " + formatTime(recipe.getCookTime()), TEXT_DARK);
        cookLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel totalLabel = createMetaLabel("Total: " + formatTime(recipe.getTotalTime()), TEXT_DARK);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        timePanel.add(prepLabel);
        timePanel.add(cookLabel);
        timePanel.add(totalLabel);

        // Servings
        JLabel servingsLabel = createMetaLabel(recipe.getServings() + " servings", TEXT_SECONDARY);
        servingsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        servingsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        servingsLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        infoPanel.add(categoryLabel);
        infoPanel.add(timePanel);
        infoPanel.add(servingsLabel);

        card.add(imagePanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);

        return card;
    }

    // Load recipe image from file
    private BufferedImage loadRecipeImage(Recipe recipe) {
        if (recipe.hasImage()) {
            try {
                File imgFile = new File(recipe.getImagePath());
                if (imgFile.exists()) {
                    return ImageIO.read(imgFile);
                }
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

    // Get placeholder color based on recipe ID
    private Color getPlaceholderColor(int recipeId) {
        return PLACEHOLDER_COLORS[Math.abs(recipeId) % PLACEHOLDER_COLORS.length];
    }

    // Copy image to recipe_images folder
    private String copyImageToFolder(String sourcePath, String recipeName) {
        try {
            File sourceFile = new File(sourcePath);
            String extension = "";
            int i = sourcePath.lastIndexOf('.');
            if (i > 0) {
                extension = sourcePath.substring(i);
            }

            String safeName = recipeName.replaceAll("[^a-zA-Z0-9]", "_");
            String destFileName = safeName + "_" + System.currentTimeMillis() + extension;
            Path destPath = Paths.get(IMAGES_FOLDER, destFileName);
            Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
            return destPath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private JLabel createMetaLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(color);
        return label;
    }

    // Helper method to format minutes into hours and minutes
    private String formatTime(int minutes) {
        if (minutes < 60) {
            return minutes + " min";
        } else if (minutes % 60 == 0) {
            int hours = minutes / 60;
            return hours + (hours == 1 ? " hr" : " hrs");
        } else {
            int hours = minutes / 60;
            int mins = minutes % 60;
            return hours + (hours == 1 ? " hr " : " hrs ") + mins + " min";
        }
    }

    private String getCategoryEmoji(String category) {
        if (category == null) return "🍽️";
        switch (category.toLowerCase()) {
            case "italian": return "🍝";
            case "breakfast": return "🥞";
            case "desserts": case "dessert": return "🍰";
            case "asian": return "🍜";
            case "seafood": return "🍤";
            case "vegetarian": case "vegan": return "🥗";
            case "bbq": case "grill": return "🍖";
            case "soups": case "soup": return "🍲";
            case "salads": case "salad": return "🥗";
            case "bakery": case "baking": return "🍞";
            default: return "🍽️";
        }
    }

    private JButton createRoundButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentColor;
                if (getModel().isPressed()) {
                    currentColor = bgColor.darker();
                } else if (getModel().isRollover()) {
                    currentColor = bgColor.brighter();
                } else {
                    currentColor = bgColor;
                }

                g2.setColor(currentColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JButton createOutlineButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(new Color(245, 245, 245));
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                g2.setColor(PRIMARY_RUST);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 25, 25);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(PRIMARY_RUST);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JButton createSmallButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentColor;
                if (getModel().isPressed()) {
                    currentColor = bgColor.darker();
                } else if (getModel().isRollover()) {
                    currentColor = bgColor.brighter();
                } else {
                    currentColor = bgColor;
                }

                g2.setColor(currentColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(90, 35));

        return button;
    }

    private JButton createTextButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(PRIMARY_RUST);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setForeground(PRIMARY_LIGHT);
            }
            public void mouseExited(MouseEvent e) {
                button.setForeground(PRIMARY_RUST);
            }
        });

        return button;
    }

    private void performSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty() || searchText.equals("Search recipes...")) {
            loadRecipes();
            return;
        }

        recipesContainer.removeAll();
        List<Recipe> filteredRecipes = recipeService.searchRecipes(searchText);

        if (filteredRecipes.isEmpty()) {
            JLabel noResults = new JLabel("No recipes found for: " + searchText);
            noResults.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noResults.setForeground(TEXT_SECONDARY);
            recipesContainer.add(noResults);
        } else {
            for (Recipe recipe : filteredRecipes) {
                JPanel recipeCard = createRecipeCard(recipe);
                recipesContainer.add(recipeCard);
            }
        }

        recipesContainer.revalidate();
        recipesContainer.repaint();
    }

    //  UPDATED RECIPE DETAILS
    private void showRecipeDetails(Recipe recipe) {
        JDialog dialog = new JDialog(this, recipe.getName(), true);
        dialog.setSize(1000, 850);
        dialog.setMinimumSize(new Dimension(800, 600));
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(232, 221, 203),
                        getWidth(), getHeight(), new Color(245, 230, 210)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());

                BufferedImage img = loadRecipeImage(recipe);

                if (img != null) {
                    //scale image
                    double scale = Math.min((double)getWidth() / img.getWidth(), (double)getHeight() / img.getHeight());
                    int scaledW = (int)(img.getWidth() * scale);
                    int scaledH = (int)(img.getHeight() * scale);

                    // Center the image
                    int x = (getWidth() - scaledW) / 2;
                    int y = (getHeight() - scaledH) / 2;

                    g2.setColor(new Color(0,0,0,30));
                    g2.fillRoundRect(x+4, y+4, scaledW, scaledH, 10, 10);

                    g2.drawImage(img, x, y, scaledW, scaledH, this);
                }
                else {
                    // if no image: Emoji
                    g2.setColor(PRIMARY_RUST);
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
                    FontMetrics fm = g2.getFontMetrics();
                    String emoji = getCategoryEmoji(recipe.getCategory());
                    int x = (getWidth() - fm.stringWidth(emoji)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent() - 20;
                    g2.drawString(emoji, x, y);
                }

                g2.dispose();
            }
        };

        headerPanel.setPreferredSize(new Dimension(1000, 380));

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Back and Edit buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backBtn = createRoundButton("← Back", PRIMARY_RUST);
        backBtn.setPreferredSize(new Dimension(100, 40));
        backBtn.addActionListener(e -> dialog.dispose());
        buttonRow.add(backBtn);

        // Show edit/delete buttons only if user owns this recipe or is admin
        if (recipe.getUserId() == currentUser.getUserId() || currentUser.isAdmin()) {
            JButton editBtn = createRoundButton("Edit", ACCENT_GOLD);
            editBtn.setPreferredSize(new Dimension(80, 40));
            editBtn.addActionListener(e -> {
                dialog.dispose();
                showEditRecipeDialog(recipe);
            });
            buttonRow.add(editBtn);

            JButton deleteBtn = createRoundButton("Delete", DANGER_RED);
            deleteBtn.setPreferredSize(new Dimension(90, 40));
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Are you sure you want to delete this recipe?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    recipeService.deleteRecipe(recipe.getId());
                    dialog.dispose();
                    loadRecipes();
                }
            });
            buttonRow.add(deleteBtn);
        }

        contentPanel.add(buttonRow);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Recipe name
        JLabel nameLabel = new JLabel(recipe.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        nameLabel.setForeground(TEXT_DARK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(nameLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        metaPanel.setOpaque(false);
        metaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel categoryLabel = createMetaLabel("Category: " + recipe.getCategory(), PRIMARY_RUST);
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        metaPanel.add(categoryLabel);

        JLabel prepLabel = createMetaLabel("Prep: " + formatTime(recipe.getPrepTime()), TEXT_DARK);
        prepLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        metaPanel.add(prepLabel);

        JLabel cookLabel = createMetaLabel("Cook: " + formatTime(recipe.getCookTime()), TEXT_DARK);
        cookLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        metaPanel.add(cookLabel);

        JLabel totalLabel = createMetaLabel("Total: " + formatTime(recipe.getTotalTime()), TEXT_DARK);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        metaPanel.add(totalLabel);

        JLabel servingsLabel = createMetaLabel(recipe.getServings() + " servings", TEXT_DARK);
        servingsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        metaPanel.add(servingsLabel);

        contentPanel.add(metaPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Ingredients
        JLabel ingredientsHeader = new JLabel("Ingredients");
        ingredientsHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        ingredientsHeader.setForeground(PRIMARY_RUST);
        ingredientsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(ingredientsHeader);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        for (String ingredient : recipe.getIngredients()) {
            JLabel ingLabel = new JLabel("• " + ingredient);
            ingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            ingLabel.setForeground(TEXT_DARK);
            ingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(ingLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Instructions
        JLabel instructionsHeader = new JLabel("Instructions");
        instructionsHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        instructionsHeader.setForeground(PRIMARY_RUST);
        instructionsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(instructionsHeader);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Styled instructions panel
        JPanel instructionsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(250, 247, 242));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(230, 220, 205));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        instructionsPanel.setLayout(new BorderLayout());
        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        instructionsPanel.setOpaque(false);
        instructionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea instructionsArea = new JTextArea(recipe.getInstructions());
        instructionsArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        instructionsArea.setForeground(TEXT_DARK);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setEditable(false);
        instructionsArea.setOpaque(false);
        instructionsArea.setBorder(null);

        instructionsPanel.add(instructionsArea, BorderLayout.CENTER);
        contentPanel.add(instructionsPanel);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Reviews Section
        JLabel reviewsHeader = new JLabel("Reviews");
        reviewsHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        reviewsHeader.setForeground(PRIMARY_RUST);
        reviewsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(reviewsHeader);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Add Review button
        JButton addReviewBtn = createRoundButton("+ Add Review", SUCCESS_GREEN);
        addReviewBtn.setPreferredSize(new Dimension(140, 40));
        addReviewBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addReviewBtn.addActionListener(e -> showAddReviewDialog(recipe, dialog));
        contentPanel.add(addReviewBtn);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Display reviews
        List<Review> reviews = recipeService.getReviewsForRecipe(recipe.getId());
        if (reviews.isEmpty()) {
            JLabel noReviews = new JLabel("No reviews yet. Be the first to review!");
            noReviews.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            noReviews.setForeground(TEXT_SECONDARY);
            noReviews.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(noReviews);
        } else {
            for (Review review : reviews) {
                JPanel reviewPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        g2.dispose();
                    }
                };
                reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
                reviewPanel.setBackground(new Color(250, 248, 245));
                reviewPanel.setBorder(BorderFactory.createEmptyBorder(15, 18, 15, 18));
                reviewPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                reviewPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
                reviewPanel.setOpaque(false);

                JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                headerRow.setOpaque(false);
                headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel reviewUser = new JLabel(review.getUsername());
                reviewUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
                reviewUser.setForeground(TEXT_DARK);

                JLabel ratingBadge = new JLabel(" " + review.getRating() + "/5 ");
                ratingBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
                ratingBadge.setForeground(Color.WHITE);
                ratingBadge.setOpaque(true);
                ratingBadge.setBackground(ACCENT_GOLD);

                headerRow.add(reviewUser);
                headerRow.add(ratingBadge);

                JLabel reviewComment = new JLabel(review.getComment());
                reviewComment.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                reviewComment.setForeground(TEXT_SECONDARY);
                reviewComment.setAlignmentX(Component.LEFT_ALIGNMENT);

                reviewPanel.add(headerRow);
                reviewPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                reviewPanel.add(reviewComment);

                contentPanel.add(reviewPanel);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showAddReviewDialog(Recipe recipe, JDialog parentDialog) {
        JDialog dialog = new JDialog(this, "Add Review", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Rate " + recipe.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_RUST);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Rating
        JLabel ratingLabel = new JLabel("Rating (1-5 stars):");
        ratingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(ratingLabel);

        JComboBox<Integer> ratingCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        ratingCombo.setSelectedItem(5);
        ratingCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        ratingCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(ratingCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Comment
        JLabel commentLabel = new JLabel("Your Review:");
        commentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(commentLabel);

        JTextArea commentArea = new JTextArea(4, 30);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane commentScroll = new JScrollPane(commentArea);
        commentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(commentScroll);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Submit button
        JButton submitBtn = createRoundButton("Submit Review", SUCCESS_GREEN);
        submitBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        submitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> {
            int rating = (Integer) ratingCombo.getSelectedItem();
            String comment = commentArea.getText().trim();

            if (comment.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please write a review comment!");
                return;
            }

            recipeService.addReview(currentUser.getUserId(), recipe.getId(), rating, comment);
            JOptionPane.showMessageDialog(dialog, "Review added successfully!");
            dialog.dispose();
            parentDialog.dispose();
            showRecipeDetails(recipe);
        });
        mainPanel.add(submitBtn);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showAddRecipeDialog() {
        JDialog dialog = new JDialog(this, "Add New Recipe", true);
        dialog.setSize(700, 800);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Add New Recipe");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_RUST);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        // Image picker
        addFormLabel(formPanel, "Recipe Image (optional)");
        JPanel imagePickerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        imagePickerPanel.setOpaque(false);
        imagePickerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imagePickerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JLabel selectedImageLabel = new JLabel("No image selected");
        selectedImageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        selectedImageLabel.setForeground(TEXT_SECONDARY);

        final String[] selectedImagePath = {""};

        JButton chooseImageBtn = createSmallButton("Choose Image", PRIMARY_LIGHT);
        chooseImageBtn.setPreferredSize(new Dimension(120, 35));
        chooseImageBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedImagePath[0] = selectedFile.getAbsolutePath();
                selectedImageLabel.setText(selectedFile.getName());
            }
        });

        imagePickerPanel.add(chooseImageBtn);
        imagePickerPanel.add(selectedImageLabel);
        formPanel.add(imagePickerPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Name
        addFormLabel(formPanel, "Recipe Name");
        JTextField nameField = createFormField();
        formPanel.add(nameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Category
        addFormLabel(formPanel, "Category");
        JTextField categoryField = createFormField();
        formPanel.add(categoryField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Ingredients
        addFormLabel(formPanel, "Ingredients (comma separated, e.g., '200 g flour, 2 pieces eggs')");
        JTextArea ingredientsArea = new JTextArea(3, 40);
        ingredientsArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        ingredientsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane ingScroll = new JScrollPane(ingredientsArea);
        ingScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        ingScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(ingScroll);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Instructions
        addFormLabel(formPanel, "Instructions");
        JTextArea instructionsArea = new JTextArea(5, 40);
        instructionsArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane instScroll = new JScrollPane(instructionsArea);
        instScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        instScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(instScroll);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Time & Servings
        JPanel detailsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField timeField = createFormField();
        timeField.setText("30");
        JTextField servingsField = createFormField();
        servingsField.setText("4");

        JPanel timePanel = new JPanel(new BorderLayout(0, 5));
        timePanel.setOpaque(false);
        JLabel timeLabel = new JLabel("Prep Time (min)");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timePanel.add(timeLabel, BorderLayout.NORTH);
        timePanel.add(timeField, BorderLayout.CENTER);

        JPanel servPanel = new JPanel(new BorderLayout(0, 5));
        servPanel.setOpaque(false);
        JLabel servLabel = new JLabel("Servings");
        servLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        servPanel.add(servLabel, BorderLayout.NORTH);
        servPanel.add(servingsField, BorderLayout.CENTER);

        detailsPanel.add(timePanel);
        detailsPanel.add(servPanel);
        formPanel.add(detailsPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Save button
        JButton saveBtn = createRoundButton("Save Recipe", PRIMARY_RUST);
        saveBtn.setPreferredSize(new Dimension(200, 50));
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String category = categoryField.getText().trim();
                String ingredientsText = ingredientsArea.getText().trim();
                String instructions = instructionsArea.getText().trim();

                if (name.isEmpty() || category.isEmpty() || ingredientsText.isEmpty() || instructions.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields!");
                    return;
                }

                String[] ingredientsArray = ingredientsText.split(",");
                List<String> ingredientsList = new ArrayList<>();
                for (String ing : ingredientsArray) {
                    ingredientsList.add(ing.trim());
                }

                int prepTime = Integer.parseInt(timeField.getText());
                int servings = Integer.parseInt(servingsField.getText());

                Recipe newRecipe = new Recipe(name, category, ingredientsList,
                        instructions, prepTime, servings);

                // Copy image if selected
                if (!selectedImagePath[0].isEmpty()) {
                    String destPath = copyImageToFolder(selectedImagePath[0], name);
                    newRecipe.setImagePath(destPath);
                }

                recipeService.addRecipe(newRecipe, currentUser.getUserId());

                JOptionPane.showMessageDialog(dialog, "Recipe added successfully!");
                dialog.dispose();
                loadRecipes();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for time and servings!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        formPanel.add(saveBtn);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JTextField createFormField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private void addFormLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            RecipeApp app = new RecipeApp();
            app.setVisible(true);
        });
    }
}