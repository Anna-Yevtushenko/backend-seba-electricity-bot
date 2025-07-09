package com.example.electricity_bot.repositories;
import com.example.electricity_bot.model.DeviceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface
DeviceHistoryRepository extends JpaRepository<DeviceHistory, Long > {
    List<DeviceHistory> findAllByDevice_DeviceUuidOrderByTimestampDesc(String deviceUuid);
}


