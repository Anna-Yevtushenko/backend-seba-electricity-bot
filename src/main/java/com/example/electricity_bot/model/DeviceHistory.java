package com.example.electricity_bot.model;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "device_history")
public class DeviceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="device_uuid", referencedColumnName = "device_uuid" )
    private Device device;

    private String status;
    @UpdateTimestamp
    private LocalDateTime timestamp;
}
