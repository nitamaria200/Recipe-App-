package model;

import service.DataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Admin extends Account {

    public Admin(String username, String fullName) {
        super(username, fullName);
    }

    @Override
    public String getRole() {
        return "Administrator";
    }

    public boolean isAdmin() {
        return true;
    }

    public void deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM Users WHERE Username = ?";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("User '" + username + "' deleted successfully.");
                System.out.println("   (All their recipes, reviews automatically deleted by CASCADE)");
            } else {
                System.out.println("User '" + username + "' not found.");
            }
        }
    }

    public void deleteRecipe(int recipeId) throws SQLException {
        String sql = "DELETE FROM Recipes WHERE RecipeID = ?";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipeId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Recipe deleted successfully.");
                System.out.println("   (Ingredients and reviews automatically deleted by CASCADE)");
            } else {
                System.out.println("Recipe not found.");
            }
        }
    }

    public void updateRecipeDetails(Recipe recipe) throws SQLException {
        String sql = "UPDATE Recipes SET RecipeName=?, Instructions=?, PreparationTime=?, Servings=?, DifficultyLevel=? WHERE RecipeID=?";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recipe.getName());
            stmt.setString(2, recipe.getInstructions());
            stmt.setInt(3, recipe.getPrepTime());
            stmt.setInt(4, recipe.getServings());
            stmt.setString(5, "Easy"); // Default difficulty
            stmt.setInt(6, recipe.getId());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Recipe updated successfully.");
            } else {
                System.out.println("Recipe not found.");
            }
        }
    }

    public List<String> getAllUsernames() {
        List<String> users = new ArrayList<>();
        String sql = "SELECT Username FROM Users WHERE Username != 'admin' ORDER BY Username";

        try (Connection conn = DataBase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(rs.getString("Username"));
            }

        } catch (SQLException e) {
            System.out.println("Error getting usernames: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT RecipeID, RecipeName FROM Recipes ORDER BY RecipeName";

        try (Connection conn = DataBase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("RecipeID");
                String name = rs.getString("RecipeName");
                // Create minimal Recipe object (can be expanded if needed)
                Recipe r = new Recipe(name, "", new ArrayList<>(), "", 0, 0);
                recipes.add(r);
            }

        } catch (SQLException e) {
            System.out.println("Error getting recipes: " + e.getMessage());
            e.printStackTrace();
        }
        return recipes;
    }
}