package bg.tshirt.database.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderPageDTO {
    private Long id;

    @JsonIgnore
    private Instant createdAt;

    private String address;

    private List<OrderItemPageDTO> items;

    public String createdAt() {
        if (createdAt == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault());
        return formatter.format(createdAt);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderItemPageDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemPageDTO> items) {
        this.items = items;
    }
}
