package com.example.electricity_bot.model;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="device_status")
public class DeviceStatus {
    @Id
    @Column(name = "device_uuid")
    private String deviceUuid;

    private String status;

    @UpdateTimestamp
    private LocalDateTime timestamp;

    @OneToOne
    @MapsId
    @JoinColumn(name = "device_uuid", referencedColumnName = "device_uuid")
    private Device device;
}
