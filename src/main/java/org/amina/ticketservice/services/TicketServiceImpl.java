package org.amina.ticketservice.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.amina.ticketservice.entity.Customer;
import org.amina.ticketservice.entity.SeatHold;
import org.amina.ticketservice.entity.SeatOrder;
import org.amina.ticketservice.entity.Venue;
import org.amina.ticketservice.exceptions.CustomerValidationException;
import org.amina.ticketservice.exceptions.SeatHoldNotFoundException;
import org.amina.ticketservice.repository.CustomerRepository;
import org.amina.ticketservice.repository.SeatHoldRepository;
import org.amina.ticketservice.repository.SeatOrderRepository;
import org.amina.ticketservice.repository.VenueRepository;
import org.amina.ticketservice.utils.ConfirmationCodeUtils;
import org.amina.ticketservice.utils.VenueLevel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service("ticketService")
public class TicketServiceImpl implements TicketService {
	
	private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);
	
	private final long secondsToExpire = 10L;
	
	@Autowired
	private VenueRepository venueRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private SeatHoldRepository seatHoldRepository;

	@Autowired
	private SeatOrderRepository seatOrderRepository;

	
	
	@Override
	public int numSeatsAvailable(Optional<Integer> venueLevel) {
	 // remove expired hold seats
	 removeExpiredSeatHolds();
     if(venueLevel.isPresent()) {
		 Venue venue = venueRepository.findOne(venueLevel.get());
		 if(venue!=null) {
			  return getAvailableSeatsInVenueLevel(venue);
			}
	      } else {
			  // If no venueLevel information is given, search total available seats through all levels
			  List<Venue> venues = venueRepository.findAll();
			  return venues.stream().mapToInt(venue -> getAvailableSeatsInVenueLevel(venue)).sum();
	   }
	 return 0;
	}
	
	/**
	 * Get available seats for given venue level
	 * @param venue VenueLevel entity
	 * @return number of available seats
	 */
	private int getAvailableSeatsInVenueLevel(Venue venue) {
		List<SeatOrder> seatOrders = seatOrderRepository.findByVenue(venue);
		int numberOfSeatTaken = seatOrders.stream().mapToInt(SeatOrder::getNumberOfSeats).sum();
		int totalNumberOfSeat = venue.getNumberOfRow() * venue.getSeatsInRow();
		return totalNumberOfSeat - numberOfSeatTaken;
	}

	
	/**
	 * Remove expired SeatHolds
	 */
	private void removeExpiredSeatHolds() {
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expired = now.minusSeconds(secondsToExpire);
		Instant expiredInstant = expired.atZone(ZoneId.systemDefault()).toInstant();
        List<SeatHold> expiredHolds = seatHoldRepository.findByConfirmationCodeAndHoldTimeBefore(Date.from(expiredInstant));
		if(!expiredHolds.isEmpty()) {
			seatHoldRepository.delete(expiredHolds);
		}
	}

	@Override
	public SeatHold findAndHoldSeats(final int numSeats, final Optional<Integer> minLevel, final Optional<Integer> maxLevel, final String customerEmail) {
		//Validate if the customer email is present or not
		if(StringUtils.isEmpty(customerEmail)) {
			log.warn("Get empty email, so ignore hold request");
			throw new CustomerValidationException();
		}
		Customer customer = customerRepository.findByEmail(customerEmail);
		List<VenueLevel> venueLevels = getVenueAndCustomerInformation(minLevel,maxLevel,customerEmail,customer);
				
		//Create SeatHold object and set the customer email
		SeatHold seatHold = new SeatHold();
		seatHold.setCustomer(customer);

		int seatsToHold = findSeatsThroughVenueLevels(numSeats,customer,venueLevels,seatHold);
		
       // save the seat holds
		if( seatHold.getSeatOrders().isEmpty() ) {
			log.warn("failed to hold any seats in given levels for customer email: ".concat(customerEmail));
		} else {
			seatHold.setHoldTime(new Date());
			seatHold = seatHoldRepository.save(seatHold);
			String message = new StringBuilder("hold ")
					.append(numSeats - seatsToHold)
					.append(" seats held for email: ")
					.append(customerEmail)
					.toString();
			log.info(message);
		}
		return seatHold;
	}
	
	/*
	 * This function does following 
	 *  1. Finds all seats between venue levels
	 *  @return seatsToHold: number of seats to Hold asked by the customer
	 */
	
	private int findSeatsThroughVenueLevels(final int numSeats,Customer customer,final List<VenueLevel> venueLevels,final SeatHold seatHold) {
		// Find seats through venue levels
		int seatsToHold = numSeats;
		for(VenueLevel venueLevel: venueLevels) {
		if(seatsToHold > 0 ) {
		  Venue venue = venueRepository.findOne(venueLevel.getId());
		  int numSeatsAvail = numSeatsAvailable(Optional.of(venueLevel.getId()));
		  if( numSeatsAvail > 0) {
		    if(numSeatsAvail >= seatsToHold) {	// more seats available
			  seatHold.getSeatOrders().add( new SeatOrder(seatHold, venue, seatsToHold) );
			  seatsToHold = 0;
			  break;
		  } else {							
			 // not enough seats available in this particular level
			 seatHold.getSeatOrders().add( new SeatOrder(seatHold, venue, numSeatsAvail) );
			 seatsToHold = seatsToHold - numSeatsAvail;
		   }
		 }
	    }
	  }	
	  return seatsToHold;
	}
	
	/*
	 * This function does following 
	 *  1. Finds all venue levels between minLevel and maxLevel 
	 *  2. Creates a new Customer if one isnt already present
	 *   @return List<VenueLevel>: information on venue levels between min and max level
	 */
	
	private List<VenueLevel> getVenueAndCustomerInformation(final Optional<Integer> minLevel, final Optional<Integer> maxLevel,final String customerEmail,Customer customer) {
		
		// Get Venue and Customer info
		List<VenueLevel> venueLevels = VenueLevel.getVenueLevels(minLevel, maxLevel);
	    if(customer==null) {
				log.info(String.join(": ", "create new customer by email", customerEmail));
				customer = customerRepository.save(new Customer(customerEmail));
		} else {
				log.info(String.join(": ", "found existing customer by email", customerEmail));
		}
		return venueLevels;		
		
	}


	@Override
	public String reserveSeats(final int seatHoldId, final String customerEmail) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expired = now.minusSeconds(secondsToExpire);
		Instant expiredInstant = expired.atZone(ZoneId.systemDefault()).toInstant();

		// find SeatHold
		SeatHold seatHold = findSeatHold(seatHoldId);
		
		// verify customer info first
		Customer customer = verfiyCustomerInfo(seatHold,seatHoldId,customerEmail);
		

		// verify seatHold and reservation status
		if(StringUtils.isEmpty(seatHold.getConfirmationCode()) ) {
			if(seatHold.getHoldTime().before(Date.from(expiredInstant))) {
				String errorMessage = String.join(": ", "failed on reservation, the SeatHold is expired, seatHoldId", String.valueOf(seatHoldId));
				log.error(errorMessage);
				throw new SeatHoldNotFoundException(errorMessage);
			}
		} else {
			String message = new StringBuilder("The seatHold is already reservated, seatHoldId: ")
					.append(seatHoldId)
					.append(", customerEmail: ")
					.append(customerEmail)
					.toString();
			log.warn(message);
			return seatHold.getConfirmationCode();
		}

		// generate confirmation code and reserve seat(s)
		String code = ConfirmationCodeUtils.generateConfirmationCode(seatHoldId, customerEmail);
		seatHold.setConfirmationCode(code);
		seatHold.setReservationTime(new Date());
		seatHoldRepository.save(seatHold);
		String message = new StringBuilder("Reserved Seat for customer email: ")
				.append(customerEmail)
				.append(", seatHoldId: ")
				.append(seatHoldId)
				.append(", confirmationCode: ")
				.append(code)
				.toString();
		log.info(message);
		return code;
	}
	
	/**
	 * Find Hold on Seat(s)
	 * @param seatHoldId Id of seat to be reserved 
	 * @return SeatHold object searched through repository
	 */
	private SeatHold findSeatHold(final int seatHoldId) {
		// find SeatHold
	   SeatHold seatHold = seatHoldRepository.findOne(seatHoldId);
	   if(seatHold == null) {
		  String errMessage = String.join(": ", "fail on reservation, no SeatHold found. it probably already expired, seatHoldId", String.valueOf(seatHoldId));
		  log.error(errMessage);
		  throw new SeatHoldNotFoundException(errMessage);
	   }
	   return seatHold;

	}
	
	/**
	 * Verify Customer Information
	 * @param seatHoldId Id of seat to be reserved 
	 * @param customerEmail Email address of customer
	 * @return Customer Object
	 */
	
	private Customer verfiyCustomerInfo(SeatHold seatHold,final int seatHoldId, final String customerEmail) {
		// verify customer info first
		Customer customer = seatHold.getCustomer();
		if(customer == null || !StringUtils.equalsIgnoreCase(customer.getEmail(), customerEmail)) {
		  StringBuilder errorMessage = new StringBuilder("Customer's Email Validation on provided SeatHoldId fails, seatHoldId: ")
							.append(seatHoldId)
							.append(", customerEmail: ")
							.append(customerEmail);
		  log.error(errorMessage.toString());
		  throw new CustomerValidationException(errorMessage.toString());
		}
		return customer;
		
	}
	
}
