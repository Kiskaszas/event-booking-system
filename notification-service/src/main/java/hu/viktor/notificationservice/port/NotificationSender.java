package hu.viktor.notificationservice.port;

public interface NotificationSender {
    void sendOrderConfirmation(String toEmail, String eventId, int quantity);
}