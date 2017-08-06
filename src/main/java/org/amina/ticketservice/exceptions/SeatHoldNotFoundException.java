package org.amina.ticketservice.exceptions;

public class SeatHoldNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6953709059986084207L;
	public SeatHoldNotFoundException() { super(); }
	public SeatHoldNotFoundException(String s) { super(s); }
}
