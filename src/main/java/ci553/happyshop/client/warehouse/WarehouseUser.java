package ci553.happyshop.client.warehouse;

import java.time.LocalDate;

public class WarehouseUser {
    private String firstName;
    private String lastName;
    private String accountNumber;
    private String password;
    private String email;
    private LocalDate birthDate;

    public WarehouseUser( String accountNumber, String password, String firstName, String lastName, String email, LocalDate birthDate) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.accountNumber = accountNumber;
        this.password = password;
        this.email = email;
        this.birthDate = birthDate;
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
}
