package service;

import model.*;
import java.sql.*;
import java.util.*;

public class RecipeService {

    public RecipeService() {}

    // GET ALL RECIPES
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        String query = "SELECT r.RecipeID, r.RecipeName, c.CategoryName, r.Instructions, " +
                "r.PreparationTime, r.CookTime, r.TotalTime, r.Servings, r.UserID, r.ImagePath " +
                "FROM Recipes r " +
                "JOIN Categories c ON r.CategoryID = c.CategoryID";

        try (Connection conn = DataBase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                recipes.add(mapResultSetToRecipe(rs, conn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    // SEARCH RECIPES
    public List<Recipe> searchRecipes(String keyword) {
        List<Recipe> recipes = new ArrayList<>();
        String query = "SELECT r.RecipeID, r.RecipeName, c.CategoryName, r.Instructions, " +
                "r.PreparationTime, r.CookTime, r.TotalTime, r.Servings, r.UserID, r.ImagePath " +
                "FROM Recipes r " +
                "JOIN Categories c ON r.CategoryID = c.CategoryID " +
                "WHERE r.RecipeName LIKE ? OR c.CategoryName LIKE ?";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs, conn));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    // GET RECIPES BY USER ID
    public List<Recipe> getRecipesByUserId(int userId) {
        List<Recipe> recipes = new ArrayList<>();
        String query = "SELECT r.RecipeID, r.RecipeName, c.CategoryName, r.Instructions, " +
                "r.PreparationTime, r.CookTime, r.TotalTime, r.Servings, r.UserID, r.ImagePath " +
                "FROM Recipes r " +
                "JOIN Categories c ON r.CategoryID = c.CategoryID " +
                "WHERE r.UserID = ?";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs, conn));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    // HELPER TO MAP DB ROW TO RECIPE OBJECT
    private Recipe mapResultSetToRecipe(ResultSet rs, Connection conn) throws SQLException {
        int id = rs.getInt("RecipeID");
        String name = rs.getString("RecipeName");
        String category = rs.getString("CategoryName");
        String instructions = rs.getString("Instructions");
        int prepTime = rs.getInt("PreparationTime");
        int cookTime = rs.getInt("CookTime");
        int totalTime = rs.getInt("TotalTime");
        int servings = rs.getInt("Servings");
        int userId = rs.getInt("UserID");
        String imagePath = rs.getString("ImagePath");

        List<String> ingredients = getIngredientsForRecipe(id, conn);

        Recipe recipe = new Recipe(id, name, category, ingredients, instructions, prepTime, servings);
        recipe.setCookTime(cookTime);
        recipe.setTotalTime(totalTime);
        recipe.setUserId(userId);
        recipe.setImagePath(imagePath != null ? imagePath : "");

        return recipe;
    }

    private List<String> getIngredientsForRecipe(int recipeId, Connection conn) {
        List<String> ingredients = new ArrayList<>();
        String query = "SELECT i.IngredientName, ri.Quantity, ri.Unit " +
                "FROM RecipeIngredients ri " +
                "JOIN Ingredients i ON ri.IngredientID = i.IngredientID " +
                "WHERE ri.RecipeID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recipeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double qty = rs.getDouble("Quantity");
                    String qtyStr = (qty == (int) qty) ? String.valueOf((int) qty) : String.valueOf(qty);
                    String line = qtyStr + " " + rs.getString("Unit") + " " + rs.getString("IngredientName");
                    ingredients.add(line);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    // ADD RECIPE
    public void addRecipe(Recipe recipe, int userId) {
        String sql = "INSERT INTO Recipes (RecipeName, Instructions, PreparationTime, CookTime, Servings, DifficultyLevel, UserID, CategoryID, ImagePath) " +
                "VALUES (?, ?, ?, ?, ?, 'Easy', ?, (SELECT TOP 1 CategoryID FROM Categories WHERE CategoryName = ?), ?)";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, recipe.getName());
            stmt.setString(2, recipe.getInstructions());
            stmt.setInt(3, recipe.getPrepTime());
            stmt.setInt(4, recipe.getCookTime());
            stmt.setInt(5, recipe.getServings());
            stmt.setInt(6, userId);
            stmt.setString(7, recipe.getCategory());
            stmt.setString(8, recipe.getImagePath());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UPDATE RECIPE
    public void updateRecipe(Recipe recipe) {
        String sql = "UPDATE Recipes SET RecipeName=?, Instructions=?, PreparationTime=?, CookTime=?, Servings=?, ImagePath=? WHERE RecipeID=?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recipe.getName());
            stmt.setString(2, recipe.getInstructions());
            stmt.setInt(3, recipe.getPrepTime());
            stmt.setInt(4, recipe.getCookTime());
            stmt.setInt(5, recipe.getServings());
            stmt.setString(6, recipe.getImagePath());
            stmt.setInt(7, recipe.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE RECIPE
    public void deleteRecipe(int recipeId) {
        try (Connection conn = DataBase.getConnection()) {
            // Delete dependencies first
            try (PreparedStatement s1 = conn.prepareStatement("DELETE FROM RecipeIngredients WHERE RecipeID=?")) {
                s1.setInt(1, recipeId);
                s1.executeUpdate();
            }
            try (PreparedStatement s2 = conn.prepareStatement("DELETE FROM Reviews WHERE RecipeID=?")) {
                s2.setInt(1, recipeId);
                s2.executeUpdate();
            }
            // Delete Recipe
            try (PreparedStatement s3 = conn.prepareStatement("DELETE FROM Recipes WHERE RecipeID=?")) {
                s3.setInt(1, recipeId);
                s3.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // GET REVIEWS
    public List<Review> getReviewsForRecipe(int recipeId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.ReviewID, r.Rating, r.Comment, r.ReviewDate, u.Username " +
                "FROM Reviews r JOIN Users u ON r.UserID = u.UserID WHERE r.RecipeID = ? ORDER BY r.ReviewDate DESC";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    reviews.add(new Review(
                            rs.getInt("ReviewID"), recipeId, rs.getString("Username"),
                            rs.getInt("Rating"), rs.getString("Comment"), rs.getTimestamp("ReviewDate")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return reviews;
    }

    // ADD REVIEW
    public void addReview(int userId, int recipeId, int rating, String comment) {
        String sql = "INSERT INTO Reviews (Rating, Comment, UserID, RecipeID) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating);
            stmt.setString(2, comment);
            stmt.setInt(3, userId);
            stmt.setInt(4, recipeId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // REVIEWS BY USER
    public List<Review> getReviewsByUserId(int userId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.ReviewID, r.Rating, r.Comment, r.ReviewDate, rc.RecipeName, rc.RecipeID " +
                "FROM Reviews r JOIN Recipes rc ON r.RecipeID = rc.RecipeID WHERE r.UserID = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    Review review = new Review(
                            rs.getInt("ReviewID"), rs.getInt("RecipeID"), "",
                            rs.getInt("Rating"), rs.getString("Comment"), rs.getTimestamp("ReviewDate")
                    );
                    review.setRecipeName(rs.getString("RecipeName"));
                    reviews.add(review);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return reviews;
    }

    public void deleteReview(int reviewId) {
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Reviews WHERE ReviewID=?")) {
            stmt.setInt(1, reviewId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // GET ALL USERS
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT UserID, Username, FullName, Password FROM Users";
        try (Connection conn = DataBase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"), rs.getString("FullName")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    // DELETE USER
    public void deleteUser(String username) {
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users WHERE Username=?")) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}