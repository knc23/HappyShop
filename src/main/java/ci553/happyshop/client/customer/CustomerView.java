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
    private VBox vbRoot;
    private HBox hbShopMenu;
    private VBox vbInfoPage;
    private VBox vbWishListPage;
    private VBox vbTrolleyPage;  //vbTrolleyPage and vbReceiptPage will swap with each other when need
    private VBox vbHistoryPage;
    private VBox vbReceiptPage;

    TextField searchTextField; //for user input on the search page. Made accessible so it can be accessed or modified by CustomerModel
    public Label laSearchSummary; //eg. the lable shows "3 products found" after search
    public ObservableList<Product> obeProductList; //observable product list
    ListView<Product> obrLvProducts; //A ListView observes the product list

    //four controllers needs updating when program going on
    private ImageView ivProduct; //image area in searchPage
    private Label lbProductInfo;//product text info in searchPage
    private TextArea taInfo;
    private TextArea taWishList;
    private TextArea taTrolley; //in trolley Page
    private TextArea taHistory;
    private TextArea taReceipt;//in receipt page

    // Holds a reference to this CustomerView window for future access and management
    // (e.g., positioning the removeProductNotifier when needed).
    private Stage viewWindow;

    public void start(Stage window) {
        VBox vbSearchPage = createSearchPage();
        hbShopMenu = createShopMenuPage();
        vbInfoPage = CreateInformationPage();
        vbWishListPage = CreateWishListPage();
        vbTrolleyPage = CreateTrolleyPage();
        vbHistoryPage = createHistoryPage();
        vbReceiptPage = createReceiptPage();

        // Create a divider line
        Line line = new Line(0, 0, 0, HEIGHT);
        line.setStrokeWidth(4);
        line.setStroke(Color.PINK);
        VBox lineContainer = new VBox(line);
        lineContainer.setPrefWidth(4); // Give it some space
        lineContainer.setAlignment(Pos.CENTER);


        hbRoot = new HBox(10, vbSearchPage, lineContainer, vbInfoPage); //initialize to show trolleyPage

        vbRoot = new VBox(10,hbShopMenu, hbRoot);
        vbRoot.setAlignment(Pos.CENTER);
        vbRoot.setStyle(UIStyle.rootStyle);

        Scene scene = new Scene(vbRoot, WIDTH, HEIGHT);
        window.setScene(scene);
        window.setTitle("🛒 HappyShop Customer Client");
        WinPosManager.registerWindow(window,WIDTH,HEIGHT); //calculate position x and y for this window
        window.show();
        viewWindow=window;// Sets viewWindow to this window for future reference and management.
    }

/// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private HBox createShopMenuPage() {
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

        HBox hbShopMenu = new HBox(10, laShopName, btnWishList, btnTrolley, btnHistory);
        hbShopMenu.setPrefWidth(COLUMN_WIDTH);
        hbShopMenu.setAlignment(Pos.CENTER);
        hbShopMenu.setStyle("-fx-padding: 15px;");

        return hbShopMenu;
    }

/// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private VBox createSearchPage() {
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

        Button btnCheckInfo = new Button("More Information");
        btnCheckInfo.setOnAction(this::buttonClicked);
        btnCheckInfo.setStyle(UIStyle.buttonStyle);

        Button btnAddToWishList = new Button("Add to Wish List");
        btnAddToWishList.setOnAction(this::buttonClicked);
        btnAddToWishList.setStyle(UIStyle.buttonStyle);

        Button btnAddToTrolley = new Button("Add to Trolley");
        btnAddToTrolley.setStyle(UIStyle.buttonStyle);
        btnAddToTrolley.setOnAction(this::buttonClicked);

        HBox hbBtns = new HBox(10, btnCheckInfo, btnAddToWishList, btnAddToTrolley);

        //--------------------------------------------------------------------------------------------------------------

        VBox vbSearchPage = new VBox(15, laPageTitle, hbSearchTextField, laSearchSummary, obrLvProducts, hbBtns);
        vbSearchPage.setPrefWidth(COLUMN_WIDTH);
        vbSearchPage.setAlignment(Pos.TOP_CENTER);
        vbSearchPage.setStyle("-fx-padding: 15px;");

        return vbSearchPage;
    }

    /// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private VBox CreateInformationPage() {
        Label laPageTitle = new Label("Product information");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        //--------------------------------------------------------------------------------------------------------------

        taInfo = new TextArea();
        taInfo.setEditable(false);
        taInfo.setPrefSize(WIDTH/2, HEIGHT-50);

        //--------------------------------------------------------------------------------------------------------------

        vbInfoPage = new VBox(15, laPageTitle, taInfo);
        vbInfoPage.setPrefWidth(COLUMN_WIDTH);
        vbInfoPage.setAlignment(Pos.TOP_CENTER);
        vbInfoPage.setStyle("-fx-padding: 15px;");

        return vbInfoPage;
    }

    /// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private VBox CreateWishListPage() {
        Label laPageTitle = new Label("Wish List");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        //--------------------------------------------------------------------------------------------------------------

        taWishList = new TextArea();
        taWishList.setEditable(false);
        taWishList.setPrefSize(WIDTH/2, HEIGHT-50);

        //--------------------------------------------------------------------------------------------------------------

        Button btnCancel = new Button("Cancel");
        btnCancel.setOnAction(this::buttonClicked);
        btnCancel.setStyle(UIStyle.buttonStyle);

        Button btnAddToTrolley = new Button("Add to Trolley");
        btnAddToTrolley.setOnAction(this::buttonClicked);
        btnAddToTrolley.setStyle(UIStyle.buttonStyle);

        HBox hbBtns = new HBox(10, btnCancel, btnAddToTrolley);
        hbBtns.setStyle("-fx-padding: 15px;");
        hbBtns.setAlignment(Pos.CENTER);

        //--------------------------------------------------------------------------------------------------------------

        vbWishListPage = new VBox(15, laPageTitle, taWishList, hbBtns);
        vbWishListPage.setPrefWidth(COLUMN_WIDTH);
        vbWishListPage.setAlignment(Pos.TOP_CENTER);
        vbWishListPage.setStyle("-fx-padding: 15px;");

        return vbWishListPage;
    }

    /// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private VBox CreateTrolleyPage() {
        Label laPageTitle = new Label("🛒🛒 Trolley 🛒🛒");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        //--------------------------------------------------------------------------------------------------------------

        taTrolley = new TextArea();
        taTrolley.setEditable(false);
        taTrolley.setPrefSize(WIDTH/2, HEIGHT-50);

        //--------------------------------------------------------------------------------------------------------------

        Button btnCancel = new Button("Cancel");
        btnCancel.setOnAction(this::buttonClicked);
        btnCancel.setStyle(UIStyle.buttonStyle);

        Button btnAddToWishList = new Button("Add to Wish List");
        btnAddToWishList.setOnAction(this::buttonClicked);
        btnAddToWishList.setStyle(UIStyle.buttonStyle);

        Button btnCheckout = new Button("Check Out");
        btnCheckout.setOnAction(this::buttonClicked);
        btnCheckout.setStyle(UIStyle.buttonStyle);

        HBox hbBtns = new HBox(10, btnCancel, btnAddToWishList,btnCheckout);
        hbBtns.setStyle("-fx-padding: 15px;");
        hbBtns.setAlignment(Pos.CENTER);

        //--------------------------------------------------------------------------------------------------------------

        vbTrolleyPage = new VBox(15, laPageTitle, taTrolley, hbBtns);
        vbTrolleyPage.setPrefWidth(COLUMN_WIDTH);
        vbTrolleyPage.setAlignment(Pos.TOP_CENTER);
        vbTrolleyPage.setStyle("-fx-padding: 15px;");
        return vbTrolleyPage;
    }

    /// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private VBox createHistoryPage() {
        Label laPageTitle = new Label("History");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        //--------------------------------------------------------------------------------------------------------------

        taHistory = new TextArea();
        taHistory.setEditable(false);
        taHistory.setPrefSize(WIDTH/2, HEIGHT-50);

        //--------------------------------------------------------------------------------------------------------------

        vbHistoryPage = new VBox(15, laPageTitle, taHistory);
        vbHistoryPage.setPrefWidth(COLUMN_WIDTH);
        vbHistoryPage.setAlignment(Pos.TOP_CENTER);
        vbHistoryPage.setStyle("-fx-padding: 15px;");

        return vbHistoryPage;
    }

    /// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private VBox createReceiptPage() {
        Label laPageTitle = new Label("Receipt");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        //--------------------------------------------------------------------------------------------------------------

        taReceipt = new TextArea();
        taReceipt.setEditable(false);
        taReceipt.setPrefSize(WIDTH/2, HEIGHT-50);

        //--------------------------------------------------------------------------------------------------------------

        Button btnCloseReceipt = new Button("OK & Close"); //btn for closing receipt and showing trolley page
        btnCloseReceipt.setStyle(UIStyle.buttonStyle);
        btnCloseReceipt.setOnAction(this::buttonClicked);

        //--------------------------------------------------------------------------------------------------------------

        vbReceiptPage = new VBox(15, laPageTitle, taReceipt, btnCloseReceipt);
        vbReceiptPage.setPrefWidth(COLUMN_WIDTH);
        vbReceiptPage.setAlignment(Pos.TOP_CENTER);
        vbReceiptPage.setStyle(UIStyle.rootStyleYellow);
        return vbReceiptPage;
    }

    /// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void buttonClicked(ActionEvent event) {
        try{
            Button btn = (Button)event.getSource();
            String action = btn.getText();
            if(action.equals("My Wish List")) {
                showPage(vbWishListPage);
            }
            if(action.equals("My Trolley")) {
                showPage(vbTrolleyPage);
            }
            if(action.equals("My History")) {
                showPage(vbHistoryPage);
            }
            if(action.equals("More Information") && obrLvProducts.getSelectionModel().getSelectedItem()!=null) {
                showPage(vbInfoPage);
            }
            if(action.equals("Add to Wish List") && obrLvProducts.getSelectionModel().getSelectedItem()!=null) {

            }
            if(action.equals("Add to Trolley") && obrLvProducts.getSelectionModel().getSelectedItem()!=null) {

            }
            if(action.equals("OK & Close")){
                showPage(vbInfoPage);
            }
            cusController.doAction(action);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public void update(String info, String wishList, String trolley, String receipt) {

        taInfo.setText(info);
        taWishList.setText(wishList);
        taTrolley.setText(trolley);
        if (!receipt.equals("")) {
            showPage(vbReceiptPage);
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
    private void showPage(Node pageToShow) {
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
