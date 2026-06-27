package com.bustracking.tracking.infrastructure.mappers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bustracking.tracking.domain.model.RouteStopView;
import com.bustracking.tracking.domain.model.StopView;
import com.bustracking.tracking.domain.model.TripDetailView;
import com.bustracking.tracking.domain.model.TripStopDetailView;
import com.bustracking.tracking.domain.model.TripView;
import com.bustracking.tracking.infrastructure.web.dto.response.RouteStopResponse;
import com.bustracking.tracking.infrastructure.web.dto.response.StopResponse;
import com.bustracking.tracking.infrastructure.web.dto.response.TripDetailResponse;
import com.bustracking.tracking.infrastructure.web.dto.response.TripResponse;
import com.bustracking.tracking.infrastructure.web.dto.response.TripStopDetailResponse;

@Component
public class TripDetailMapper {

    public TripDetailResponse toResponse(TripDetailView detailView) {
        
        TripResponse tripResponse = toTripResponse(detailView.getTrip());
        List<TripStopDetailResponse> stopResponses = toStopResponseList(detailView.getStops());
        return new TripDetailResponse(tripResponse, stopResponses);
    }

    private TripResponse toTripResponse(TripView tripView) {
        return new TripResponse(
                tripView.getId(),
                tripView.getRouteName(),
                tripView.getOrigin(),
                tripView.getDestination(),
                tripView.getDepartureTime(),
                tripView.getStatus()
        );
    }

    private List<TripStopDetailResponse> toStopResponseList(List<TripStopDetailView> stopViews) {
        List<TripStopDetailResponse> result = new ArrayList<>();
        for (TripStopDetailView stopView : stopViews) {
            result.add(toSingleStopResponse(stopView));
        }
        return result;
    }

    private TripStopDetailResponse toSingleStopResponse(TripStopDetailView stopView) {
        RouteStopView rs = stopView.getRouteStop();
        StopView sv = stopView.getStop();

        RouteStopResponse routeStopResponse = new RouteStopResponse(
                rs.getId(),
                rs.getStopId(),
                rs.getOrderIndex(),
                rs.getEstimatedTimeOffset()
        );

        StopResponse stopResponse = new StopResponse(
                sv.getId(),
                sv.getName(),
                sv.getLatitude(),
                sv.getLongitude(),
                sv.getReference()
        );

        return new TripStopDetailResponse(
                routeStopResponse,
                stopResponse,
                stopView.getCompletedAt()
        );
    }
}