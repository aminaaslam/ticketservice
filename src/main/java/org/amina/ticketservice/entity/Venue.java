package org.amina.ticketservice.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "venue")
public class Venue {
	
    private Integer levelId;
	private String levelName;
	private BigDecimal price;
	private Integer numberOfRows;
	private Integer seatsInRow;
	private Set<SeatOrder> seatOrders = new HashSet<>();

	public Venue() {}

	public Venue(Integer levelId, String levelName, BigDecimal price, Integer numberOfRow, Integer seatsInRow) {
		this.levelId = levelId;
		this.levelName = levelName;
		this.price = price;
		this.numberOfRows = numberOfRow;
		this.seatsInRow = seatsInRow;
	}

	@Id
	@Column(name="LEVEL_ID")
	public Integer getLevelId() {
		return levelId;
	}
	public void setLevelId(Integer levelId) {
		this.levelId = levelId;
	}

	@Column(name="LEVEL_NAME", nullable=false)
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	@Column(name="PRICE", nullable=false)
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Column(name="NUMBER_OF_ROW", nullable=false)
	public Integer getNumberOfRow() {
		return numberOfRows;
	}
	public void setNumberOfRow(Integer numberOfRow) {
		this.numberOfRows = numberOfRow;
	}

	@Column(name="SEATS_IN_ROW", nullable=false)
	public Integer getSeatsInRow() {
		return seatsInRow;
	}
	public void setSeatsInRow(Integer seatsInRow) {
		this.seatsInRow = seatsInRow;
	}

	@OneToMany(cascade={CascadeType.ALL}, mappedBy="venue", fetch=FetchType.LAZY)
	public Set<SeatOrder> getSeatOrders() {
		return seatOrders;
	}
	public void setSeatOrders(Set<SeatOrder> seatOrders) {
		this.seatOrders = seatOrders;
	}
   

}
