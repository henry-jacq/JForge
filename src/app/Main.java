package app;

import jdi.container.ApplicationContext;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize the DI container with the app package
            ApplicationContext context = new ApplicationContext("app");
            
            System.out.println("=== Interface-Based Dependency Injection Demo ===\n");
            
            // Get the OrderService - it should have a PaymentInterface implementation injected
            OrderService orderService = context.getBean(OrderService.class);
            
            if (orderService == null) {
                throw new RuntimeException("OrderService not found in container");
            }
            
            // Check which payment implementation was injected
            PaymentInterface paymentService = orderService.getPaymentService();
            if (paymentService == null) {
                throw new RuntimeException("PaymentInterface not injected into OrderService");
            }
            
            System.out.println("✓ OrderService created successfully");
            System.out.println("✓ PaymentInterface injected: " + paymentService.getClass().getSimpleName());
            System.out.println("✓ Payment method: " + paymentService.getPaymentMethod());
            
            // Test the functionality
            System.out.println("\n--- Testing Order Processing ---");
            orderService.processOrder("ORD-001", 99.99);
            
            // Demonstrate that we can get both payment implementations
            System.out.println("\n--- Available Payment Implementations ---");
            PayPalPayment paypal = context.getBean(PayPalPayment.class);
            CreditCardPayment creditCard = context.getBean(CreditCardPayment.class);
            
            if (paypal != null) {
                System.out.println("✓ PayPalPayment available: " + paypal.getPaymentMethod());
            }
            if (creditCard != null) {
                System.out.println("✓ CreditCardPayment available: " + creditCard.getPaymentMethod());
            }
            
            // Show which one was actually injected
            System.out.println("\n--- Injection Analysis ---");
            System.out.println("Injected implementation: " + paymentService.getClass().getSimpleName());
            System.out.println("Same as PayPal instance: " + (paymentService == paypal));
            System.out.println("Same as CreditCard instance: " + (paymentService == creditCard));
            
            System.out.println("\n🎉 Interface-based DI demonstration completed successfully!");
            
        } catch (Exception e) {
            System.err.println("✗ Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}