package model;

import java.sql.Timestamp;

public class Review {
    private int id;
    private int recipeId;
    private String username;
    private int rating;
    private String comment;
    private Timestamp date;
    private String recipeName; // For displaying in user's review list

    public Review(int id, int recipeId, String username, int rating, String comment, Timestamp date) {
        this.id = id;
        this.recipeId = recipeId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.recipeName = "";
    }

    // Getters
    public int getId() { return id; }
    public int getRecipeId() { return recipeId; }
    public String getUsername() { return username; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Timestamp getDate() { return date; }
    public String getRecipeName() { return recipeName; }

    // Setters
    public void setRecipeName(String recipeName) { this.recipeName = recipeName; }

    // Helper to display star rating (using text to avoid font issues)
    public String getStarRating() {
        return rating + "/5";
    }
}