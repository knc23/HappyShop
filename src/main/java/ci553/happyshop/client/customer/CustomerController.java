package ci553.happyshop.client.customer;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class CustomerController {
    public CustomerModel cusModel;
    public CustomerView cusView;

    private CustomerUserManage cusUserManage = new CustomerUserManage();
    private CustomerUser currentUser;

    boolean existsAccount = true;

    public CustomerController(CustomerView cusView, CustomerModel cusModel) {
        this.cusView = cusView;
        this.cusModel = cusModel;
    }

    public void login(String accountNumber, String password) {
        currentUser = cusUserManage.authenticate(accountNumber, password);
        if (currentUser != null) {
            cusView.tfAccID.setText("");
            cusView.pfAccPwd.setText("");
            cusView.laLoginMsg.setText("");
            cusView.showSearchPage();
            System.out.println(currentUser.getAccountNumber() + " " + currentUser.getFirstName() + " " + currentUser.getLastName() + " Logged in");
        } else {
            cusView.tfAccID.setText("");
            cusView.pfAccPwd.setText("");
            cusView.laLoginMsg.setText("Invalid login credentials!");
        }
    }

    public void handleCreateAccount(String accountNumber, String password, String firstName, String lastName, String confirmPassword, String email, LocalDate birthDate) {
        existsAccount = cusUserManage.accountExists(accountNumber);

        if ( firstName.equals("") ) {
            cusView.tfCreateAccUserFN.setText("");
            cusView.taCreateAccMsg.setText("Please ENTER your FIRST NAME");
        } else if ( lastName.equals("") ) {
            cusView.tfCreateAccUserLN.setText("");
            cusView.taCreateAccMsg.setText("Please ENTER your LAST NAME");
        } else if ( accountNumber.equals("") ) {
            cusView.tfCreateAccID.setText("");
            cusView.taCreateAccMsg.setText("Please ENTER an ACCOUNT NUMBER of your choice");
        } else if ( password.equals("") ) {
            cusView.pfCreateAccPwd.setText("");
            cusView.taCreateAccMsg.setText("Please ENTER a PASSWORD of your choice");
        }  else if ( confirmPassword.equals("") ) {
            cusView.pfCreateAccPwd2.setText("");
            cusView.taCreateAccMsg.setText("Please ENTER the same PASSWORD");
        } else if ( email.equals("") ) {
            cusView.tfCreateAccEmail.setText("");
            cusView.taCreateAccMsg.setText("Please ENTER your EMAIL");
        } else if ( birthDate == null ) {
            cusView.dpCreateAccBDay.setValue(LocalDate.now());
            cusView.taCreateAccMsg.setText("Please SELECT your DATE OF BIRTH");
        } else {
            if ( !existsAccount ) {
                if ( password.equals(confirmPassword) ) {
                    String newAccountNumber = accountNumber;
                    String newPassword = password;
                    String newFirstName = firstName;
                    String newLastName = lastName;
                    String newEmail = email;
                    LocalDate newBirthDate = birthDate;

                    CustomerUser newUser = new CustomerUser(newAccountNumber, newPassword, newFirstName, newLastName, newEmail, newBirthDate);
                    boolean userAdded = cusUserManage.addUser(newUser);

                    if (userAdded) {
                        cusView.welcomeRoot.getChildren().setAll(cusView.vbLoginPage);

                        cusView.tfCreateAccID.setText("");
                        cusView.pfCreateAccPwd.setText("");
                        cusView.tfCreateAccUserFN.setText("");
                        cusView.tfCreateAccUserLN.setText("");
                        cusView.pfCreateAccPwd2.setText("");
                        cusView.tfCreateAccEmail.setText("");
                        cusView.dpCreateAccBDay.setValue(LocalDate.now());

                        System.out.println("Account Created\n" +
                                "Account Number : " + accountNumber + "\n" +
                                "Password : " + password + "\n" +
                                "First Name : " + firstName + " " + "Last Name : " + lastName + "\n" +
                                "Email : " + email + "\n" +
                                "Date Of Birth : " + birthDate);
                    } else {
                        cusView.taCreateAccMsg.setText("Error: Unable to create account");
                    }
                } else {
                    cusView.pfCreateAccPwd.setText("");
                    cusView.pfCreateAccPwd2.setText("");
                    cusView.taCreateAccMsg.setText("Password does not match");
                }
            } else {
                cusView.tfAccID.setText("");
                cusView.taCreateAccMsg.setText("Account Number already exists, please choose another account number");
            }
            //view.createAccMessageTextArea.setText("Please ENTER the information to create an account");
        }
    }

    public void doAction(String action) throws SQLException, IOException {
        switch (action) {
            case "Create Account" :
                handleCreateAccount(cusView.tfCreateAccID.getText(),
                        cusView.pfCreateAccPwd.getText(),
                        cusView.tfCreateAccUserFN.getText(),
                        cusView.tfCreateAccUserLN.getText(),
                        cusView.pfCreateAccPwd2.getText(),
                        cusView.tfCreateAccEmail.getText(),
                        cusView.dpCreateAccBDay.getValue());
                break;
            case "Search":
                cusModel.search();
                break;
            case "More Information":
                cusModel.checkInfo();
                break;
            case "Add to Wish List":
                cusModel.addToWishList();
                break;
            case "Add to Trolley":
                cusModel.addToTrolley();
                break;
            case "Add to ALL Wish List":
                cusModel.addALLToWishList();
                break;
            case "Add to ALL Trolley":
                cusModel.addALLToTrolley();
                break;
            case "Cancel":
                cusModel.cancel();
                break;
            case "Check Out":
                cusModel.checkOut();
                break;
            case "OK & Close":
                cusModel.closeReceipt();
                break;
        }
    }

}
