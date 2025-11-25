package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerUser {
    private String firstName;
    private String lastName;
    private String accountNumber;
    private String password;
    private String email;
    private LocalDate birthDate;

    private ArrayList<Product> wishList;
    private ArrayList<Product> trolley;
    private List<String> purchaseHistory;

    public CustomerUser( String accountNumber, String password, String firstName, String lastName, String email, LocalDate birthDate) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.accountNumber = accountNumber;
        this.password = password;
        this.email = email;
        this.birthDate = birthDate;

        this.wishList = new ArrayList<>();
        this.trolley = new ArrayList<>();
        this.purchaseHistory = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public ArrayList<Product> getWishList() {
        return wishList;
    }

    public ArrayList<Product> getTrolley() {
        return trolley;
    }

    public List<String> getPurchaseHistory() {
        return purchaseHistory;
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public String getPassword() {
        return password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }


    public void addPurchaseHistory(String purchase) {
        purchaseHistory.add(purchase);
        System.out.println(purchaseHistory);
    }
}
