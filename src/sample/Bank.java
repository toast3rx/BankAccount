package sample;

import com.mysql.cj.exceptions.NumberOutOfRange;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.sql.*;


public class Bank {

    private static final String USER = "basic_user";
    private  static final String PASSWORD = "=E8gC>BG]%aW@wp4";
    private static final Stage stage = new Stage();

    public Bank(){

    }

    /**
     * Helper method for @showSignUpMenu
     * @param username used to create an unique entry
     * @param password a password to login in using username
     */
    private static void addAccount(String username, String password)  {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bank", USER, PASSWORD
            );

            Statement statement = con.createStatement();
            int result = statement.executeUpdate("insert into account(username, password, security_code, balance) " +
                     "values('" + username + "','" + password +"','" +  ((int)(1000 + Math.random()  * 8889)) + "'," + 0.00 + ")");

            con.close();
        }catch (Exception e){
            System.out.println(e);
        }

    }


    /**
     * Main menu
     * Fields for entering the username and password
     * Login, Sign Up, Forgot password buttons
     */
    public static void showMainMenu() {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5);
        pane.setVgap(5);

        // Field for error messages
        Text errorMessage = new Text("");
        pane.add(errorMessage, 1, 4);

        // Clear error message if you click anywhere on the screen
        pane.setOnMouseClicked(mouseEvent -> {
            errorMessage.setText("");
        });

        // Username field
        pane.add(new Label("Username: "), 0, 0);
        TextField tfUsername = new TextField();
        pane.add(tfUsername, 1, 0);

        // Password field
        pane.add(new Label("Password: "), 0, 1);
        PasswordField tfPassword = new PasswordField();
        pane.add(tfPassword, 1, 1);

        // Login button
        Button btLogin = new Button("Login");
        btLogin.setOnAction(actionEvent -> {

            String username = tfUsername.getText();
            String password = tfPassword.getText();

            try {
                login(username, password);
                showLoginMenu(username, password);
            } catch (com.mysql.cj.jdbc.exceptions.CommunicationsException ex) {
                errorMessage.setText("Server is offline. Come back later");
                tfUsername.clear();
                tfPassword.clear();
            } catch (Exception ex) {
                errorMessage.setText("Invalid username or password!");
                tfUsername.clear();
                tfPassword.clear();
            }


        });
        pane.add(btLogin, 0, 5);

        // If the user user press enter, activate the login process
        tfPassword.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                btLogin.fire();
            }
        });

        // Forgot password button
        Button btForgotPsd = new Button("Forgot password");
        btForgotPsd.setOnAction(actionEvent -> showForgotPasswordMenu());
        pane.add(btForgotPsd, 2, 5);

        // Sign up  Button
        Button btSignUp = new Button("Sign up");
        btSignUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                showSignUpMenu();
            }
        });
        pane.add(btSignUp, 1, 5);

        stage.setScene(new Scene(pane, 350, 400));
        stage.show();


    }

    /**
     * Sign up Menu
     * Create an entry in the database with an unique username and a password
     */
    private static void showSignUpMenu() {

        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setAlignment(Pos.CENTER);

        // New Username label and text field
        pane.add(new Label("Enter an username for your account: "), 0, 0);
        TextField tfUsername = new TextField();
        pane.add(tfUsername, 0, 1);

        // New password label and text fields
        pane.add(new Label("Enter a password for your account: "), 0, 4);
        PasswordField tfPassword = new PasswordField();
        pane.add(tfPassword, 0, 5);

        pane.add(new Label("Rewrite your password: "), 0, 6);
        PasswordField tfPassWordC = new PasswordField();
        pane.add(tfPassWordC, 0, 7);

        Button btSignUp = new Button("Sign up");
        pane.add(btSignUp, 0, 9);
        btSignUp.setOnAction(actionEvent -> {
            String username = tfUsername.getText();
            String password = tfPassword.getText();
            String password1 = tfPassWordC.getText();
            if (!(password.equals(password1)))
                pane.add(new Text("The password is not the same in the fields"), 0, 8);
            else
                try {
                    addAccount(username, password);
                    showMainMenu();
                } catch (Exception ex) {
                    pane.add(new Text("The name is already taken"), 0, 2);
                }
        });
        Button btReturn = new Button("Return");
        btReturn.setOnAction(actionEvent -> showMainMenu());
        pane.add(btReturn, 1, 9);


        stage.setScene(new Scene(pane, 350, 400));


    }



    /**
     * Help method for login
     * Call it in main menu
     */
    private static void login(String username, String password) throws Exception  {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bank", USER, PASSWORD
            );
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select username, password from account where username = '" + username + "' AND password ='" + password + "';");
           // if(rs.next())
            //System.out.println(rs.getString(1) + "/" + rs.getString(2));
           if(!rs.next())
               throw new Exception("Invalid password");
            con.close();

        } catch (Exception e){
            System.out.println(e);
            throw e;
        }
    }


    /**
     * Login menu (Account menu)
     * Shows the options for the logged account
     * balance, withdraw, security code, transfer, change password
     */
    private static void showLoginMenu(String username, String password) {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setVgap(5);
        pane.setHgap(5);

        // Get balance
        Button btBalance = new Button("Check your balance");
        btBalance.setOnAction(actionEvent -> showBalanceMenu( username,  password));
        pane.add(btBalance, 0, 0);

        // Withdraw money
        Button btWithdraw = new Button("Withdraw");
        btWithdraw.setOnAction(actionEvent -> showWithdrawMenu(username, password));
        pane.add(btWithdraw, 0, 1);

        // Add balance
        Button btAddBalance = new Button("Add balance");
        btAddBalance.setOnAction(actionEvent -> showAddMenuPane( username, password));
        pane.add(btAddBalance, 0, 2);

        // Change password
        Button btChangePass = new Button("Change password");
        btChangePass.setOnAction(actionEvent -> showChangePasswordMenu( username,  password));
        pane.add(btChangePass, 0, 3);

        // Get security code
        Button btSecurityCode = new Button("Security Code");
        btSecurityCode.setOnAction(actionEvent -> showSecurityCodeMenu( username,  password));
        pane.add(btSecurityCode, 0, 4);

        // Transfer balance
        Button btTransfer = new Button("Transfer money");
        btTransfer.setOnAction(actionEvent -> showTransferMenu( username,  password));
        pane.add(btTransfer, 0, 5);

        // Log out from account
        Button btLogOut = new Button("Log Out");
        btLogOut.setOnAction(actionEvent -> showMainMenu());
        pane.add(btLogOut, 0, 6);

        stage.setScene(new Scene(pane, 350, 400));
    }

    /**
     * Balance menu
     *  get the balance from your account
     * */
    private static void showBalanceMenu(String username, String password) {
        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setAlignment(Pos.CENTER);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bank", USER, PASSWORD);
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select balance from account where username = '" + username + "' AND password = '" + password + "';");
            Text balance = new Text();
            if(rs.next()) {
                 balance.setText("Your balance is " + rs.getString(1));
            }
            else{
                balance.setText("Error");
            }
            pane.add(balance, 0, 0);

            con.close();
        }catch (Exception exception){
            System.out.println(exception);
        }
        // Back to main menu
        Button buttonBack = new Button("Back");
        buttonBack.setOnAction(actionEvent -> showLoginMenu(username, password));
        pane.add(buttonBack, 0, 1);

        stage.setScene(new Scene(pane, 350, 400));
    }

    /**
     * Add money to balance
     * @throws NumberFormatException is threw if the number is not valid
     */
    private static void showAddMenuPane(String username, String password) throws NumberFormatException {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setVgap(5);
        pane.setHgap(5);

        pane.add(new Label("Enter how much you want to add:"), 0, 0);

        TextField balance = new TextField();
        pane.add(balance, 0, 1);

        Button btAdd = new Button("Add");
        btAdd.setOnAction(actionEvent -> {
            try {
                double addedBalance = Math.abs(Double.parseDouble(balance.getText()));

                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/bank", USER, PASSWORD);
                Statement statement = con.createStatement();
                int index = statement.executeUpdate("update account set balance = balance + " + addedBalance +
                        "where username = '" + username + "' and password = '" + password + "';");

                balance.setText("Operation was successful");
            } catch (NumberFormatException ex) {
                balance.setText("Invalid number");
            }
            catch (Exception exception){
                System.out.println(exception);
            }
        });
        pane.add(btAdd, 0, 3);

        Button back = new Button("Back");
        back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                showLoginMenu(username, password);
            }
        });
        pane.add(back, 0, 4);

        stage.setScene(new Scene(pane, 350, 400));
    }


    /**
     * Withdraw money from your balance
     */
    private static void showWithdrawMenu(String username, String password) {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5);
        pane.setVgap(5);

        // Text field for inserting the balance
        pane.add(new Label("Enter how much you want to withdraw: "), 0, 0);
        TextField tfWithdraw = new TextField();
        pane.add(tfWithdraw, 0, 1);

        // Field for showing errors
        Text errorMessage = new Text("");
        pane.add(errorMessage, 0, 3 );
        pane.setOnMouseClicked(mouseEvent -> {
            errorMessage.setText("");
        });


        Button btWithdraw = new Button("Withdraw");
        btWithdraw.setOnAction(actionEvent -> {
            try {
                double withdrawn = Math.abs(Double.parseDouble(tfWithdraw.getText()));
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/bank", USER,PASSWORD
                );
                Statement statement = con.createStatement();
                ResultSet rs = statement.executeQuery("select balance from account where username = '" + username +"' and password = '" +
                password + "';");
                double balance;
                if(rs.next())
                     balance = Double.parseDouble(rs.getString(1));
                else throw new Exception();
                if(withdrawn > balance)
                    throw new NumberOutOfRange("You do not have enough money");

                int index = statement.executeUpdate("update account set balance = balance - " + withdrawn +
                        " where username = '" + username + "' and password = '" + password + "';");
                errorMessage.setText("Operation successful!");

            } catch (NumberFormatException ex) {
                errorMessage.setText("Please enter a valid number.");
            }
            catch (NumberOutOfRange e){
                errorMessage.setText("You do not have enough money.");
            }
            catch (Exception e){
                errorMessage.setText("Unexpected error. Please try again.");
                System.out.println(e);
            }
        });

        pane.add(btWithdraw, 0, 2);

        Button btBack = new Button("Back");
        btBack.setOnAction(actionEvent -> showLoginMenu(username, password));
        pane.add(btBack, 0, 4);

        stage.setScene(new Scene(pane, 350, 400));
    }


    /**
     * Change password menu
     * Change account password
     */

    private static void showChangePasswordMenu(String username, String passwordOld) {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5);
        pane.setVgap(5);

        // new password field
        pane.add(new Label("Enter your new password: "), 0, 0);
        PasswordField pfPassword = new PasswordField();
        pane.add(pfPassword, 0, 1);

        // re-enter new password field
        pane.add(new Label("Re-enter your new password : "), 0, 3);
        PasswordField pfPassword1 = new PasswordField();
        pane.add(pfPassword1, 0, 4);

        // fields for info, error messages
        Text message = new Text();
        Text passwordMsg = new Text();
        pane.setOnMouseClicked(mouseEvent -> {
            message.setText("");
            passwordMsg.setText("");
        });
        pane.add(message, 0, 6);
        pane.add(passwordMsg, 0, 5);

        // change password button
        Button btResetPassword = new Button("Reset password");
        btResetPassword.setOnAction(actionEvent -> {
            message.setText("");
            passwordMsg.setText("");
            String password = pfPassword.getText();
            String password1 = pfPassword1.getText();

            if (!(password.equals(password1)))
                passwordMsg.setText("Passwords are not the same");
            else {
               try{
                  Class.forName("com.mysql.cj.jdbc.Driver");
                  Connection con = DriverManager.getConnection(
                          "jdbc:mysql://localhost:3306/bank", USER, PASSWORD
                  );
                  Statement statement = con.createStatement();
                  int index = statement.executeUpdate("update account set password = '" + password + "' where username = '" + username +
                          "' and password = '" + passwordOld + "';");
                   message.setText("Password changed!");
               }
               catch (Exception e){
                   message.setText("Unexpected error. Please try again.");
               }
            }

        });
        pane.add(btResetPassword, 0, 7);

        Button btReturn = new Button("Return");
        btReturn.setOnAction(actionEvent -> showLoginMenu(username, passwordOld));
        pane.add(btReturn, 0, 8);

        stage.setScene(new Scene(pane, 350, 400));
    }

    /**
     * Security code menu
     */
    private static void showSecurityCodeMenu(String username, String password) {
       try {
           GridPane pane = new GridPane();
           pane.setAlignment(Pos.CENTER);
           pane.setHgap(5);
           pane.setVgap(5);

           pane.add(new Label("Your security code: "), 0, 0);
           TextField securityCode = new TextField();

           //securityCode.setText(" ");
           securityCode.setEditable(false);
           pane.add(securityCode, 0, 1);

           Class.forName("com.mysql.cj.jdbc.Driver");
           Connection con = DriverManager.getConnection(
                   "jdbc:mysql://localhost:3306/bank", USER, PASSWORD);
           Statement statement = con.createStatement();
           ResultSet rs = statement.executeQuery("select security_code from account where username = '" + username +
                   "' and password = '" + password +"';");
           if(rs.next())
               securityCode.setText(rs.getString(1));

           Button btReturn = new Button("Return");
           btReturn.setOnAction(actionEvent -> showLoginMenu(username, password));

           pane.add(btReturn, 0, 3);

           stage.setScene(new Scene(pane, 350, 400));
       }catch (Exception e){
           System.out.println(e);
       }
    }


    /**
     * Change password using username and security code linked to this account
     * If the current password is unknown
     */
    private static void showForgotPasswordMenu() {
        // Pane settings, padding, fields alignment
        GridPane pane = new GridPane();
        pane.setVgap(5);
        pane.setHgap(5);
        pane.setAlignment(Pos.CENTER);

        // Text field for entering the username
        pane.add(new Label("Enter your username: "), 0, 0);
        TextField tfUsername = new TextField();
        pane.add(tfUsername, 0, 1);

        // Password field to enter the new password
        pane.add(new Label("Enter your new password: "), 0, 2);
        PasswordField pfPassword = new PasswordField();
        pane.add(pfPassword, 0, 4);

        // Password field to reenter the new password
        pane.add(new Label("Re-enter your password "), 0, 5);
        PasswordField pfPassword1 = new PasswordField();
        pane.add(pfPassword1, 0, 6);

        // Text field to insert account's security code
        pane.add(new Label("Enter your security code: "), 0, 8);
        TextField tfSecurityCode = new TextField();
        pane.add(tfSecurityCode, 0, 9);

        // Create a text field to display messages like errors
        Text message = new Text();
        pane.add(message, 0, 10);

        // Button to reset password
        Button btReset = new Button("Reset password");
        /* Connect to the database
         * Check if the username exists and security code matches
         * Check if password matches
         * Renew password
         */
        btReset.setOnAction(new EventHandler<ActionEvent>() {
            private void clearFields(){
                tfUsername.clear();
                pfPassword.clear();
                pfPassword1.clear();
                tfSecurityCode.clear();
            }
            @Override
            public void handle(ActionEvent actionEvent) {
                // Clear previous messages
                message.setText("");
                // Connect to the database
                try {
                    // Get the username from the text
                    String username = tfUsername.getText();

                    // Check if one or more fields are empty
                    if (username.isEmpty() || pfPassword.getText().isEmpty() || pfPassword1.getText().isEmpty() ||
                            tfSecurityCode.getText().isEmpty()) throw new InputEmptyException();

                    // Connect to the database
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/bank", USER, PASSWORD);
                    Statement statement = con.createStatement();

                    // Get the security code which belongs to the account
                    ResultSet rs = statement.executeQuery("select security_code from account where username = '" + username + "';");
                    String security_code;
                    if (rs.next()) {
                        security_code = rs.getString(1);
                    } else throw new MissingUsernameException();

                    // If the passwords are not equal, throw exception
                    if (!pfPassword.getText().equals(pfPassword1.getText())) throw new PasswordMatchException();
                    // Check if the security code entered by the user is the same as the one in the database
                    if (!security_code.equals(tfSecurityCode.getText())) throw new InvalidSecurityCodeException();

                    // Execute the query to change password
                    int changePassRs = statement.executeUpdate("update account set password = '" + pfPassword.getText() +
                            "' where username = '" + username + "' and security_code= '" + security_code + "';");

                    // Show a message that confirms if the password what changed with no errors
                    message.setText("Password changed!");

                    //Clear the fields
                    clearFields();
                } catch (InputEmptyException e) {
                    message.setText("One or more fields are empty");
                    clearFields();
                    tfSecurityCode.clear();
                } catch (MissingUsernameException e) {
                    message.setText("This username does not exist");
                    clearFields();
                } catch (PasswordMatchException e) {
                    message.setText("Passwords are not the same");
                    clearFields();

                } catch (InvalidSecurityCodeException e) {
                    message.setText("Invalid security code");
                    clearFields();
                } catch (Exception e) {
                    System.out.println(e);
                    message.setText("Unexpected error");
                    clearFields();
                }
            }
        });
        // Add the reset button in the window
        pane.add(btReset, 0, 11);

        // Return button for login page
        Button btReturn = new Button("Return");
        btReturn.setOnAction(actionEvent -> showMainMenu());
        pane.add(btReturn, 0, 12);

        // Set the window
        stage.setScene(new Scene(pane, 350, 400));
    }


    /**
     * Transfer money menu
     * Transfer monet from current account to another account
     */
    private static void showTransferMenu(String username, String password) {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5);
        pane.setVgap(5);

        // text field for recipient username
        pane.add(new Label("Enter the recipient username"), 0, 0);
        TextField tfRecipientUsername = new TextField();
        pane.add(tfRecipientUsername, 0, 1);

        // text field for the sum you want to transfer
        pane.add(new Label("Enter the sum you want to transfer"), 0, 3);
        TextField tfSum = new TextField();
        pane.add(tfSum, 0, 4);

        // text field security code
        pane.add(new Label("Enter your security code"), 0, 6);
        TextField tfSecurityCode = new TextField();
        pane.add(tfSecurityCode, 0, 7);

        // field for error/information messages
        Text message = new Text();
        pane.add(message, 0, 10);
        Button btSend = new Button("Transfer");

        btSend.setOnAction(actionEvent -> {
            try {
                String transferUsername = tfRecipientUsername.getText();
                double sum = Double.parseDouble(tfSum.getText());
                if(sum < 0) throw new NumberOutOfRange("You can't transfer negative numbers");
                String securityCode = tfSecurityCode.getText();

                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/bank", USER, PASSWORD
                );

                Statement statement = con.createStatement();
                ResultSet sk  = statement.executeQuery("select security_code from account where username = '" + username + "'" +
                        " and password = '" + password + "';");
                String actualSecurityKey = "";
                if(sk.next())
                    actualSecurityKey = sk.getString(1);
                if(!securityCode.equals(actualSecurityKey)) throw new Exception("Invalid security key");

                double balance = 0;
                ResultSet getBalance = statement.executeQuery("select balance from account where username = '" + username +"'" +
                        " and password = '" + password + "';");
                if(getBalance.next())
                    balance = Double.parseDouble(getBalance.getString(1));

                if(balance < sum ) throw new NumberOutOfRange("You do not have enough money");

                int removeMoney = statement.executeUpdate("update account set balance = balance - " + sum + " where username = '" + username +
                        "' and password = '" + password + "';");
                int addMoney = statement.executeUpdate("update account set balance = balance + " + sum + " where username = '" + transferUsername + "';");


            }catch ( Exception e){
                System.out.println(e);
            }
        });



        pane.add(btSend, 0, 9);

        Button btReturn = new Button("Return");
        btReturn.setOnAction(actionEvent -> showLoginMenu(username, password));
        pane.add(btReturn, 0, 11);

        stage.setScene(new Scene(pane, 350, 400));
    }

    /**
     * Stage properties, such as block resize and set the title
     */
    public static void setStageProperty() {
        stage.setWidth(600);
        stage.setHeight(400);
        stage.setResizable(false);
        stage.setTitle("Bank Account");
    }
}

    //////////////////
    // Exceptions/////
    //////////////////
    class InputEmptyException extends java.lang.Exception{
        public InputEmptyException(){
        super("One or more fields are empty");
    }

    }
    class MissingUsernameException extends java.lang.Exception{
        public MissingUsernameException(){
        super("This username does not exist");
    }
    }
    class PasswordMatchException extends java.lang.Exception{ public PasswordMatchException(){
        super("Passwords do not match");
        }
    }
    class InvalidSecurityCodeException extends java.lang.Exception{
        public InvalidSecurityCodeException() {
        super("Invalid security code");
    }
    }

