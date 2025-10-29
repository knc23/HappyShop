package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
class CustomerModelTest {

    @Test
    void makeOrganizedTrolley() {
        CustomerModel customerModel = new CustomerModel();
        Product product = new Product( "0001", "TV", "0001.jpg", 12.01, 100);
        customerModel.setTheProduct(product);
        customerModel.makeOrganizedTrolley();
        customerModel.makeOrganizedTrolley();
        customerModel.makeOrganizedTrolley();
        ArrayList<Product> trolley = customerModel.getTrolley();
        assertEquals(1, trolley.size());
        assertEquals(3, trolley.get(0).getOrderedQuantity());
    }
}