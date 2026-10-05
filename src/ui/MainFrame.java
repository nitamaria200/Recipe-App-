package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame {

    public static final Color PRIMARY_COLOR = new Color(183, 85, 40);        // Terracotta/rust
    public static final Color PRIMARY_DARK = new Color(153, 65, 30);         // Darker rust
    public static final Color PRIMARY_LIGHT = new Color(203, 105, 60);       // Lighter rust

    public static final Color ACCENT_COLOR = new Color(212, 175, 55);        // Golden
    public static final Color BACKGROUND_WARM = new Color(232, 221, 203);    // Warm beige
    public static final Color CARD_BACKGROUND = new Color(255, 251, 245);    // Cream/off-white

    public static final Color TEXT_PRIMARY = new Color(51, 51, 51);          // Dark gray
    public static final Color TEXT_SECONDARY = new Color(102, 102, 102);     // Medium gray
    public static final Color TEXT_LIGHT = new Color(153, 153, 153);         // Light gray

    public static final Color ERROR_COLOR = new Color(211, 47, 47);          // Red
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);        // Green
    public static final Color BORDER_COLOR = new Color(224, 224, 224);       // Light border

    public static final Color BACKGROUND_COLOR = CARD_BACKGROUND;
    public static final Color TEXT_COLOR = TEXT_PRIMARY;

    // fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font MONO_FONT = new Font("Consolas", Font.PLAIN, 12);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BRAND_FONT = new Font("Segoe UI", Font.BOLD, 48);
    public static final Font TAGLINE_FONT = new Font("Segoe UI", Font.PLAIN, 20);

    // dimensions
    public static final Dimension LOGIN_SIZE = new Dimension(1000, 650);
    public static final Dimension REGISTER_SIZE = new Dimension(450, 500);
    public static final Dimension MAIN_APP_SIZE = new Dimension(1200, 700);
    public static final Dimension ADD_RECIPE_SIZE = new Dimension(600, 500);

    // padding
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 20;
    public static final int PADDING_XLARGE = 30;

    // border radius
    public static final int RADIUS_SMALL = 8;
    public static final int RADIUS_MEDIUM = 12;
    public static final int RADIUS_LARGE = 20;


    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);

        button.setOpaque(true);
        button.setBorderPainted(false);

        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_LIGHT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    // Outlined button
    public static JButton createOutlinedButton(String text, Icon icon) {
        JButton button = new JButton(text, icon);
        button.setFont(NORMAL_FONT);
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(Color.WHITE);
        button.setOpaque(true);

        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);

        return button;
    }

    // Text field
    public static JTextField createModernTextField(String placeholder, Icon icon) {
        JTextField field = new JTextField();
        field.setFont(NORMAL_FONT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(Color.WHITE);

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, icon != null ? 45 : 15, 12, 15)
        ));

        field.setPreferredSize(new Dimension(350, 50));

        field.setText(placeholder);
        field.setForeground(TEXT_LIGHT);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                    field.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                            BorderFactory.createEmptyBorder(11, icon != null ? 44 : 14, 11, 14)
                    ));
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(TEXT_LIGHT);
                    field.setText(placeholder);
                    field.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR),
                            BorderFactory.createEmptyBorder(12, icon != null ? 45 : 15, 12, 15)
                    ));
                }
            }
        });

        return field;
    }

    // Password field
    public static JPasswordField createModernPasswordField(String placeholder, Icon icon) {
        JPasswordField field = new JPasswordField();
        field.setFont(NORMAL_FONT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(Color.WHITE);

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, icon != null ? 45 : 15, 12, 15)
        ));

        field.setPreferredSize(new Dimension(350, 50));

        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.setForeground(TEXT_LIGHT);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                    field.setEchoChar('•');
                    field.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                            BorderFactory.createEmptyBorder(11, icon != null ? 44 : 14, 11, 14)
                    ));
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getPassword().length == 0) {
                    field.setForeground(TEXT_LIGHT);
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR),
                            BorderFactory.createEmptyBorder(12, icon != null ? 45 : 15, 12, 15)
                    ));
                }
            }
        });

        return field;
    }

    public static JButton createTextLink(String text) {
        JButton button = new JButton(text);
        button.setFont(SMALL_FONT);
        button.setForeground(TEXT_SECONDARY);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(PRIMARY_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(TEXT_SECONDARY);
            }
        });

        return button;
    }

    public static JPanel createRoundedPanel(Color bgColor) {
        JPanel panel = new JPanel();
        panel.setBackground(bgColor);
        return panel;
    }

    public static JButton createSecondaryButton(String text) {
        return createOutlinedButton(text, null);
    }

    public static JTextField createTextField(int columns) {
        return createModernTextField("", null);
    }

    public static JPasswordField createPasswordField(int columns) {
        return createModernPasswordField("", null);
    }

    public static JTextArea createTextArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setFont(MONO_FONT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return area;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(NORMAL_FONT);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUBTITLE_FONT);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    public static JPanel createPaddedPanel(int padding) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        panel.setBackground(CARD_BACKGROUND);
        return panel;
    }

    public static void showErrorDialog(Component parent, String message, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parent);
        dialog.setUndecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ERROR_COLOR, 2),
                BorderFactory.createEmptyBorder(PADDING_XLARGE, PADDING_XLARGE, PADDING_XLARGE, PADDING_XLARGE)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ERROR_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel messageLabel = new JLabel("<html><div style='text-align: center; padding: 20px;'>" + message + "</div></html>");
        messageLabel.setFont(NORMAL_FONT);
        messageLabel.setForeground(TEXT_SECONDARY);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(messageLabel, BorderLayout.CENTER);

        // === FIXED: Error OK Button ===
        JButton okButton = new JButton("OK");
        okButton.setBackground(ERROR_COLOR);
        okButton.setForeground(Color.WHITE);
        okButton.setFont(BUTTON_FONT);

        // Fix color visibility here too
        okButton.setOpaque(true);
        okButton.setBorderPainted(false);

        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.setPreferredSize(new Dimension(120, 42));
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    public static void showSuccessDialog(Component parent, String message, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parent);
        dialog.setUndecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(PADDING_XLARGE, PADDING_XLARGE, PADDING_XLARGE, PADDING_XLARGE)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel messageLabel = new JLabel("<html><div style='text-align: center; padding: 20px;'>" + message + "</div></html>");
        messageLabel.setFont(NORMAL_FONT);
        messageLabel.setForeground(TEXT_SECONDARY);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(messageLabel, BorderLayout.CENTER);

        JButton okButton = createPrimaryButton("OK");
        okButton.setPreferredSize(new Dimension(120, 42));
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    public static void showWarningDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void configureFrame(JFrame frame, String title, Dimension size) {
        frame.setTitle(title);
        frame.setSize(size);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }
}