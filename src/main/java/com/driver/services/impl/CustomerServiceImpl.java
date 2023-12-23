package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
        customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer = customerRepository2.findById(customerId).get();
		List<TripBooking> bookedTrip = customer.getTripBookingList();

		for (TripBooking tripBooking : bookedTrip) {
			Driver driver = tripBooking.getDriver();
			Cab cab = driver.getCab();
			cab.setAvailable(true);
			driverRepository2.save(driver);
			tripBooking.setStatus(TripStatus.CANCELED);
		}

		customerRepository2.delete(customer);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

		List<Driver> driverList = driverRepository2.findAll();
		Driver driver = null;
		for (Driver d : driverList) {
			if (d.getCab().isAvailable()) {
				if ((driver == null) || (d.getDriverId() < driver.getDriverId())) {
					driver = d;
				}
			}
		}

		if (driver == null) {
			throw new Exception("No can available");
		}

		TripBooking tripBooking = new TripBooking();
		tripBooking.setCustomer(customerRepository2.findById(customerId).get());
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		tripBooking.setDriver(driver);
		int rate = driver.getCab().getPerKmRate();
		tripBooking.setBill(distanceInKm * rate);

		driver.getCab().setAvailable(false);
		driverRepository2.save(driver);

		Customer customer = customerRepository2.findById(customerId).get();
		customer.getTripBookingList().add(tripBooking);
		customerRepository2.save(customer);

		tripBookingRepository2.save(tripBooking);
		return tripBooking;

	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking booking = tripBookingRepository2.findById(tripId).get();
		booking.setStatus(TripStatus.CANCELED);
		booking.setBill(0);
		booking.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(booking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.COMPLETED);
		tripBooking.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(tripBooking);
	}
}
