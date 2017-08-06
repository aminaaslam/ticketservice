package org.amina.ticketservice.utils;

import java.util.UUID;

public class ConfirmationCodeUtils {
	
	private ConfirmationCodeUtils() {}

	public static String generateConfirmationCode(final int seatHoldId, final String customerEmail) {
		String name = String.join(",", "TicketService", customerEmail, String.valueOf(seatHoldId));
		UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
		return uuid.toString();
	}

}
