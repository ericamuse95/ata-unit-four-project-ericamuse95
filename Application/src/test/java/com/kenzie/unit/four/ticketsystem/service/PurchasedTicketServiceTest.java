package com.kenzie.unit.four.ticketsystem.service;

import com.kenzie.unit.four.ticketsystem.repositories.PurchaseTicketRepository;
import com.kenzie.unit.four.ticketsystem.repositories.model.PurchasedTicketRecord;
import com.kenzie.unit.four.ticketsystem.repositories.model.ReserveTicketRecord;
import com.kenzie.unit.four.ticketsystem.service.model.PurchasedTicket;
import com.kenzie.unit.four.ticketsystem.service.model.ReservedTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PurchasedTicketServiceTest {
    private PurchaseTicketRepository purchaseTicketRepository;
    private ReservedTicketService reservedTicketService;
    private PurchasedTicketService purchasedTicketService;

    @BeforeEach
    void setup() {
        purchaseTicketRepository = mock(PurchaseTicketRepository.class);
        reservedTicketService = mock(ReservedTicketService.class);
        purchasedTicketService = new PurchasedTicketService(purchaseTicketRepository, reservedTicketService);
    }

    /** ------------------------------------------------------------------------
     *  purchasedTicketService.purchaseTicket
     *  ------------------------------------------------------------------------ **/

    // Write additional tests here
    @Test
    void purchaseTicket() {
        // GIVEN
        String id = UUID.randomUUID().toString();
        String name = UUID.randomUUID().toString();
        String date = LocalDate.now().toString();
        Double ticketBasePrice = 90.0;

        ReserveTicketRecord record = new ReserveTicketRecord();
        record.setTicketId(id);
        record.setConcertId(name);
        record.setDateOfReservation(date);
        record.setDateReservationClosed("closed2date");

        ReservedTicket reservedTicket = new ReservedTicket(
                record.getConcertId(),
                record.getTicketId(),
                record.getDateOfReservation(),
                record.getReservationClosed(),
                record.getDateReservationClosed(),
                record.getPurchasedTicket());

        PurchasedTicketRecord purchasedTicketRecord = new PurchasedTicketRecord();
        purchasedTicketRecord.setTicketId(reservedTicket.getTicketId());
        purchasedTicketRecord.setDateOfPurchase(reservedTicket.getDateOfReservation());
        purchasedTicketRecord.setConcertId(reservedTicket.getConcertId());
        purchasedTicketRecord.setPricePaid(ticketBasePrice);

        PurchasedTicket purchasedTicket = new PurchasedTicket(purchasedTicketRecord.getConcertId(), purchasedTicketRecord.getTicketId(), purchasedTicketRecord.getDateOfPurchase(), purchasedTicketRecord.getPricePaid());

        when(reservedTicketService.findByReserveTicketId(reservedTicket.getTicketId())).thenReturn(reservedTicket);

        // WHEN
        PurchasedTicket result = purchasedTicketService.purchaseTicket(reservedTicket.getTicketId(), ticketBasePrice);

        // THEN
        verify(purchaseTicketRepository).save(purchasedTicketRecord);

        Assertions.assertNotNull(purchasedTicket);
        Assertions.assertEquals(purchasedTicket.getConcertId(), result.getConcertId(), "The concert id matches");
        Assertions.assertEquals(purchasedTicket.getTicketId(), result.getTicketId(), "The reservationClosed matches");
        Assertions.assertEquals(purchasedTicket.getPricePaid(), result.getPricePaid());
        Assertions.assertEquals(purchasedTicket.getDateOfPurchase(), result.getDateOfPurchase());
    }

    /** ------------------------------------------------------------------------
     *  purchasedTicketService.findByConcertId
     *  ------------------------------------------------------------------------ **/
    @Test
    void purchaseTicket_reservationClosed_throws_Exception() {
        // GIVEN
        String id = UUID.randomUUID().toString();
        String name = UUID.randomUUID().toString();
        String date = LocalDate.now().toString();
        Double ticketBasePrice = 90.0;

        ReserveTicketRecord record = new ReserveTicketRecord();
        record.setTicketId(id);
        record.setConcertId(name);
        record.setDateOfReservation(date);
        record.setDateReservationClosed("closed2date");
        record.setReservationClosed(true);

        ReservedTicket reservedTicket = new ReservedTicket(
                record.getConcertId(),
                record.getTicketId(),
                record.getDateOfReservation(),
                record.getReservationClosed(),
                record.getDateReservationClosed(),
                record.getPurchasedTicket());

        PurchasedTicketRecord purchasedTicketRecord = new PurchasedTicketRecord();
        purchasedTicketRecord.setTicketId(reservedTicket.getTicketId());
        purchasedTicketRecord.setDateOfPurchase(reservedTicket.getDateOfReservation());
        purchasedTicketRecord.setConcertId(reservedTicket.getConcertId());
        purchasedTicketRecord.setPricePaid(ticketBasePrice);

        PurchasedTicket purchasedTicket = new PurchasedTicket(purchasedTicketRecord.getConcertId(), purchasedTicketRecord.getTicketId(), purchasedTicketRecord.getDateOfPurchase(), purchasedTicketRecord.getPricePaid());

        when(reservedTicketService.findByReserveTicketId(reservedTicket.getTicketId())).thenReturn(reservedTicket);

        //THEN
        assertThrows(ResponseStatusException.class, () -> purchasedTicketService.purchaseTicket(reservedTicket.getTicketId(), ticketBasePrice));
    }
    @Test
    void findByConcertId() {
        // GIVEN
        String concertId = randomUUID().toString();

        PurchasedTicketRecord record = new PurchasedTicketRecord();
        record.setConcertId(concertId);
        record.setTicketId(randomUUID().toString());
        record.setDateOfPurchase("purchasedate");
        record.setPricePaid(11.0);

        // WHEN
        when(purchaseTicketRepository.findByConcertId(concertId)).thenReturn(Arrays.asList(record));
        List<PurchasedTicket> purchasedTickets = purchasedTicketService.findByConcertId(concertId);

        // THEN
        Assertions.assertEquals(1, purchasedTickets.size(), "There is one Purchased Ticket");
        PurchasedTicket ticket = purchasedTickets.get(0);
        Assertions.assertNotNull(ticket, "The purchased ticket is returned");
        Assertions.assertEquals(record.getConcertId(), ticket.getConcertId(), "The concert id matches");
        Assertions.assertEquals(record.getTicketId(), ticket.getTicketId(), "The ticket id matches");
        Assertions.assertEquals(record.getDateOfPurchase(), ticket.getDateOfPurchase(), "The date of purchase matches");
        Assertions.assertEquals(record.getPricePaid(), ticket.getPricePaid(), "The price paid matches");
    }

    @Test
    void findByConcertId_no_purchased_tickets() {
        // GIVEN
        String concertId = randomUUID().toString();

        // WHEN
        when(purchaseTicketRepository.findByConcertId(concertId)).thenReturn(new ArrayList<PurchasedTicketRecord>());
        List<PurchasedTicket> purchasedTickets = purchasedTicketService.findByConcertId(concertId);

        // THEN
        Assertions.assertEquals(0, purchasedTickets.size(), "There are no Purchased Tickets");
    }
}
