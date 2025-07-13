package com.example.electricity_bot.repositories;
import com.example.electricity_bot.model.Device;
import com.example.electricity_bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    List<Device> findAllByUser(User user);
}
