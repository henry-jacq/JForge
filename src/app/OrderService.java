package app;

import jdi.annotations.Inject;
import jdi.annotations.Service;

@Service
public class OrderService {
    
    private final PaymentInterface paymentService;
    
    @Inject
    public OrderService(PaymentInterface paymentService) {
        this.paymentService = paymentService;
    }
    
    public void processOrder(String orderId, double amount) {
        System.out.println("Processing order: " + orderId);
        System.out.println("Using payment method: " + paymentService.getPaymentMethod());
        paymentService.processPayment(amount);
        System.out.println("Order " + orderId + " completed successfully");
    }
    
    public PaymentInterface getPaymentService() {
        return paymentService;
    }
}