# Ticketservice

Implement a simple ticket service that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance venue.

# Instructions to Download and Run the unit test cases:

git clone https://github.com/aminaaslam/ticketservice.git cd gs-rest-ticket-service mvn clean compile package After running these steps user should see following output on the screen to ensure application ran successfully. [INFO] ------------------------------------------------------------------------ [INFO] BUILD SUCCESS [INFO] ------------------------------------------------------------------------ Results : Tests run: 5, Failures: 0, Errors: 0, Skipped: 0

# Design and Implementation:
 
This service is implemented in Spring Boot which provides alot of convenience classes, interfaces and templates that reduces the amount of code which needs to be written significantly.

Spring Boot
H2 DataBase
Spring-Boot-Starter-Data-jpa

# Testing TicketService Application:

Following test cases have been written to test different functionalities provided by the application. 1.testNumSeatsAvailable() 2.testFindAndHoldSeats() 3.testReserveSeats() 4.testReserveSeatsWithExpiredSeatHoldId() // This test is to simulate the case where the SeatHold has expired and deleted //already. It should throw SeatHoldNotFoundException 5.testReserveSeatsWithInvalidEmail() // This test is to simulate the case when invalid email address for a customer is provided.

# Assumptions and Limitations:

This implementation of ticketservice application doesnot provide a disk-based storage, a REST API, and a front-end GUI
User has to run the unit test cases to verify the functionality of the application
Test data and object is created when the application boots up and runs the test cases with that data.
