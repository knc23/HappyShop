package ci553.happyshop.client.customer;

import java.time.LocalDate;
import java.util.HashMap;

public class CustomerUserManage {
    private HashMap<String, CustomerUser> users = new HashMap<>();

    CustomerController custController;
    CustomerUser currentUser;

    public CustomerUserManage() {
        users.put("20060120", new CustomerUser("20060120", "0120", "Kary", "Ching", "K.Ching1@uni.brighton.ac.uk", LocalDate.of(2006, 1, 20)));
        users.put("0", new CustomerUser("0", "0", "Kary", "Ching", "K.Ching1@uni.brighton.ac.uk", LocalDate.of(2006, 1, 20)));
    }

    public CustomerUser getAccount(String accountNumber) {
        return users.get(accountNumber);
    }

    public CustomerUser authenticate(String accountNumber, String password) {
        CustomerUser user = users.get(accountNumber);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean accountExists(String accountNumber) {
        return users.containsKey(accountNumber);
    }

    public boolean addUser(CustomerUser user) {
        if ( accountExists(user.getAccountNumber()) ) {
            return false;
        }
        users.put(user.getAccountNumber(), user);
        return true;
        //System.out.println("User " + user.getAccountNumber() + " " + user.getAccountType() + " " + user.getFirstName() + " " + user.getLastName() + " added successfully.");
    }
}
