package model;

import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private String category;
    private List<String> ingredients;
    private String instructions;
    private int prepTime;
    private int cookTime;
    private int totalTime;
    private int servings;
    private int userId;
    private String imagePath;

    public Recipe(int id, String name, String category, List<String> ingredients,
                  String instructions, int prepTime, int servings) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.prepTime = prepTime;
        this.cookTime = 0;
        this.totalTime = 0;
        this.servings = servings;
        this.userId = 0;
        this.imagePath = "";
    }

    // Constructor for new recipes (without ID)
    public Recipe(String name, String category, List<String> ingredients,
                  String instructions, int prepTime, int servings) {
        this(0, name, category, ingredients, instructions, prepTime, servings);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public List<String> getIngredients() { return ingredients; }
    public String getInstructions() { return instructions; }
    public int getPrepTime() { return prepTime; }
    public int getCookTime() { return cookTime; } // New Getter
    public int getServings() { return servings; }
    public int getUserId() { return userId; }
    public String getImagePath() { return imagePath; }

    public int getTotalTime() {
        return totalTime;
    }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public void setPrepTime(int prepTime) { this.prepTime = prepTime; }
    public void setCookTime(int cookTime) { this.cookTime = cookTime; }
    public void setTotalTime(int totalTime) { this.totalTime = totalTime; }
    public void setServings(int servings) { this.servings = servings; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public boolean hasImage() {
        return imagePath != null && !imagePath.isEmpty();
    }
}