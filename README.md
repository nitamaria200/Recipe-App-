# 🍳 RecipeApp

A desktop recipe app built with Java Swing and Microsoft SQL Server. It works as a shared digital cookbook: you can browse and search recipes, post your own with photos, and rate and review what other people have shared.

I built it to practice object-oriented design in a real application, with a proper separation between the UI, the application logic and the database.

## Features

**For users**

- Register, log in and log out
- Browse recipes with photos, preparation time and servings
- Search by name or category
- Create recipes with ingredients, step-by-step instructions and a photo, then edit them later
- Rate recipes from 1 to 5 stars and leave comments
- See your account, your recipes and your reviews in one place

**For administrators**

- View and delete user accounts
- Remove inappropriate recipes or reviews

## 📸 Screenshots

**Login and registration**

<p align="center">
  <img src="interface/Picture1.png" alt="Login window" width="45%">
  <img src="interface/Picture2.png" alt="Account registration window" width="45%">
</p>

**Browsing recipes**

<p align="center">
  <img src="interface/Picture4.png" alt="Main dashboard with recipe list" width="45%">
  <img src="interface/Picture5.png" alt="Recipe cards with photos" width="45%">
</p>

**Recipe details, ingredients and reviews**

<p align="center">
  <img src="interface/Picture6.png" alt="Recipe detail page" width="45%">
  <img src="interface/Picture7.png" alt="Reviews and star rating on a recipe" width="45%">
</p>

**Creating a recipe**

<p align="center">
  <img src="interface/Picture8.png" alt="New recipe form" width="45%">
  <img src="interface/Picture9.png" alt="Adding ingredients and instructions to a new recipe" width="45%">
</p>

**Search**

<p align="center">
  <img src="interface/Picture10.png" alt="Search results filtered by name or category" width="45%">
</p>

## How it's built

The code is split into three packages:

```text
RecipeApp
├── ui        Login.java, RecipeApp.java, MainFrame.java
├── service   DataBase.java, RecipeService.java
└── model     Account.java, User.java, Admin.java, Recipe.java, Review.java
```

- **`ui`** contains the Swing windows. `Login` handles sign-in and registration, `RecipeApp` is the main dashboard, and `MainFrame` keeps the shared colors, fonts and custom buttons, so the whole app looks consistent.
- **`service`** holds the logic. `DataBase` opens the SQL Server connection, and `RecipeService` handles searching, saving recipes and reviews, and user operations.
- **`model`** contains plain data classes for accounts, recipes and reviews.

The UI never runs SQL directly. It calls methods like `getAllRecipes()` on `RecipeService`, which talks to the database, so the queries can change without touching the interface.

<p align="center">
  <img src="interface/Picture12.png" alt="Class diagram" width="80%">
</p>

### Object-oriented design

- **Inheritance and abstraction.** `Account` is an abstract base class, and `User` and `Admin` extend it. They share the common account fields and differ in permissions.
- **Polymorphism.** Each subclass overrides `getRole()`, so `User` returns "Home Cook" and `Admin` returns "Administrator". The custom Swing components override `paintComponent()`. `Recipe` has two constructors: one for recipes loaded from the database, which already have an ID, and one for new recipes, which don't have one yet.
- **Encapsulation.** Fields like `password` and `userId` are private and are only accessed through methods.

## Testing

The project has JUnit tests for the model classes:

- **`UserTest`** checks role assignment, `isAdmin()` and password checking.
- **`ReviewTest`** checks rating validation, comments, the link to the recipe, timestamps and the star formatting from `getStarRating()`.

## Tech stack

- Java with Swing for the desktop interface
- Microsoft SQL Server for storage
- JUnit for testing

## 🚀 Running it

You'll need:

- a JDK
- a running SQL Server instance
- the Microsoft JDBC driver for SQL Server

Then:

1. Create a database named `Project` with the tables `Users`, `Recipes`, `Reviews` and `Ingredients`.
2. Update the connection details in `service/DataBase.java` to match your server.
3. Run the app and log in, or create a new account.

## Future improvemnets

- Search by ingredients, plus more filters
- Favorites and bookmarks
- More admin tools
