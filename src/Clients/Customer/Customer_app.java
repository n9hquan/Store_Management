package Clients.Customer;

import Clients.Customer.View.*;
import BackEnd.DataLayer;
import Clients.Customer.Controller.*;

public class Customer_app {
    private static Customer_app instance;

    public static Customer_app getInstance() {
        if (instance == null) {
           instance = new Customer_app();
        }
  
        return instance;
    }
    private DataLayer dataLayer = new DataLayer();
    private CustomerMainView CustomerMainScreen = new CustomerMainView();
    private SelfOrderView SelfOrderScreen = new SelfOrderView(null);
    private SelfOrderHistoryView SelfOrderHistoryScreen = new SelfOrderHistoryView(null);
    private UpdateProfileView UpdateProfileScreen = new UpdateProfileView(null);
    private UpdatePaymentView UpdatePaymentScreen = new UpdatePaymentView(null);

    public CustomerMainController customerMainController;
    public SelfOrderController selfOrderController;
    public SelfOrderHistoryController selfOrderHistoryController;
    public UpdateProfileController updateProfileController;
    public UpdatePaymentController updatePaymentController;


    public CustomerMainView getCustomerMainView() {
        return CustomerMainScreen;
    }

    public SelfOrderView getSelfOrderView(){
        return SelfOrderScreen;
    }

    public SelfOrderHistoryView getOrderHistoryView(){
        return SelfOrderHistoryScreen;
    }

    public UpdateProfileView getUpdateProfileView(){
        return UpdateProfileScreen;
    }

    public UpdatePaymentView getUpdatePaymentInfoView(){
        return UpdatePaymentScreen;
    }

    private Customer_app(){
        customerMainController = new CustomerMainController(CustomerMainScreen);
        selfOrderController = new SelfOrderController(SelfOrderScreen, dataLayer);
        selfOrderHistoryController = new SelfOrderHistoryController(SelfOrderHistoryScreen, dataLayer);
        updateProfileController = new UpdateProfileController(UpdateProfileScreen);
        updatePaymentController = new UpdatePaymentController(UpdatePaymentScreen);
    }
    public static void main(String[] var0) {
        getInstance().getCustomerMainView().setVisible(true);
    }
}