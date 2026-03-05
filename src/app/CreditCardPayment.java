package app;

import jdi.annotations.Service;

@Service
public class CreditCardPayment implements PaymentInterface {
    
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment of $" + amount);
    }
    
    @Override
    public String getPaymentMethod() {
        return "Credit Card";
    }
}