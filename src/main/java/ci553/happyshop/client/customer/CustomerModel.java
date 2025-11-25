package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.ProductListFormatter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * TODO
 * You can either directly modify the CustomerModel class to implement the required tasks,
 * or create a subclass of CustomerModel and override specific methods where appropriate.
 */
public class CustomerModel {
    public CustomerView cusView;
    private CustomerUser currentUser;
    public DatabaseRW databaseRW; //Interface type, not specific implementation
                                  //Benefits: Flexibility: Easily change the database implementation.
    private ArrayList<Product> productList = new ArrayList<>(); // search results fetched from the database

    private enum UpdateForAction {
        BtnSearch

    }

    private Product theProduct =null; // product found from search
    //private ArrayList<Product> trolley =  new ArrayList<>(); // a list of products in trolley
    //private ArrayList<Product> wishList = new ArrayList<>();

    // Four UI elements to be passed to CustomerView for display updates.
    private String imageName = "imageHolder.jpg";                // Image to show in product preview (Search Page)
    private String displayLaSearchResult = "No Product was searched yet"; // Label showing search result message (Search Page)
    private String displayTaInfo = "";
    //private String displayTaWishList = "";
    //private String displayTaTrolley = "";                                // Text area content showing current trolley items (Trolley Page)
    private String displayTaReceipt = "";                                // Text area content showing receipt after checkout (Receipt Page)

    public void setCurrentUser(CustomerUser user) {
        this.currentUser = user;
    }


    //SELECT productID, description, image, unitPrice,inStock quantity
    void search() throws SQLException {
        String productItem = cusView.searchTextField.getText().trim();
        if (!productItem.equals("")) {
            productList = databaseRW.searchProduct(productItem); //search database
            if(productList != null ) {
                theProduct = productList.get(0);
                if (theProduct.getStockQuantity()>0){
                    double unitPrice = theProduct.getUnitPrice();
                    String description = theProduct.getProductDescription();
                    int stock = theProduct.getStockQuantity();

                    String baseInfo = String.format("Product_Id / Name: %s\n%s,\nPrice: £%.2f", productItem, description, unitPrice);
                    String quantityInfo = stock < 100 ? String.format("\n%d units left.", stock) : "";
                    displayLaSearchResult = baseInfo + quantityInfo;
                    System.out.println(displayLaSearchResult);
                }
            }else{
                theProduct=null;
                displayLaSearchResult = "No Product was found with " + productItem;
                System.out.println("No Product was found with " + productItem);
            }
        }else{
            theProduct=null;
            displayLaSearchResult = "Please type the product you would like to search for";
            System.out.println("Please type the product you would like to search for");
        }
        updateObservableProductList(UpdateForAction.BtnSearch);
    }

    void checkInfo() {
        theProduct = cusView.obrLvProducts.getSelectionModel().getSelectedItem();
        if(theProduct!= null) {
            double unitPrice = theProduct.getUnitPrice();
            String description = theProduct.getProductDescription();
            int stock = theProduct.getStockQuantity();

            String baseInfo = String.format("Product_Id / Name: %s\n%s,\nPrice: £%.2f", theProduct, description, unitPrice);
            String quantityInfo = stock < 100 ? String.format("\n%d units left.", stock) : "";
            displayTaInfo = baseInfo + quantityInfo;
            System.out.println(displayLaSearchResult);
        }
        updateView();
    }

    void addToWishList(){
        System.out.println("Add to Wish List got called from controller");
        theProduct = cusView.obrLvProducts.getSelectionModel().getSelectedItem();
        if(theProduct!= null){

            makeOrganizedWishList();
            //displayTaWishList = ProductListFormatter.buildString(currentUser.getWishList()); //build a String for trolley so that we can show it
        }
        else{
            displayLaSearchResult = "Please search for an available product before adding it to the trolley";
            System.out.println("must search and get an available product before add to trolley");
        }
        displayTaReceipt=""; // Clear receipt to switch back to trolleyPage (receipt shows only when not empty)
        updateView();
    }

    void makeOrganizedWishList(){
        for ( Product product : currentUser.getWishList()) {
            if ( product.getProductId().equals(theProduct.getProductId())) {
                product.setOrderedQuantity(product.getOrderedQuantity() + theProduct.getOrderedQuantity());
                return;
            }
        }
        Product newProduct = new Product(theProduct.getProductId(),
                                        theProduct.getProductDescription(),
                                        theProduct.getProductImageName(),
                                        theProduct.getUnitPrice(),
                                        theProduct.getStockQuantity());
        currentUser.getWishList().add(newProduct);
        currentUser.getWishList().sort(Comparator.comparing(Product::getProductId));
    }

    void addToTrolley(){
        System.out.println("Add to trolley got called from controller");
        theProduct = cusView.obrLvProducts.getSelectionModel().getSelectedItem();
        if(theProduct!= null){

            // trolley.add(theProduct) — Product is appended to the end of the trolley.
            // To keep the trolley organized, add code here or call a method that:
            //TODO
            // 1. Merges items with the same product ID (combining their quantities).
            // 2. Sorts the products in the trolley by product ID.
            // trolley.add(theProduct);

            makeOrganizedTrolley();
            //displayTaTrolley = ProductListFormatter.buildString(currentUser.getTrolley()); //build a String for trolley so that we can show it
        }
        else{
            displayLaSearchResult = "Please search for an available product before adding it to the trolley";
            System.out.println("must search and get an available product before add to trolley");
        }
        displayTaReceipt=""; // Clear receipt to switch back to trolleyPage (receipt shows only when not empty)
        updateView();
    }

