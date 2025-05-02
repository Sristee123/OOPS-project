package project;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

class Cookings extends JFrame {
    static class Recipe {
        String name;
        String type;
        List<String> ingredients;
        String instructions;

        Recipe(String name, String type, String ingredientsStr, String instructions) {
            this.name = name;
            this.type = type;
            this.ingredients = Arrays.asList(ingredientsStr.split(","));
            this.instructions = instructions;
        }

        boolean matchesIngredients(List<String> userIngredients) {
            List<String> normalizedUserIngredients = new ArrayList<>();
            for (String ing : userIngredients) {
                normalizedUserIngredients.add(ing.trim().toLowerCase());
            }

            for (String recipeIng : this.ingredients) {
                if (!normalizedUserIngredients.contains(recipeIng.trim().toLowerCase())) {
                    return false;
                }
            }
            return true;
        }

        public String toString() {
            return name;
        }
    }

    private List<Recipe> recipes;
    private JTextArea outputArea;

    public Cookings() throws IOException {
        recipes = loadRecipes("C:\\Users\\ASUS\\OneDrive\\ドキュメント\\reciepe\\project.csv");

        setTitle("Recipe Finder");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton quickRecipesButton = new JButton("Quick Recipes");
        JButton findByIngredientsButton = new JButton("Find by Ingredients");
        JButton exitButton = new JButton("Exit");

        topPanel.add(quickRecipesButton);
        topPanel.add(findByIngredientsButton);
        topPanel.add(exitButton);
        add(topPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        quickRecipesButton.addActionListener(e -> showQuickRecipes());
        findByIngredientsButton.addActionListener(e -> findByIngredients());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void showQuickRecipes() {
        List<Recipe> quickRecipes = new ArrayList<>();
        for (Recipe r : recipes) {
            if (r.type.equalsIgnoreCase("Normal")) {
                quickRecipes.add(r);
            }
        }

        if (quickRecipes.isEmpty()) {
            outputArea.setText("No quick recipes found.");
            return;
        }

        Recipe selectedRecipe = (Recipe) JOptionPane.showInputDialog(
            this,
            "Select a recipe:",
            "Quick Recipes",
            JOptionPane.QUESTION_MESSAGE,
            null,
            quickRecipes.toArray(),
            quickRecipes.get(0)
        );

        if (selectedRecipe != null) {
            int result = JOptionPane.showConfirmDialog(this, "Do you want to see the instructions?", "Instructions", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                outputArea.setText("Instructions for " + selectedRecipe.name + ":\n" + selectedRecipe.instructions);
            }
        }
    }

    private void findByIngredients() {
        String input = JOptionPane.showInputDialog(this, "Enter ingredients (comma-separated):");
        if (input != null && !input.trim().isEmpty()) {
            List<String> inputIngredients = Arrays.asList(input.split(","));
            StringBuilder matches = new StringBuilder();
            boolean found = false;

            for (Recipe r : recipes) {
                if (r.matchesIngredients(inputIngredients)) {
                    matches.append("Match Found: ").append(r.name)
                        .append("\nInstructions: ").append(r.instructions)
                        .append("\n----------------------\n");
                    found = true;
                }
            }

            if (!found) {
                matches.append("No match found.");
            }
            outputArea.setText(matches.toString());
        }
    }

    static List<Recipe> loadRecipes(String filePath) throws IOException {
        List<Recipe> recipes = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        for (int i = 1; i < lines.size(); i++) { // Skip header
            String[] parts = lines.get(i).split(",(?=(?:[^\"]\"[^\"]\")[^\"]$)", -1);
            if (parts.length == 4) {
                recipes.add(new Recipe(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()));
            }
        }

        return recipes;
    }
}

public class Cooking {
    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(() -> {
            try {
                new Cookings().setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}