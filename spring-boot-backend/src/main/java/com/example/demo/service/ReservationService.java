package com.example.demo.service;

import com.example.demo.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    @Autowired
    private com.example.demo.repository.ReservationMapper reservationMapper;

    public Reservation handleReservationNotification(Long bookId) {
        return null;
    }
}