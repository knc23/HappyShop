package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.UIStyle;
import ci553.happyshop.utility.WinPosManager;
import ci553.happyshop.utility.WindowBounds;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The CustomerView is separated into two sections by a line :
 *
 * 1. Search Page – Always visible, allowing customers to browse and search for products.
 * 2. the second page – display either the Trolley Page or the Receipt Page
 *    depending on the current context. Only one of these is shown at a time.
 */

public class CustomerView  {
    public CustomerController cusController;

    private final int WIDTH = UIStyle.customerWinWidth;
    private final int HEIGHT = UIStyle.customerWinHeight;
    private final int COLUMN_WIDTH = WIDTH / 2 - 10;

    private HBox hbRoot; // Top-level layout manager
    private VBox welcomePage;
    private VBox loginPage;
    private VBox searchPage;
    private VBox wishListPage;
    private VBox trolleyPage;  //vbTrolleyPage and vbReceiptPage will swap with each other when need
    private VBox historyPage;
    private VBox receiptPage;

    TextField searchTextField; //for user input on the search page. Made accessible so it can be accessed or modified by CustomerModel
    public Label laSearchSummary; //eg. the lable shows "3 products found" after search
    public ObservableList<Product> obeProductList; //observable product list
    ListView<Product> obrLvProducts; //A ListView observes the product list

    //four controllers needs updating when program going on
    private ImageView ivProduct; //image area in searchPage
    private Label lbProductInfo;//product text info in searchPage
    private TextArea taWishList;
    private TextArea taTrolley; //in trolley Page
    private TextArea taReceipt;//in receipt page

    // Holds a reference to this CustomerView window for future access and management
    // (e.g., positioning the removeProductNotifier when needed).
    private Stage viewWindow;

    public void start(Stage window) {
        VBox vbSearchPage = createSearchPage();
        trolleyPage = trolleyPage();
        wishListPage = wishListPage();
        //receiptPage = receiptPage();

        hbRoot = new HBox(10, vbSearchPage); //initialize to show trolleyPage
        hbRoot.setAlignment(Pos.CENTER);
        hbRoot.setStyle(UIStyle.rootStyle);

        Scene scene = new Scene(hbRoot, WIDTH, HEIGHT);
        window.setScene(scene);
        window.setTitle("🛒 HappyShop Customer Client");
        WinPosManager.registerWindow(window,WIDTH,HEIGHT); //calculate position x and y for this window
        window.show();
        viewWindow=window;// Sets viewWindow to this window for future reference and management.
    }

    private VBox createSearchPage() {
        Label laShopName = new Label("HappyShop");
        laShopName.setStyle(UIStyle.labelTitleStyle);

        Button btnWishList = new Button("My Wish List");
        btnWishList.setStyle(UIStyle.buttonStyle);
        btnWishList.setOnAction(this::buttonClicked);

        Button btnTrolley = new Button("My Trolley");
        btnTrolley.setStyle(UIStyle.buttonStyle);
        btnTrolley.setOnAction(this::buttonClicked);

        Button btnHistory = new Button("My History");
        btnHistory.setStyle(UIStyle.buttonStyle);
        btnHistory.setOnAction(this::buttonClicked);

        HBox hbTitleFuncBtn = new HBox(10, laShopName, btnWishList, btnTrolley, btnHistory);

        //--------------------------------------------------------------------------------------------------------------

        Label laPageTitle = new Label("Search by Product ID/Name");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        //--------------------------------------------------------------------------------------------------------------

        Label laId = new Label("ID / Name:      ");
        laId.setStyle(UIStyle.labelStyle);
        searchTextField = new TextField();
        searchTextField.setPromptText("eg. 0001 / TV");
        searchTextField.setStyle(UIStyle.textFiledStyle);
        Button btnSearch = new Button("Search");
        btnSearch.setStyle(UIStyle.buttonStyle);
        btnSearch.setOnAction(this::buttonClicked);

        HBox hbSearchTextField = new HBox(10, laId, searchTextField, btnSearch);

        //--------------------------------------------------------------------------------------------------------------

        laSearchSummary = new Label("Search Summary");
        laSearchSummary.setStyle(UIStyle.labelStyle);

        //--------------------------------------------------------------------------------------------------------------

        obeProductList = FXCollections.observableArrayList();
        obrLvProducts = new ListView<>(obeProductList);//ListView proListView observes proList
        obrLvProducts.setPrefHeight(HEIGHT - 100);
        obrLvProducts.setFixedCellSize(50);
        obrLvProducts.setStyle(UIStyle.listViewStyle);

        obrLvProducts.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);

