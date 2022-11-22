package com.kenzie.unit.four.ticketsystem.service;

import com.kenzie.unit.four.ticketsystem.repositories.PurchaseTicketRepository;
import com.kenzie.unit.four.ticketsystem.repositories.model.PurchasedTicketRecord;
import com.kenzie.unit.four.ticketsystem.repositories.model.ReserveTicketRecord;
import com.kenzie.unit.four.ticketsystem.service.model.PurchasedTicket;

import com.kenzie.unit.four.ticketsystem.service.model.ReservedTicket;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PurchasedTicketService {
    private PurchaseTicketRepository purchaseTicketRepository;
    private ReservedTicketService reservedTicketService;

    public PurchasedTicketService(PurchaseTicketRepository purchaseTicketRepository,
                                  ReservedTicketService reservedTicketService) {
        this.purchaseTicketRepository = purchaseTicketRepository;
        this.reservedTicketService = reservedTicketService;
    }

    public PurchasedTicket purchaseTicket(String reservedTicketId, Double pricePaid) {
        // Your code here
        ReservedTicket reservedTicket = reservedTicketService.findByReserveTicketId(reservedTicketId);

        if (reservedTicketId == null || (reservedTicket.getReservationClosed() != null && reservedTicket.getReservationClosed())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reserved ticket does not exist");
        }

        PurchasedTicketRecord record = new PurchasedTicketRecord();
        record.setTicketId(reservedTicket.getTicketId());
        record.setDateOfPurchase(reservedTicket.getDateOfReservation());
        record.setConcertId(reservedTicket.getConcertId());
        record.setPricePaid(pricePaid);

        purchaseTicketRepository.save(record);

        ReserveTicketRecord reserveTicketRecord = new ReserveTicketRecord();
        reserveTicketRecord.setTicketId(reservedTicket.getTicketId());
        reserveTicketRecord.setConcertId(reservedTicket.getConcertId());
        reserveTicketRecord.setDateOfReservation(reservedTicket.getDateOfReservation());
        reserveTicketRecord.setReservationClosed(true);
        reserveTicketRecord.setPurchasedTicket(true);

        ReservedTicket ticket = new ReservedTicket(reserveTicketRecord.getConcertId(), reserveTicketRecord.getTicketId(),
                reserveTicketRecord.getDateOfReservation(), reserveTicketRecord.getReservationClosed(),
                reserveTicketRecord.getDateReservationClosed(), reserveTicketRecord.getPurchasedTicket());

        reservedTicketService.updateReserveTicket(ticket);

        PurchasedTicket purchasedTicket = new PurchasedTicket(record.getConcertId(), record.getTicketId(), record.getDateOfPurchase(), record.getPricePaid());

        return purchasedTicket;
    }

    public List<PurchasedTicket> findByConcertId(String concertId) {
        List<PurchasedTicketRecord> purchasedTicketRecords = purchaseTicketRepository
                .findByConcertId(concertId);

        List<PurchasedTicket> purchasedTickets = new ArrayList<>();

        for (PurchasedTicketRecord purchasedTicketRecord : purchasedTicketRecords) {
            purchasedTickets.add(new PurchasedTicket(purchasedTicketRecord.getConcertId(),
                    purchasedTicketRecord.getTicketId(),
                    purchasedTicketRecord.getDateOfPurchase(),
                    purchasedTicketRecord.getPricePaid()));
        }

        return purchasedTickets;
    }
}
