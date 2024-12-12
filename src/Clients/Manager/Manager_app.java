package Clients.Manager;

import Clients.Manager.View.*;
import BackEnd.DataLayer;
import Clients.Manager.Controller.*;

public class Manager_app {
    private static Manager_app instance;

    public static Manager_app getInstance() {
        if (instance == null) {
           instance = new Manager_app();
        }
  
        return instance;
    }
    private DataLayer dataLayer = new DataLayer();
    private ManagerMainView ManagerMainScreen = new ManagerMainView();
    private InventoryView InventoryScreen = new InventoryView(null);
    private SalesReportView SalesReportScreen = new SalesReportView(null);

    public ManagerMainController managerMainController;
    public InventoryController inventoryController;
    public SalesReportController salesReportController;


    public ManagerMainView getManagerMainView() {
        return ManagerMainScreen;
    }

    public InventoryView getInventoryView(){
        return InventoryScreen;
    }

    public SalesReportView getSalesReportView(){
        return SalesReportScreen;
    }

    private Manager_app(){
        managerMainController = new ManagerMainController(ManagerMainScreen);
        inventoryController = new InventoryController(InventoryScreen, dataLayer);
        salesReportController = new SalesReportController(SalesReportScreen, dataLayer);
    }
    public static void main(String[] var0) {
        getInstance().getManagerMainView().setVisible(true);
    }
}