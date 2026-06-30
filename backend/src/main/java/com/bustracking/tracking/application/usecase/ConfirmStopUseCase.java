package com.bustracking.tracking.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.ConfirmStop;
import com.bustracking.tracking.domain.contract.GetTripDetail;
import com.bustracking.tracking.domain.model.TripDetailView;

@Service
public class ConfirmStopUseCase {

    // Contract Interfaces
    private final BusExistsById busExistsById;
    private final ConfirmStop confirmStop;
    private final GetTripDetail getTripDetail;

    public ConfirmStopUseCase(
            BusExistsById busExistsById,
            ConfirmStop confirmStop,
            GetTripDetail getTripDetail) {
        this.busExistsById = busExistsById;
        this.confirmStop = confirmStop;
        this.getTripDetail = getTripDetail;
    }

    @Transactional
    public TripDetailView execute(UUID tripId, UUID stopId, UUID busId) {

        if (!busExistsById.check(busId)) {
            throw new NotFoundException(
                    ErrorCode.BUS_NOT_FOUND,
                    "Bus not found",
                    "Bus with ID " + busId + " does not exist"
            );
        }
        //para mañana: Finalizar viaje apenas completa la ultima o dejarlo manual
        confirmStop.execute(tripId, stopId);
        
        //We return the updated trip to the frontend (driver)
        return getTripDetail.execute(tripId);
    }
}