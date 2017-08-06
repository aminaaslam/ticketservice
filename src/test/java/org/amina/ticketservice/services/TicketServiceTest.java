package org.amina.ticketservice.services;

import static org.junit.Assert.*;
import java.util.Optional;
import org.amina.ticketservice.entity.SeatHold;
import org.amina.ticketservice.exceptions.CustomerValidationException;
import org.amina.ticketservice.exceptions.SeatHoldNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(classes = ServiceTestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TicketServiceTest {
	
	@Autowired
	private TicketServiceImpl ticketService = new TicketServiceImpl();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNumSeatsAvailable() {
		
		int numSeatsAvail = ticketService.numSeatsAvailable(Optional.of(1));
		System.out.println("this is the number of seat" + numSeatsAvail);
		assertTrue(0 <=numSeatsAvail);
		
	}
	
	/**
	 * Test for checking Find and Hold Seat functionality
	 * mocked customer is ready for the email amina@aslam.com
	 */
	@Test
	public void testFindAndHoldSeats() {
		SeatHold seatHold = ticketService.findAndHoldSeats(900, Optional.of(1), Optional.of(3), "amina@aslam.com");
		assertNotNull(seatHold);
		assertTrue(StringUtils.equalsIgnoreCase(seatHold.getCustomer().getEmail(), "amina@aslam.com"));
		assertFalse(seatHold.getSeatOrders().isEmpty());
	}
	
	/**
	 * Test for checking ReserveSeat functionality
	 * Mocked repositories has customer for email amina@aslam.com and SeatHoldId = 15
	 */
	@Test
	public void testReserveSeats() {
		String confirmationCode = ticketService.reserveSeats(15, "amina@aslam.com");
		assertFalse(confirmationCode.isEmpty());
	}
	
	/**
	 * Test with passing SeatHoldId which doesnot exist database
	 * This is test for simulating the case the SeatHold is expired and deleted already
	 * It should throw SeatHoldNotFoundException
	 */
	@Test(expected=SeatHoldNotFoundException.class)
	public void testReserveSeatsWithExpiredSeatHoldId() {
		ticketService.reserveSeats(5, "amina@aslam.com");
	}
	
	/**
	 * This test is for the case where given a wrong email address for the seatHold 
	 * It should throw exception for email verification failure
	 */
	@Test(expected=CustomerValidationException.class)
	public void testReserveSeatsWithInvalidEmail() {
		ticketService.reserveSeats(15, "hasan@abc.com");
	}



}
