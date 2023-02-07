package com.kenzie.unit.four.ticketsystem.service;

import com.kenzie.unit.four.ticketsystem.repositories.model.ReserveTicketRecord;
import com.kenzie.unit.four.ticketsystem.service.model.ReservedTicket;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CloseReservationTask implements Runnable {

    private final Integer durationToPay;
    private final ConcurrentLinkedQueue<ReservedTicket> reservedTicketsQueue;
    private final ReservedTicketService reservedTicketService;

    public CloseReservationTask(Integer durationToPay,
                                ReservedTicketService reservedTicketService,
                                ConcurrentLinkedQueue<ReservedTicket> reservedTicketsQueue) {
        this.durationToPay = durationToPay;
        this.reservedTicketService = reservedTicketService;
        this.reservedTicketsQueue = reservedTicketsQueue;
    }

    @Override
    public void run() {
       // Your code here
        ReserveTicketRecord record = new ReserveTicketRecord();
        LocalDateTime reservationTime;
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration;

        while (!reservedTicketsQueue.isEmpty()) {
            ReservedTicket reservedTicket = reservedTicketsQueue.poll();

            record.setTicketId(reservedTicket.getTicketId());
            record.setDateOfReservation(reservedTicket.getDateOfReservation());

            reservationTime = LocalDateTime.parse(reservedTicket.getDateOfReservation());
            duration = Duration.between(reservationTime, currentTime);

            if(!reservedTicket.getTicketPurchased() && duration.getSeconds() > durationToPay) {
                record.setReservationClosed(true);
                record.setPurchasedTicket(false);
                record.setDateReservationClosed(currentTime.toString());

                ReservedTicket ticket = new ReservedTicket(record.getConcertId(),
                        record.getTicketId(),record.getDateOfReservation(),record.getReservationClosed(),
                        record.getDateReservationClosed(),record.getPurchasedTicket());

                reservedTicketService.updateReserveTicket(ticket);

            } else if(reservedTicket.getTicketPurchased() || duration.getSeconds() < durationToPay){
                reservedTicketsQueue.add(reservedTicket);
            }
        }
    }
}
