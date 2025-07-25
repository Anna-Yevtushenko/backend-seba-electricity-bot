package com.example.electricity_bot.repositories;
import com.example.electricity_bot.model.DeviceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeviceStatusHistoryRepository extends JpaRepository<DeviceHistory, Long> {
}
