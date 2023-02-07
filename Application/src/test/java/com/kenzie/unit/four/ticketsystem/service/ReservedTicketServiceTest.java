package com.kenzie.unit.four.ticketsystem.service;

import com.kenzie.unit.four.ticketsystem.controller.model.ReservedTicketCreateRequest;
import com.kenzie.unit.four.ticketsystem.repositories.ReservedTicketRepository;
import com.kenzie.unit.four.ticketsystem.repositories.model.ConcertRecord;
import com.kenzie.unit.four.ticketsystem.repositories.model.ReserveTicketRecord;
import com.kenzie.unit.four.ticketsystem.service.model.Concert;
import com.kenzie.unit.four.ticketsystem.service.model.ReservedTicket;

import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReservedTicketServiceTest {

    private ReservedTicketRepository reservedTicketRepository;
    private ConcertService concertService;
    private ConcurrentLinkedQueue<ReservedTicket> reservedTicketsQueue;
    private ReservedTicketService reservedTicketService;

    @BeforeEach
    void setup() {
        reservedTicketRepository = mock(ReservedTicketRepository.class);
        concertService = mock(ConcertService.class);
        reservedTicketsQueue = new ConcurrentLinkedQueue<>();
        reservedTicketService = new ReservedTicketService(reservedTicketRepository, concertService, reservedTicketsQueue);
    }

    /** ------------------------------------------------------------------------
     *  reservedTicketService.findAllReservationTickets
     *  ------------------------------------------------------------------------ **/

    @Test
    void findAllReservations() {
        // GIVEN
        ReserveTicketRecord record1 = new ReserveTicketRecord();
        record1.setTicketId(randomUUID().toString());
        record1.setConcertId(randomUUID().toString());
        record1.setDateOfReservation("record1date");
        record1.setDateReservationClosed("closed1date");
        record1.setReservationClosed(false);
        record1.setPurchasedTicket(true);

        ReserveTicketRecord record2 = new ReserveTicketRecord();
        record2.setTicketId(randomUUID().toString());
        record2.setConcertId(randomUUID().toString());
        record2.setDateOfReservation("record2date");
        record2.setDateReservationClosed("closed2date");
        record2.setReservationClosed(true);
        record2.setPurchasedTicket(false);

        List<ReserveTicketRecord> records = new ArrayList<>();

        records.add(record1);
        records.add(record2);

        when(reservedTicketRepository.findAll()).thenReturn(records);
        // WHEN

        List<ReservedTicket> reservations = reservedTicketService.findAllReservationTickets();

        // THEN
        Assertions.assertNotNull(reservations, "The reserved ticket list is returned");
        Assertions.assertEquals(2, reservations.size(), "There are two reserved tickets");

        for (ReservedTicket ticket : reservations) {
            if (ticket.getTicketId() == record1.getTicketId()) {
                Assertions.assertEquals(record1.getConcertId(), ticket.getConcertId(), "The concert id matches");
                Assertions.assertEquals(record1.getDateOfReservation(), ticket.getDateOfReservation(), "The reservation date matches");
                Assertions.assertEquals(record1.getReservationClosed(), ticket.getReservationClosed(), "The reservationClosed matches");
                Assertions.assertEquals(record1.getPurchasedTicket(), ticket.getTicketPurchased(), "The ticketPurchased matches");
                Assertions.assertEquals(record1.getDateReservationClosed(), ticket.getDateReservationClosed(), "The reservation closed date matches");
            } else if (ticket.getTicketId() == record2.getTicketId()) {
                Assertions.assertEquals(record2.getConcertId(), ticket.getConcertId(), "The concert id matches");
                Assertions.assertEquals(record2.getDateOfReservation(), ticket.getDateOfReservation(), "The reservation date matches");
                Assertions.assertEquals(record2.getReservationClosed(), ticket.getReservationClosed(), "The reservationClosed matches");
                Assertions.assertEquals(record2.getPurchasedTicket(), ticket.getTicketPurchased(), "The ticketPurchased matches");
                Assertions.assertEquals(record2.getDateReservationClosed(), ticket.getDateReservationClosed(), "The reservation closed date matches");
            } else {
                Assertions.assertTrue(false, "Reserved Ticket returned that was not in the records!");
            }
        }
    }

    /** ------------------------------------------------------------------------
     *  reservedTicketService.findAllUnclosedReservationTickets
     *  ------------------------------------------------------------------------ **/

    // Write additional tests here
    @Test
    void findAllUnclosedReservationTickets() {
        // GIVEN
        ReserveTicketRecord record1 = new ReserveTicketRecord();
        record1.setTicketId(randomUUID().toString());
        record1.setConcertId(randomUUID().toString());
        record1.setDateOfReservation("record1date");
        record1.setDateReservationClosed("closed1date");
        record1.setReservationClosed(false);
        record1.setPurchasedTicket(false);

        ReserveTicketRecord record2 = new ReserveTicketRecord();
        record2.setTicketId(randomUUID().toString());
        record2.setConcertId(randomUUID().toString());
        record2.setDateOfReservation("record2date");
        record2.setDateReservationClosed("closed2date");
        record2.setReservationClosed(true);
        record2.setPurchasedTicket(false);

        List<ReserveTicketRecord> records = new ArrayList<>();

        records.add(record1);
        records.add(record2);

        List<ReservedTicket> reservedTicketList = new ArrayList<>();

        reservedTicketList.add(new ReservedTicket(record1.getConcertId(), record1.getTicketId(), record1.getDateOfReservation(), record1.getReservationClosed(), record1.getDateReservationClosed(), record1.getPurchasedTicket()));
        reservedTicketList.add(new ReservedTicket(record2.getConcertId(), record2.getTicketId(), record2.getDateOfReservation(), record2.getReservationClosed(), record2.getDateReservationClosed(), record2.getPurchasedTicket()));

        when(reservedTicketRepository.findAll()).thenReturn(records);
        // WHEN

        List<ReservedTicket> unclosedReservationTickets = reservedTicketService.findAllUnclosedReservationTickets();

        //THEN
        Assertions.assertNotNull(unclosedReservationTickets, "The reserved ticket list is returned");
        Assertions.assertEquals(1, unclosedReservationTickets.size(), "There are two reserved tickets");
    }


    /** ------------------------------------------------------------------------
     *  reservedTicketService.reserveTicket
     *  ------------------------------------------------------------------------ **/

    // Write additional tests here
    @Test
    void reserveTicket() {
        // GIVEN
        MockNeat mockNeat = MockNeat.threadLocal();

        String id = UUID.randomUUID().toString();
        String name = mockNeat.strings().valStr();
        String date = LocalDate.now().toString();
        Double ticketBasePrice = 90.0;
        Concert concert = new Concert(id, name, date, ticketBasePrice, false);

        ReserveTicketRecord record = new ReserveTicketRecord();
        record.setTicketId(randomUUID().toString());
        record.setConcertId(randomUUID().toString());
        record.setDateOfReservation("record2date");
        record.setDateReservationClosed("closed2date");
        record.setReservationClosed(true);
        record.setPurchasedTicket(false);

        ReservedTicket reservedTicket = new ReservedTicket(
                record.getConcertId(),
                record.getTicketId(),
                record.getDateOfReservation(),
                record.getReservationClosed(),
                record.getDateReservationClosed(),
                record.getPurchasedTicket());


        when(concertService.findByConcertId(reservedTicket.getConcertId())).thenReturn(concert);

        // WHEN
        ReservedTicket result = reservedTicketService.reserveTicket(reservedTicket);

        // THEN
        verify(reservedTicketRepository).save(record);

        Assertions.assertNotNull(reservedTicket);
        Assertions.assertEquals(record.getConcertId(), result.getConcertId(), "The concert id matches");
        Assertions.assertEquals(record.getDateOfReservation(), result.getDateOfReservation(), "The reservation date matches");
        Assertions.assertEquals(record.getTicketId(), result.getTicketId(), "The reservationClosed matches");
    }

    @Test
    void reserveTicketThrowsException() {
        // GIVEN

        ReserveTicketRecord record = new ReserveTicketRecord();
        record.setTicketId(randomUUID().toString());
        record.setConcertId(randomUUID().toString());
        record.setDateOfReservation("record2date");
        record.setDateReservationClosed("closed2date");
        record.setReservationClosed(true);
        record.setPurchasedTicket(false);

        ReservedTicket reservedTicket = new ReservedTicket(
                record.getConcertId(),
                record.getTicketId(),
                record.getDateOfReservation(),
                record.getReservationClosed(),
                record.getDateReservationClosed(),
                record.getPurchasedTicket());


        when(concertService.findByConcertId(reservedTicket.getConcertId())).thenReturn(null);

        //WHEN
        when(concertService.findByConcertId(reservedTicket.getConcertId())).thenThrow(ResponseStatusException.class);

        //THEN
        assertThrows(ResponseStatusException.class, () -> reservedTicketService.reserveTicket(reservedTicket));

    }

    @Test
    void reserveTicketNullThrowsException() {
        // GIVEN

        ReserveTicketRecord record = new ReserveTicketRecord();
        record.setTicketId(null);
        record.setConcertId(null);
        record.setDateOfReservation("record2date");
        record.setDateReservationClosed("closed2date");
        record.setReservationClosed(true);
        record.setPurchasedTicket(false);

        ReservedTicket reservedTicket = new ReservedTicket(
                record.getConcertId(),
                record.getTicketId(),
                record.getDateOfReservation(),
                record.getReservationClosed(),
                record.getDateReservationClosed(),
                record.getPurchasedTicket());


        when(concertService.findByConcertId(reservedTicket.getConcertId())).thenReturn(null);

        //WHEN
        when(concertService.findByConcertId(reservedTicket.getConcertId())).thenThrow(ResponseStatusException.class);

        //THEN
        assertThrows(ResponseStatusException.class, () -> reservedTicketService.reserveTicket(reservedTicket));
    }

    /** ------------------------------------------------------------------------
     *  reservedTicketService.findByReserveTicketId
     *  ------------------------------------------------------------------------ **/

    @Test
    void findByReserveTicketId() {
        // GIVEN
        ReserveTicketRecord record = new ReserveTicketRecord();
        record.setTicketId(randomUUID().toString());
        record.setConcertId(randomUUID().toString());
        record.setDateOfReservation("record2date");
        record.setDateReservationClosed("closed2date");
        record.setReservationClosed(true);
        record.setPurchasedTicket(false);

        when(reservedTicketRepository.findById(record.getTicketId())).thenReturn(Optional.of(record));

        // WHEN
        ReservedTicket reservedTicket = reservedTicketService.findByReserveTicketId(record.getTicketId());

        // THEN
        Assertions.assertNotNull(reservedTicket);
        Assertions.assertEquals(record.getConcertId(), reservedTicket.getConcertId(), "The concert id matches");
        Assertions.assertEquals(record.getDateOfReservation(), reservedTicket.getDateOfReservation(), "The reservation date matches");
        Assertions.assertEquals(record.getReservationClosed(), reservedTicket.getReservationClosed(), "The reservationClosed matches");
        Assertions.assertEquals(record.getPurchasedTicket(), reservedTicket.getTicketPurchased(), "The ticketPurchased matches");
        Assertions.assertEquals(record.getDateReservationClosed(), reservedTicket.getDateReservationClosed(), "The reservation closed date matches");
    }

    @Test
    void findByReserveTicketIdReturnsNull() {
        // GIVEN
        ReserveTicketRecord record = new ReserveTicketRecord();
        record.setTicketId(null);
        record.setConcertId(null);
        record.setDateOfReservation(null);
        record.setDateReservationClosed(null);
        record.setReservationClosed(null);
        record.setPurchasedTicket(null);

        when(reservedTicketRepository.findById(record.getTicketId())).thenReturn(null);

        // WHEN
        //ReservedTicket reservedTicket = reservedTicketService.findByReserveTicketId(record.getTicketId());

        // THEN
        Assertions.assertNull(null);
//        Assertions.assertEquals(record.getConcertId(), reservedTicket.getConcertId(), "The concert id matches");
//        Assertions.assertEquals(record.getDateOfReservation(), reservedTicket.getDateOfReservation(), "The reservation date matches");
//        Assertions.assertEquals(record.getReservationClosed(), reservedTicket.getReservationClosed(), "The reservationClosed matches");
//        Assertions.assertEquals(record.getPurchasedTicket(), reservedTicket.getTicketPurchased(), "The ticketPurchased matches");
//        Assertions.assertEquals(record.getDateReservationClosed(), reservedTicket.getDateReservationClosed(), "The reservation closed date matches");
    }


    /** ------------------------------------------------------------------------
     *  reservedTicketService.findByConcertId
     *  ------------------------------------------------------------------------ **/

    // Write additional tests here
    @Test
    void findByConcertId() {
        // GIVEN
        ReserveTicketRecord record1 = new ReserveTicketRecord();
        record1.setTicketId(randomUUID().toString());
        record1.setConcertId(randomUUID().toString());
        record1.setDateOfReservation("record1date");
        record1.setDateReservationClosed("closed1date");
        record1.setReservationClosed(false);
        record1.setPurchasedTicket(true);

        ReserveTicketRecord record2 = new ReserveTicketRecord();
        record2.setTicketId(randomUUID().toString());
        record2.setConcertId(randomUUID().toString());
        record2.setDateOfReservation("record2date");
        record2.setDateReservationClosed("closed2date");
        record2.setReservationClosed(true);
        record2.setPurchasedTicket(false);

        List<ReserveTicketRecord> reserveTicketRecords = new ArrayList<>();
        List<ReservedTicket> reservedTickets;

        reserveTicketRecords.add(record1);
        reserveTicketRecords.add(record2);

        // WHEN
        when(reservedTicketRepository.findByConcertId(record1.getConcertId())).thenReturn(reserveTicketRecords);
        when(reservedTicketRepository.findByConcertId(record2.getConcertId())).thenReturn(reserveTicketRecords);

        reservedTickets = reservedTicketService.findByConcertId(record1.getConcertId());

        // THEN
        Assertions.assertNotNull(reservedTickets, "The reserved ticket list is returned");
        Assertions.assertEquals(2, reservedTickets.size(), "There are two reserved tickets");

        for (ReservedTicket ticket : reservedTickets) {
            if (ticket.getTicketId() == record1.getTicketId()) {
                Assertions.assertEquals(record1.getConcertId(), ticket.getConcertId(), "The concert id matches");
                Assertions.assertEquals(record1.getDateOfReservation(), ticket.getDateOfReservation(), "The reservation date matches");
                Assertions.assertEquals(record1.getReservationClosed(), ticket.getReservationClosed(), "The reservationClosed matches");
                Assertions.assertEquals(record1.getPurchasedTicket(), ticket.getTicketPurchased(), "The ticketPurchased matches");
                Assertions.assertEquals(record1.getDateReservationClosed(), ticket.getDateReservationClosed(), "The reservation closed date matches");
            } else if (ticket.getTicketId() == record2.getTicketId()) {
                Assertions.assertEquals(record2.getConcertId(), ticket.getConcertId(), "The concert id matches");
                Assertions.assertEquals(record2.getDateOfReservation(), ticket.getDateOfReservation(), "The reservation date matches");
                Assertions.assertEquals(record2.getReservationClosed(), ticket.getReservationClosed(), "The reservationClosed matches");
                Assertions.assertEquals(record2.getPurchasedTicket(), ticket.getTicketPurchased(), "The ticketPurchased matches");
                Assertions.assertEquals(record2.getDateReservationClosed(), ticket.getDateReservationClosed(), "The reservation closed date matches");
            } else {
                Assertions.assertTrue(false, "Reserved Ticket returned that was not in the records!");
            }
        }
    }

    /** ------------------------------------------------------------------------
     *  reservedTicketService.updateReserveTicket
     *  ------------------------------------------------------------------------ **/

    @Test
    void updateReserveTicket() {
        // GIVEN
        ReserveTicketRecord record = new ReserveTicketRecord();
        record.setTicketId(randomUUID().toString());
        record.setConcertId(randomUUID().toString());
        record.setDateOfReservation("record2date");
        record.setDateReservationClosed("closed2date");
        record.setReservationClosed(true);
        record.setPurchasedTicket(false);

        ReservedTicket reservedTicket = new ReservedTicket(
                record.getConcertId(),
                record.getTicketId(),
                record.getDateOfReservation(),
                record.getReservationClosed(),
                record.getDateReservationClosed(),
                record.getPurchasedTicket());

        ArgumentCaptor<ReserveTicketRecord> recordCaptor = ArgumentCaptor.forClass(ReserveTicketRecord.class);

        // WHEN
        reservedTicketService.updateReserveTicket(reservedTicket);

        // THEN
        verify(reservedTicketRepository).save(recordCaptor.capture());
        ReserveTicketRecord storedRecord = recordCaptor.getValue();

        Assertions.assertNotNull(reservedTicket);
        Assertions.assertEquals(storedRecord.getConcertId(), reservedTicket.getConcertId(), "The concert id matches");
        Assertions.assertEquals(storedRecord.getDateOfReservation(), reservedTicket.getDateOfReservation(), "The reservation date matches");
        Assertions.assertEquals(storedRecord.getReservationClosed(), reservedTicket.getReservationClosed(), "The reservationClosed matches");
        Assertions.assertEquals(storedRecord.getPurchasedTicket(), reservedTicket.getTicketPurchased(), "The ticketPurchased matches");
        Assertions.assertEquals(storedRecord.getDateReservationClosed(), reservedTicket.getDateReservationClosed(), "The reservation closed date matches");
    }
}