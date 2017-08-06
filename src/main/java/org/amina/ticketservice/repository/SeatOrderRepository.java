package org.amina.ticketservice.repository;

import java.util.List;

import org.amina.ticketservice.entity.SeatHold;
import org.amina.ticketservice.entity.SeatOrder;
import org.amina.ticketservice.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatOrderRepository extends JpaRepository<SeatOrder, Long> {
	
	List<SeatOrder> findBySeatHold(SeatHold seathold);
	List<SeatOrder> findByVenue(Venue venue);

}