                if (empty || product == null) {
                    setGraphic(null);
                    System.out.println("setCellFactory - empty item");
                } else {
                    String imageName = product.getProductImageName(); // Get image name (e.g. "0001.jpg")
                    String relativeImageUrl = StorageLocation.imageFolder + imageName;
                    // Get the full absolute path to the image
                    Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
                    String imageFullUri = imageFullPath.toUri().toString();// Build the full image Uri

                    ImageView ivPro;
                    try {
                        ivPro = new ImageView(new Image(imageFullUri, 50,45, true,true)); // Attempt to load the product image
                    } catch (Exception e) {
                        // If loading fails, use a default image directly from the resources folder
                        ivPro = new ImageView(new Image("imageHolder.jpg",50,45,true,true)); // Directly load from resources
                    }

                    Label laProToString = new Label(product.toString()); // Create a label for product details
                    HBox hbox = new HBox(10, ivPro, laProToString); // Put ImageView and label in a horizontal layout
                    setGraphic(hbox);  // Set the whole row content
                }
            }
        });

        //--------------------------------------------------------------------------------------------------------------

        Button btnAddToWishList = new Button("Add to Wish List");
        btnAddToWishList.setStyle(UIStyle.buttonStyle);
        btnAddToWishList.setOnAction(this::buttonClicked);

        Button btnAddToTrolley = new Button("Add to Trolley");
        btnAddToTrolley.setStyle(UIStyle.buttonStyle);
        btnAddToTrolley.setOnAction(this::buttonClicked);

        HBox hbBtns = new HBox(10, btnAddToWishList, btnAddToTrolley);

        //--------------------------------------------------------------------------------------------------------------

        VBox vbSearchPage = new VBox(15, hbTitleFuncBtn, laPageTitle, hbSearchTextField, laSearchSummary, obrLvProducts, hbBtns);
        vbSearchPage.setPrefWidth(COLUMN_WIDTH);
        vbSearchPage.setAlignment(Pos.TOP_CENTER);
        vbSearchPage.setStyle("-fx-padding: 3px;");

        return vbSearchPage;
    }

    private VBox wishListPage() {
        Label laShopName = new Label("HappyShop");
        laShopName.setStyle(UIStyle.labelTitleStyle);

        //--------------------------------------------------------------------------------------------------------------

        Label laPageTitle = new Label("Wish List");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        //--------------------------------------------------------------------------------------------------------------

        taWishList = new TextArea();
        taWishList.setEditable(false);
        taWishList.setPrefSize(WIDTH/2, HEIGHT-50);

        //--------------------------------------------------------------------------------------------------------------

        Button btnBack = new Button("Back");
        btnBack.setOnAction(this::buttonClicked);
        btnBack.setStyle(UIStyle.buttonStyle);

        Button btnCancel = new Button("Cancel");
        btnCancel.setOnAction(this::buttonClicked);
        btnCancel.setStyle(UIStyle.buttonStyle);

        Button btnAddToTrolley = new Button("Add to Trolley");
        btnAddToTrolley.setOnAction(this::buttonClicked);
        btnAddToTrolley.setStyle(UIStyle.buttonStyle);

        Button btnCheckout = new Button("Check Out");
        btnCheckout.setOnAction(this::buttonClicked);
        btnCheckout.setStyle(UIStyle.buttonStyle);

        HBox hbBtns = new HBox(10, btnBack ,btnCancel,btnAddToTrolley, btnCheckout);
        hbBtns.setStyle("-fx-padding: 15px;");
        hbBtns.setAlignment(Pos.CENTER);

        //--------------------------------------------------------------------------------------------------------------

        wishListPage = new VBox(15, laShopName, laPageTitle, taWishList, hbBtns);
        wishListPage.setPrefWidth(COLUMN_WIDTH);
        wishListPage.setAlignment(Pos.TOP_CENTER);
        wishListPage.setStyle("-fx-padding: 15px;");
        return wishListPage;
    }

    private VBox trolleyPage() {
        Label laShopName = new Label("HappyShop");
        laShopName.setStyle(UIStyle.labelTitleStyle);

        //--------------------------------------------------------------------------------------------------------------

        Label laPageTitle = new Label("🛒🛒  Trolley 🛒🛒");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        //--------------------------------------------------------------------------------------------------------------

        taTrolley = new TextArea();
        taTrolley.setEditable(false);
        taTrolley.setPrefSize(WIDTH/2, HEIGHT-50);

        //--------------------------------------------------------------------------------------------------------------

        Button btnBack = new Button("Back");
        btnBack.setOnAction(this::buttonClicked);
        btnBack.setStyle(UIStyle.buttonStyle);

        Button btnCancel = new Button("Cancel");
        btnCancel.setOnAction(this::buttonClicked);
        btnCancel.setStyle(UIStyle.buttonStyle);

        Button btnSaveToWishList = new Button("Save to Wish List");
        btnSaveToWishList.setOnAction(this::buttonClicked);
        btnSaveToWishList.setStyle(UIStyle.buttonStyle);

        Button btnCheckout = new Button("Check Out");
        btnCheckout.setOnAction(this::buttonClicked);
        btnCheckout.setStyle(UIStyle.buttonStyle);

        HBox hbBtns = new HBox(10, btnBack ,btnCancel,btnSaveToWishList, btnCheckout);
        hbBtns.setStyle("-fx-padding: 15px;");
        hbBtns.setAlignment(Pos.CENTER);

        //--------------------------------------------------------------------------------------------------------------

        trolleyPage = new VBox(15, laShopName, laPageTitle, taTrolley, hbBtns);
        trolleyPage.setPrefWidth(COLUMN_WIDTH);
        trolleyPage.setAlignment(Pos.TOP_CENTER);
        trolleyPage.setStyle("-fx-padding: 15px;");
        return trolleyPage;
    }

    private VBox createReceiptPage() {
        Label laPageTitle = new Label("Receipt");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        taReceipt = new TextArea();
        taReceipt.setEditable(false);
        taReceipt.setPrefSize(WIDTH/2, HEIGHT-50);

        Button btnCloseReceipt = new Button("OK & Close"); //btn for closing receipt and showing trolley page
        btnCloseReceipt.setStyle(UIStyle.buttonStyle);

        btnCloseReceipt.setOnAction(this::buttonClicked);

        receiptPage = new VBox(15, laPageTitle, taReceipt, btnCloseReceipt);
        receiptPage.setPrefWidth(COLUMN_WIDTH);
        receiptPage.setAlignment(Pos.TOP_CENTER);
        receiptPage.setStyle(UIStyle.rootStyleYellow);
        return receiptPage;
    }


    private void buttonClicked(ActionEvent event) {
        try{
            Button btn = (Button)event.getSource();
            String action = btn.getText();
            if(action.equals("Back")) {
                showWishListTrolleyOrReceiptPage(createSearchPage());
            }
            if(action.equals("My Wish List")) {
                showWishListTrolleyOrReceiptPage(wishListPage);
            }
            if(action.equals("My Trolley")) {
                showWishListTrolleyOrReceiptPage(trolleyPage);
            }
            if(action.equals("Add to Wish List") && obrLvProducts.getSelectionModel().getSelectedItem()!=null) {
                System.out.println("Added to Wish List");
            }
            if(action.equals("Add to Trolley") && obrLvProducts.getSelectionModel().getSelectedItem()!=null) {
                System.out.println("Added to Trolley");
            }
            if(action.equals("OK & Close")){
                showWishListTrolleyOrReceiptPage(trolleyPage);
            }
            cusController.doAction(action);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public void update(String wishList, String trolley, String receipt) {

        taWishList.setText(wishList);
        taTrolley.setText(trolley);

        if (!receipt.equals("")) {
            showWishListTrolleyOrReceiptPage(receiptPage);
            taReceipt.setText(receipt);
        }
    }

    void updateObservableProductList( ArrayList<Product> productList) {
        int proCounter = productList.size();
        System.out.println(proCounter);
        laSearchSummary.setText(proCounter + " products found");
        laSearchSummary.setVisible(true);
        obeProductList.clear();
        obeProductList.addAll(productList);
    }



    // Replaces the last child of hbRoot with the specified page.
    // the last child is either vbTrolleyPage or vbReceiptPage.
    private void showWishListTrolleyOrReceiptPage(Node pageToShow) {
        int lastIndex = hbRoot.getChildren().size() - 1;
        if (lastIndex >= 0) {
            hbRoot.getChildren().set(lastIndex, pageToShow);
        }
    }

    WindowBounds getWindowBounds() {
        return new WindowBounds(viewWindow.getX(), viewWindow.getY(),
                  viewWindow.getWidth(), viewWindow.getHeight());
    }
}
