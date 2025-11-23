package ci553.happyshop.client.warehouse;

import java.time.LocalDate;
import java.util.HashMap;

public class WarehouseUserManage {
    private HashMap<String, WarehouseUser> users = new HashMap<>();

    WarehouseController wareController;
    WarehouseUser currentUser;

    public WarehouseUserManage() {
        users.put("20060120", new WarehouseUser("20060120", "0120", "Kary", "Ching", "K.Ching1@uni.brighton.ac.uk", LocalDate.of(2006, 1, 20)));
        users.put("0", new WarehouseUser("0", "0", "Kary", "Ching", "K.Ching1@uni.brighton.ac.uk", LocalDate.of(2006, 1, 20)));
    }

    public WarehouseUser getAccount(String accountNumber) {
        return users.get(accountNumber);
    }

    public WarehouseUser authenticate(String accountNumber, String password) {
        WarehouseUser user = users.get(accountNumber);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean accountExists(String accountNumber) {
        return users.containsKey(accountNumber);
    }

    public boolean addUser(WarehouseUser user) {
        if ( accountExists(user.getAccountNumber()) ) {
            return false;
        }
        users.put(user.getAccountNumber(), user);
        return true;
        //System.out.println("User " + user.getAccountNumber() + " " + user.getAccountType() + " " + user.getFirstName() + " " + user.getLastName() + " added successfully.");
    }
}
