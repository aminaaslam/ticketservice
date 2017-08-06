package org.amina.ticketservice.repository;

import java.util.List;

import org.amina.ticketservice.entity.SeatHold;
import org.amina.ticketservice.entity.Venue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("VenueRepository")
public interface VenueRepository  extends JpaRepository<Venue, Integer> {
	
	List<Venue> findByVenueIdAndLevelId(Integer venueId, Integer levelId);

}
