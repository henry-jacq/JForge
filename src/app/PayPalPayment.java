package app;

import jdi.annotations.Service;

@Service
public class PayPalPayment implements PaymentInterface {
    
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing PayPal payment of $" + amount);
    }
    
    @Override
    public String getPaymentMethod() {
        return "PayPal";
    }
}