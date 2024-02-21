package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private TextView bord;
    private StringBuilder equation;

    private static final String KEY_EQUATION = "equation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bord = findViewById(R.id.bord);
        equation = new StringBuilder();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Vérifier si l'orientation a changé en mode paysage
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_landscape);
            bord = findViewById(R.id.bord);
            equation = new StringBuilder();
            // Mettre à jour votre interface utilisateur en mode paysage si nécessaire
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
            bord = findViewById(R.id.bord);
            equation = new StringBuilder();
            // Mettre à jour votre interface utilisateur en mode portrait si nécessaire
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_EQUATION, equation.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String savedEquation = savedInstanceState.getString(KEY_EQUATION);
        if (savedEquation != null) {
            equation = new StringBuilder(savedEquation);
            bord.setText(savedEquation);
        }
    }

    public void onButtonClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        switch (buttonText) {
            case "=":
                calculateResult();
                break;
            case "AC":
                clearEquation();
                break;
            case "C":
                deleteCar();
                break;
            case "sin":
            case "cos":
            case "tan":

                    handleTrigonometricFunction(buttonText);

                break;
            case "log":
            case "ln":
                handleLogarithmicFunction(buttonText);
                break;
            case "π":
                equation.append(Math.PI);
                bord.setText(equation.toString());
                break;
            case "e":
                equation.append(Math.E);
                bord.setText(equation.toString());
                break;
            case "√":
                handleSquareRoot();
                break;
            case "deg":
                if (equation.length() > 0) {
                    double radians = Double.parseDouble(equation.toString());
                    double degrees = Math.toDegrees(radians); // Convertit radians en degrés
                    bord.setText(String.valueOf(degrees));
                    equation.setLength(0); // Efface l'équation
                    equation.append(degrees); // Met à jour l'équation avec les degrés convertis
                }
                break;
            case "rad":
                if (equation.length() > 0) {
                    double degrees = Double.parseDouble(equation.toString());
                    double radians = Math.toRadians(degrees); // Convertit degrés en radians
                    bord.setText(String.valueOf(radians));
                    equation.setLength(0); // Efface l'équation
                    equation.append(radians); // Met à jour l'équation avec les radians convertis
                }
                break;
            case "!":
                handleFactorial();
                break;
            default:
                equation.append(buttonText);
                bord.setText(equation.toString());
                break;
        }
    }

    private void calculateResult() {
        if (equation.length() > 0) {
            try {
                String expression = equation.toString();
                double result = evaluateExpression(expression);
                bord.setText(String.valueOf(result));
                equation.setLength(0); // Clear the equation after calculation
                equation.append(result); // Set the result as the start of the new equation
            } catch (Exception e) {
                bord.setText("Error");
            }
        }
    }

    private void clearEquation() {
        equation.setLength(0);
        bord.setText("");
    }
 private void deleteCar(){
     if (equation.length() > 0) {
         // Supprimer le dernier caractère de l'équation
         equation.deleteCharAt(equation.length() - 1);
         // Mettre à jour le texte affiché dans le TextView
         bord.setText(equation.toString());
     }
 }
    private void handleTrigonometricFunction(String function) {
        if (equation.length() > 0) {
            double operand = Double.parseDouble(equation.toString());
            double result;
            switch (function) {
                case "sin":
                    result = Math.sin(operand);
                    break;
                case "cos":
                    result = Math.cos(operand);
                    break;
                case "tan":
                    result = Math.tan(operand);
                    break;
                default:
                    result = 0; // Default to 0 in case of unrecognized function
                    break;
            }
            bord.setText(String.valueOf(result));
            equation.setLength(0);
            equation.append(result);
        }
    }
    private void handleFactorial() {
        if (equation.length() > 0) {
            int number = Integer.parseInt(equation.toString());
            long result = calculateFactorial(number);
            bord.setText(String.valueOf(result));
            equation.setLength(0);
            equation.append(result);
        }
    }

    private long calculateFactorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers");
        }
        long factorial = 1;
        for (int i = 2; i <= n; i++) {
            factorial *= i;
        }
        return factorial;
    }
    private void handleLogarithmicFunction(String function) {
        if (equation.length() > 0) {
            double operand = Double.parseDouble(equation.toString());
            double result;
            switch (function) {
                case "log":
                    result = Math.log10(operand);
                    break;
                case "ln":
                    result = Math.log(operand);
                    break;
                default:
                    result = 0; // Default to 0 in case of unrecognized function
                    break;
            }
            bord.setText(String.valueOf(result));
            equation.setLength(0);
            equation.append(result);
        }
    }

    private void handleSquareRoot() {
        if (equation.length() > 0) {
            double operand = Double.parseDouble(equation.toString());
            double result = Math.sqrt(operand);
            bord.setText(String.valueOf(result));
            equation.setLength(0);
            equation.append(result);
        }
    }
    private double evaluateExpression(String expression) {
        // Supprimez les espaces en trop dans l'expression
        expression = expression.replaceAll("\\s+", "");

        // Utilisez une pile pour stocker les opérandes et les opérateurs
        Stack<Double> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();

        // Identifiez les tokens dans l'expression
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                // Si le caractère est un chiffre ou un point, ajoutez-le au token en cours de construction
                currentToken.append(c);
            } else {
                // Si le caractère n'est pas un chiffre ou un point, ajoutez le token en cours à la liste des tokens
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken = new StringBuilder(); // Réinitialisez le token en cours
                }
                // Ajoutez l'opérateur ou la parenthèse à la liste des tokens
                tokens.add(String.valueOf(c));
            }
        }
        // Ajoutez le dernier token s'il n'a pas été ajouté
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        // Parcourez les tokens et traitez les opérandes et les opérateurs
        for (String token : tokens) {
            char firstChar = token.charAt(0);
            if (Character.isDigit(firstChar)) {
                // Si le token est un nombre, empilez-le comme opérande
                operands.push(Double.parseDouble(token));
            } else if (firstChar == '(') {
                // Si le token est une parenthèse ouvrante, empilez-la
                operators.push(firstChar);
            } else if (firstChar == ')') {
                // Si le token est une parenthèse fermante, évaluez les sous-expressions à l'intérieur des parenthèses
                while (!operators.isEmpty() && operators.peek() != '(') {
                    evaluateOperation(operands, operators);
                }
                operators.pop(); // Supprimez la parenthèse ouvrante correspondante de la pile des opérateurs
            } else if (firstChar == '+' || firstChar == '-' || firstChar == '*' || firstChar == '/') {
                // Si le token est un opérateur, évaluez les opérations en fonction de leur priorité
                while (!operators.isEmpty() && hasPrecedence(firstChar, operators.peek())) {
                    evaluateOperation(operands, operators);
                }
                operators.push(firstChar);
            }
        }

        // Évaluez les opérations restantes dans les piles
        while (!operators.isEmpty()) {
            evaluateOperation(operands, operators);
        }

        // Le résultat final est le seul élément restant dans la pile des opérandes
        return operands.pop();
    }
    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false; // Les parenthèses ont la priorité la plus élevée
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false; // Les opérateurs * et / ont la priorité sur + et -
        }
        return true; // Les autres combinaisons ont op1 avec priorité supérieure
    }
    private void evaluateOperation(Stack<Double> operands, Stack<Character> operators) {
        // Évaluez une opération entre les deux opérandes en utilisant l'opérateur du sommet de la pile des opérateurs
        double operand2 = operands.pop();
        double operand1 = operands.pop();
        char operator = operators.pop();
        double result = performOperation(operand1, operand2, operator);
        operands.push(result);
    }

    private double performOperation(double operand1, double operand2, char operator) {
        // Exécutez l'opération entre les deux opérandes
        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return operand1 / operand2;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}