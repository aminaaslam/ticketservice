package org.amina.ticketservice.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amina.ticketservice.entity.Customer;
import org.amina.ticketservice.entity.SeatHold;
import org.amina.ticketservice.entity.SeatOrder;
import org.amina.ticketservice.entity.Venue;
import org.amina.ticketservice.repository.CustomerRepository;
import org.amina.ticketservice.repository.SeatHoldRepository;
import org.amina.ticketservice.repository.SeatOrderRepository;
import org.amina.ticketservice.repository.VenueRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceTestConfig {
	
	

	private static final long SEAT_HOLD_EXPIRATION_TIME_IN_SECONDS = 0;

	@Bean
	public VenueRepository venueRepository() {
		VenueRepository repository = mock(VenueRepository.class);
		when(repository.findOne(1)).thenReturn(new Venue(1, "Orchestra", BigDecimal.valueOf(200.00d), 25, 50));
		when(repository.findOne(2)).thenReturn(new Venue(2, "BoxSeats", BigDecimal.valueOf(150.00d), 12, 100));
		when(repository.findOne(3)).thenReturn(new Venue(3, "Mezzanine", BigDecimal.valueOf(30.00d), 15, 150));
		when(repository.findOne(4)).thenReturn(new Venue(4, "Gallery", BigDecimal.valueOf(20.00d), 15, 150));
		return repository;
	}

	@Bean
	public CustomerRepository customerRepository() {
		CustomerRepository repository = mock(CustomerRepository.class);
		when(repository.findByEmail("amina@aslam.com")).thenReturn(new Customer("amina@aslam.com"));
		when(repository.save(any(Customer.class))).thenReturn(new Customer("ali@salar.com"));
		return repository;
	}

	@Bean
	public SeatHoldRepository seatHoldRepository() {
		LocalDateTime now = LocalDateTime.now();
		Instant nowInstant = now.atZone(ZoneId.systemDefault()).toInstant();
		LocalDateTime expired = now.minusSeconds(SEAT_HOLD_EXPIRATION_TIME_IN_SECONDS + 100L);
		Instant expiredInstant = expired.atZone(ZoneId.systemDefault()).toInstant();

		SeatHoldRepository repository = mock(SeatHoldRepository.class);
		SeatHold seatHold = new SeatHold();
		seatHold.setId(11);
		seatHold.setCustomer(new Customer("amina@aslam.com"));
		seatHold.getSeatOrders().add(new SeatOrder());
		seatHold.setHoldTime(Date.from(nowInstant));

		SeatHold expiredSeatHold = new SeatHold();
		expiredSeatHold.setId(5);
		expiredSeatHold.setCustomer(new Customer("amina@aslam.com"));
		expiredSeatHold.setHoldTime(Date.from(expiredInstant));

		when(repository.save(any(SeatHold.class))).thenReturn(seatHold);
		when(repository.findOne(15)).thenReturn(seatHold);
		when(repository.findOne(5)).thenReturn(expiredSeatHold);

		List<SeatHold> expiredSeatHolds = new ArrayList<>();
		expiredSeatHolds.add(expiredSeatHold);
		when(repository.findByConfirmationCodeAndHoldTimeBefore(any(Date.class))).thenReturn(expiredSeatHolds);
		return repository;
	}

	@Bean
	public SeatOrderRepository seatOrderRepository() {
		SeatOrderRepository repository = mock(SeatOrderRepository.class);
		List<SeatOrder> seatOrders = new ArrayList<>();
		SeatOrder seatOrder1 = new SeatOrder(new SeatHold(), new Venue(), 500);
		SeatOrder seatOrder2 = new SeatOrder(new SeatHold(), new Venue(), 100);
		seatOrders.add(seatOrder1);
		seatOrders.add(seatOrder2);
		when(repository.findByVenue(any(Venue.class))).thenReturn(seatOrders);
		return repository;
	}

	@Bean
	public TicketService ticketService() {
		return new TicketServiceImpl();
	}


}
