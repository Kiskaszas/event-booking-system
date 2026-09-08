package hu.viktor.orderservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;
    private String customerEmail;
    private int quantity;
    private String status;
    private LocalDateTime orderDate;

    public static Order create(String eventId, String customerEmail, int quantity) {
        Order order = new Order();
        order.setEventId(eventId);
        order.setCustomerEmail(customerEmail);
        order.setQuantity(quantity);
        order.setStatus("CONFIRMED");
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
}