package app;

public interface PaymentInterface {
    void processPayment(double amount);
    String getPaymentMethod();
}