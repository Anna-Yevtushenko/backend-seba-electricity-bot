package com.example.electricity_bot.repositories;
import com.example.electricity_bot.model.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeviceStatusRepository extends JpaRepository<DeviceStatus, String> {
    List<DeviceStatus> findByTimestampBeforeAndStatusNot(LocalDateTime timestamp, String status);


}
