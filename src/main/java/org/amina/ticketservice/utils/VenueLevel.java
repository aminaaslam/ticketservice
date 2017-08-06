package org.amina.ticketservice.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/*
 * This class represents different venue levels in a venue. 
 * There are four levels in my venue
 */
public enum VenueLevel {
	
	ORCHESTRA("Orchestra", 1),
	BoxSeats("BoxSeats", 2),
	Mezzanine("Mezzanine", 3),
	Gallery("Gallery", 4);

	private final Integer id;
	private final String name;

	private VenueLevel(final String name, final Integer id) {
		this.name = name;
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public boolean equalsName(final String alternateName){
		return (alternateName == null) ? false : name.equalsIgnoreCase(alternateName);
	}

	public String toString() {
		return name;
	}

	/**
	 * Find all venue levels between minLevel and maxLevel
	 * @param minLevel lower level 
	 * @param maxLevel higher level 
	 * @return list of venueLevel enum
	 */
	public static List<VenueLevel> getVenueLevels(Optional<Integer> minLevel, Optional<Integer> maxLevel) {
		List<VenueLevel> levels = new ArrayList<>();
		Integer min = minLevel.isPresent() ? minLevel.get() : 1;
		Integer max = maxLevel.isPresent() ? maxLevel.get() : 4;

		if(max < min) {
			min = max;
		}

		for(VenueLevel venue : VenueLevel.values()) {
			if( min <= venue.getId() && venue.getId() <= max) {
				levels.add(venue);
			}
		}
		return levels;
	}


}
