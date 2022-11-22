package com.kenzie.unit.four.ticketsystem.controller;

import com.kenzie.unit.four.ticketsystem.controller.model.PurchasedTicketResponse;
import com.kenzie.unit.four.ticketsystem.controller.model.ReservedTicketCreateRequest;
import com.kenzie.unit.four.ticketsystem.controller.model.ReservedTicketResponse;
import com.kenzie.unit.four.ticketsystem.service.ReservedTicketService;
import com.kenzie.unit.four.ticketsystem.service.model.PurchasedTicket;
import com.kenzie.unit.four.ticketsystem.service.model.ReservedTicket;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/reservedtickets")
public class ReservedTicketController {

    private ReservedTicketService reservedTicketService;

    ReservedTicketController(ReservedTicketService reservedTicketService) {
        this.reservedTicketService = reservedTicketService;
    }

    // TODO - Task 2: reserveTicket() - POST
    // Add the correct annotation
    @PostMapping
    public ResponseEntity<ReservedTicketResponse> reserveTicket(
            @RequestBody ReservedTicketCreateRequest reservedTicketCreateRequest) {

        // Add your code here
        ReservedTicket reservedTicket = new ReservedTicket(reservedTicketCreateRequest.getConcertId(), randomUUID().toString(), LocalDateTime.now().toString());
        reservedTicketService.reserveTicket(reservedTicket);

        ReservedTicketResponse response = new ReservedTicketResponse();
        response.setConcertId(reservedTicket.getConcertId());
        response.setTicketId(reservedTicket.getTicketId());
        response.setDateOfReservation(reservedTicket.getDateOfReservation());
        response.setReservationClosed(reservedTicket.getReservationClosed());
        response.setPurchasedTicket(reservedTicket.getTicketPurchased());

        // Return your ReservedTicketResponse instead of null
        return ResponseEntity.ok(response);
    }

    // TODO - Task 3: getAllReserveTicketsByConcertId() - GET `/concerts/{concertId}`
    // Add the correct annotation
    @GetMapping("/concerts/{concertId}")
    public ResponseEntity<List<ReservedTicketResponse>> getAllReserveTicketsByConcertId(
            @PathVariable("concertId") String concertId) {

        // Add your code here
        List<ReservedTicket> reservedTickets = reservedTicketService.findByConcertId(concertId);

        if (reservedTickets == null || reservedTickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ReservedTicketResponse> reservedTicketResponses = new ArrayList<>();
        for (ReservedTicket reservedTicket : reservedTickets) {
            reservedTicketResponses.add(this.createReservedTicketResponse(reservedTicket));
        }
        // Return your List<ReservedTicketResponse> instead of null
        return ResponseEntity.ok(reservedTicketResponses);
    }

    private ReservedTicketResponse createReservedTicketResponse(ReservedTicket reservedTicket) {
        ReservedTicketResponse response = new ReservedTicketResponse();

        response.setDateReservationClosed(reservedTicket.getDateReservationClosed());
        response.setPurchasedTicket(reservedTicket.getTicketPurchased());
        response.setTicketId(reservedTicket.getTicketId());
        response.setConcertId(reservedTicket.getConcertId());
        response.setReservationClosed(reservedTicket.getReservationClosed());
        response.setDateOfReservation(reservedTicket.getDateOfReservation());

        return response;
    }

}