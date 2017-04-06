package com.rosa.swift.core.data.repositories;

import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.data.dto.common.Tplst;
import com.rosa.swift.core.data.dto.common.TplstCollection;
import com.rosa.swift.core.data.dto.deliveries.DeliveryLocation;
import com.rosa.swift.core.data.dto.deliveries.TransportationType;

import java.util.List;

public interface DatabaseRepository {

    //region Delivery

    Delivery selectTransportation(String number, TransportationType type);

    List<Delivery> selectTransportations(TransportationType type);

    void insertDeliveries(List<Delivery> deliveries);

    void insertOrUpdateDelivery(Delivery delivery);

    void deleteTransportation(String number, TransportationType type);

    void deleteTransportations(TransportationType type);

    void deleteTransportations();

    //endregion

    //region DeliveryLocation

    List<DeliveryLocation> selectDeliveryLocations();

    void insertDeliveryLocation(DeliveryLocation location);

    void deleteDeliveryLocations();

    //endregion

    //region Plant

    TplstCollection selectPlants(String parentCode);

    Tplst selectPlant(String code, String parentCode);

    void insertPlants(TplstCollection plants);

    void deletePlants();

    void updatePlants(TplstCollection plants);

    //endregion

}
