package com.driver.controllers;


import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class AirportController {
    private final Map<String, Airport> airports = new HashMap<>();
    private final Map<Integer, Flight> flights = new HashMap<>();
    private final Map<Integer, Passenger> passengers = new HashMap<>();

    public AirportController() {
    }

    @PostMapping("/add_airport")
    public String addAirport(@RequestBody Airport airport) {
        airports.put(airport.getAirportName(), airport);
        return "SUCCESS";
    }

    @GetMapping("/get-largest-airport")
    public String getLargestAirportName() {
        int maxTerminals = 0;
        String largestAirportName = null;

        for (Airport airport : airports.values()) {
            if (airport.getNoOfTerminals() > maxTerminals) {
                maxTerminals = airport.getNoOfTerminals();
                largestAirportName = airport.getAirportName();
            } else if (airport.getNoOfTerminals() == maxTerminals) {
                if (largestAirportName == null || airport.getAirportName().compareTo(largestAirportName) < 0) {
                    largestAirportName = airport.getAirportName();
                }
            }
        }

        return largestAirportName;
    }

    @GetMapping("/get-shortest-time-travel-between-cities")
    public double getShortestDurationOfPossibleBetweenTwoCities(
            @RequestParam("fromCity") City fromCity,
            @RequestParam("toCity") City toCity
    ) {
        double shortestDuration = -1;

        for (Flight flight : flights.values()) {
            if (flight.getFromCity() == fromCity && flight.getToCity() == toCity) {
                if (shortestDuration == -1 || flight.getDuration() < shortestDuration) {
                    shortestDuration = flight.getDuration();
                }
            }
        }

        return shortestDuration;
    }

    @GetMapping("/get-number-of-people-on-airport-on/{date}")
    public int getNumberOfPeopleOn(@PathVariable("date") Date date, @RequestParam("airportName") String airportName) {
        int count = 0;

        for (Flight flight : flights.values()) {
            if (flight.getFromCity() != null && flight.getFromCity().name().equals(airportName) &&
                    flight.getFlightDate() != null && flight.getFlightDate().equals(date)) {
                count++;
            }
        }

        return count;
    }

    @GetMapping("/calculate-fare")
    public int calculateFlightFare(@RequestParam("flightId") Integer flightId) {
        Flight flight = flights.get(flightId);
        if (flight == null) {
            return -1;
        }

        int noOfPeopleWhoHaveAlreadyBooked = flight.getBookings() != null ? flight.getBookings().size() : 0;
        return 3000 + noOfPeopleWhoHaveAlreadyBooked * 50;
    }

    @PostMapping("/book-a-ticket")
    public String bookATicket(@RequestParam("flightId") Integer flightId, @RequestParam("passengerId") Integer passengerId) {
        Flight flight = flights.get(flightId);
        Passenger passenger = passengers.get(passengerId);
        if (flight == null || passenger == null) {
            return "FAILURE";
        }

        if (flight.getBookings() == null) {
            flight.setBookings(new ArrayList<>());
        }

        if (flight.getBookings().size() >= flight.getMaxCapacity() || flight.getBookings().contains(passengerId)) {
            return "FAILURE";
        }

        flight.getBookings().add(passengerId);
        return "SUCCESS";
    }

    @PutMapping("/cancel-a-ticket")
    public String cancelATicket(@RequestParam("flightId") Integer flightId, @RequestParam("passengerId") Integer passengerId) {
        Flight flight = flights.get(flightId);
        if (flight == null || flight.getBookings() == null) {
            return "FAILURE";
        }

        if (!flight.getBookings().contains(passengerId)) {
            return "FAILURE";
        }

        flight.getBookings().remove(passengerId);
        return "SUCCESS";
    }

    @GetMapping("/get-count-of-bookings-done-by-a-passenger/{passengerId}")
    public int countOfBookingsDoneByPassengerAllCombined(@PathVariable("passengerId") Integer passengerId) {
        int count = 0;

        for (Flight flight : flights.values()) {
            if (flight.getBookings() != null && flight.getBookings().contains(passengerId)) {
                count++;
            }
        }
        return count;
    }

    @PostMapping("/add-flight")
    public String addFlight(@RequestBody Flight flight) {
        flights.put(flight.getFlightId(), flight);
        return "SUCCESS";
    }

    @GetMapping("/get-aiportName-from-flight-takeoff/{flightId}")
    public String getAirportNameFromFlightId(@PathVariable("flightId") Integer flightId) {
        Flight flight = flights.get(flightId);
        if (flight == null || flight.getFromCity() == null) {
            return null;
        }
        return flight.getFromCity().name();
    }

    @GetMapping("/calculate-revenue-collected/{flightId}")
    public int calculateRevenueOfAFlight(@PathVariable("flightId") Integer flightId) {
        Flight flight = flights.get(flightId);
        if (flight == null || flight.getBookings() == null) {
            return 0;
        }

        int totalRevenue = 3000 * flight.getBookings().size();
        return totalRevenue;
    }


    @PostMapping("/add-passenger")
    public String addPassenger(@RequestBody Passenger passenger) {
        passengers.put(passenger.getPassengerId(), passenger);
        return "SUCCESS";
    }
}