    void makeOrganizedTrolley(){
        for ( Product product : currentUser.getTrolley()) {
            if ( product.getProductId().equals(theProduct.getProductId())) {
                product.setOrderedQuantity(product.getOrderedQuantity() + theProduct.getOrderedQuantity());
                return;
            }
        }
        Product newProduct = new Product(theProduct.getProductId(),
                                    theProduct.getProductDescription(),
                                    theProduct.getProductImageName(),
                                    theProduct.getUnitPrice(),
                                    theProduct.getStockQuantity());
        currentUser.getTrolley().add(newProduct);
        currentUser.getTrolley().sort(Comparator.comparing(Product::getProductId));
    }

    public void addALLToWishList() {

    }

    public void addALLToTrolley() {

    }

    void checkOut() throws IOException, SQLException {
        if(!currentUser.getTrolley().isEmpty()){
            // Group the products in the trolley by productId to optimize stock checking
            // Check the database for sufficient stock for all products in the trolley.
            // If any products are insufficient, the update will be rolled back.
            // If all products are sufficient, the database will be updated, and insufficientProducts will be empty.
            // Note: If the trolley is already organized (merged and sorted), grouping is unnecessary.
            ArrayList<Product> groupedTrolley= groupProductsById(currentUser.getTrolley());
            ArrayList<Product> insufficientProducts= databaseRW.purchaseStocks(groupedTrolley);

            if(insufficientProducts.isEmpty()){ // If stock is sufficient for all products
                //get OrderHub and tell it to make a new Order

                OrderHub orderHub =OrderHub.getOrderHub();
                Order theOrder = orderHub.newOrder(currentUser.getTrolley());
                currentUser.getTrolley().clear();
                displayTaReceipt = String.format(
                        "Order_ID: %s\nOrdered_Date_Time: %s\n%s",
                        theOrder.getOrderId(),
                        theOrder.getOrderedDateTime(),
                        ProductListFormatter.buildString(theOrder.getProductList())
                );
                System.out.println(displayTaReceipt);

                currentUser.addPurchaseHistory(displayTaReceipt);
                System.out.println("Added lastest purchase to history");
            }
            else{ // Some products have insufficient stock — build an error message to inform the customer
                StringBuilder errorMsg = new StringBuilder();
                for(Product p : insufficientProducts){
                    errorMsg.append("\u2022 "+ p.getProductId()).append(", ")
                            .append(p.getProductDescription()).append(" (Only ")
                            .append(p.getStockQuantity()).append(" available, ")
                            .append(p.getOrderedQuantity()).append(" requested)\n");
                }
                theProduct=null;

                //TODO
                // Add the following logic here:
                // 1. Remove products with insufficient stock from the trolley.
                // 2. Trigger a message window to notify the customer about the insufficient stock, rather than directly changing displayLaSearchResult.
                //You can use the provided RemoveProductNotifier class and its showRemovalMsg method for this purpose.
                //remember close the message window where appropriate (using method closeNotifierWindow() of RemoveProductNotifier class)
                displayLaSearchResult = "Checkout failed due to insufficient stock for the following products:\n" + errorMsg.toString();
                System.out.println("stock is not enough");
            }
        }
        else{
            cusView.update("","","Your trolley is empty", "");
            System.out.println("Your trolley is empty");
        }
        updateView();
    }

    /**
     * Groups products by their productId to optimize database queries and updates.
     * By grouping products, we can check the stock for a given `productId` once, rather than repeatedly
     */
    private ArrayList<Product> groupProductsById(ArrayList<Product> proList) {
        Map<String, Product> grouped = new HashMap<>();
        for (Product p : proList) {
            String id = p.getProductId();
            if (grouped.containsKey(id)) {
                Product existing = grouped.get(id);
                existing.setOrderedQuantity(existing.getOrderedQuantity() + p.getOrderedQuantity());
            } else {
                // Make a shallow copy to avoid modifying the original
                grouped.put(id,new Product(p.getProductId(),p.getProductDescription(),
                        p.getProductImageName(),p.getUnitPrice(),p.getStockQuantity()));
            }
        }
        return new ArrayList<>(grouped.values());
    }

    void cancel(){
        currentUser.getTrolley().clear();
        updateView();
    }
    void closeReceipt(){
        displayTaReceipt="";
    }

    private void updateObservableProductList(UpdateForAction updateFor) {
        switch (updateFor) {
            case UpdateForAction.BtnSearch:
                cusView.updateObservableProductList(productList);
                break;
        }
    }

    void updateView() {
        theProduct = cusView.obrLvProducts.getSelectionModel().getSelectedItem();
        if(theProduct != null){
            imageName = theProduct.getProductImageName();
            String relativeImageUrl = StorageLocation.imageFolder +imageName; //relative file path, eg images/0001.jpg
            // Get the full absolute path to the image
            Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
            imageName = imageFullPath.toUri().toString(); //get the image full Uri then convert to String
            System.out.println("Image absolute path: " + imageFullPath); // Debugging to ensure path is correct
        }
        else{
            imageName = "imageHolder.jpg";
        }
        String wishList = ProductListFormatter.buildString(currentUser.getWishList());
        String trolley = ProductListFormatter.buildString(currentUser.getTrolley());

        cusView.update(displayTaInfo,wishList, trolley, displayTaReceipt);
    }
     // extra notes:
     //Path.toUri(): Converts a Path object (a file or a directory path) to a URI object.
     //File.toURI(): Converts a File object (a file on the filesystem) to a URI object

    //for test only
    public ArrayList<Product> getWishList() { return currentUser.getWishList();}
    public ArrayList<Product> getTrolley() {
        return currentUser.getTrolley();
    }
    public void setTheProduct(Product theProduct) {
        this.theProduct = theProduct;
    }
}
