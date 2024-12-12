package Clients.Cashier;

import Clients.Cashier.View.*;
import BackEnd.DataLayer;
import Clients.Cashier.Controller.*;

public class Cashier_app {
    private static Cashier_app instance;

    public static Cashier_app getInstance() {
        if (instance == null) {
           instance = new Cashier_app();
        }
  
        return instance;
    }
    private DataLayer dataLayer = new DataLayer();
    private CashierMainView CashierMainScreen = new CashierMainView();
    private OrderView OrderScreen = new OrderView(null);
    private UpdateInventoryView UpdateInventoryScreen = new UpdateInventoryView(null);


    public CashierMainController cashierMainController;
    public OrderController orderController;
    public UpdateInventoryController updateInventoryController;


    public CashierMainView getCashierMainView() {
        return CashierMainScreen;
    }

    public OrderView getOrderView(){
        return OrderScreen;
    }

    public UpdateInventoryView getUpdateInventoryView(){
        return UpdateInventoryScreen;
    }

    private Cashier_app(){
        cashierMainController = new CashierMainController(CashierMainScreen);
        orderController = new OrderController(OrderScreen, dataLayer);
        updateInventoryController = new UpdateInventoryController(UpdateInventoryScreen);

    }
    public static void main(String[] var0) {
        getInstance().getCashierMainView().setVisible(true);
    }
}