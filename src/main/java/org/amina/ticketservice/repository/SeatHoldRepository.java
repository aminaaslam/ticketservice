package org.amina.ticketservice.repository;

import java.util.Date;
import java.util.List;

import org.amina.ticketservice.entity.Customer;
import org.amina.ticketservice.entity.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatHoldRepository extends JpaRepository<SeatHold, Integer> {
	List<SeatHold> findByConfirmationCodeAndHoldTimeBefore(Date date);
	List<SeatHold> findByCustomer(Customer customer);
}
