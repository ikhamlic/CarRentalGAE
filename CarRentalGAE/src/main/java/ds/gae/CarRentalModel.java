package ds.gae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import ds.gae.entities.Car;
import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.CarType;
import ds.gae.entities.Quote;
import ds.gae.entities.Reservation;
import ds.gae.entities.ReservationConstraints;
 
public class CarRentalModel {
	
	public Map<String,CarRentalCompany> CRCS = new HashMap<String, CarRentalCompany>();	
	
	private static CarRentalModel instance;
	
	public static CarRentalModel get() {
		if (instance == null)
			instance = new CarRentalModel();
		return instance;
	}
		
	/**
	 * Get the car types available in the given car rental company.
	 *
	 * @param 	crcName
	 * 			the car rental company
	 * @return	The list of car types (i.e. name of car type), available
	 * 			in the given car rental company.
	 */
	public Set<String> getCarTypesNames(String crcName) {
		EntityManager em = EMF.get().createEntityManager();
		
		@SuppressWarnings("unchecked")
		List<String> query = em.createQuery(
				"SELECT ct.name FROM CarType ct"
				).getResultList();
		em.close();
		
    	return new HashSet<String>(query);
	}

    /**
     * Get all registered car rental companies
     *
     * @return	the list of car rental companies
     */
    @SuppressWarnings("unchecked")
	public Collection<String> getAllRentalCompanyNames() {
		EntityManager em = EMF.get().createEntityManager();
		try {
			Query query = em.createQuery(
						"SELECT crc.name FROM CarRentalCompany crc"
					);

			return query.getResultList();
		} finally {
			em.close();
		}

		
    }
	
	/**
	 * Create a quote according to the given reservation constraints (tentative reservation).
	 * 
	 * @param	company
	 * 			name of the car renter company
	 * @param	renterName 
	 * 			name of the car renter 
	 * @param 	constraints
	 * 			reservation constraints for the quote
	 * @return	The newly created quote.
	 *  
	 * @throws ReservationException
	 * 			No car available that fits the given constraints.
	 */
    public Quote createQuote(String company, String renterName, ReservationConstraints constraints) throws ReservationException {
		// FIXME: use persistence instead
    	
    	CarRentalCompany crc = CRCS.get(company);
    	Quote out = null;

        if (crc != null) {
            out = crc.createQuote(constraints, renterName);
        } else {
        	throw new ReservationException("CarRentalCompany not found.");    	
        }
        
        return out;
    }
    
	/**
	 * Confirm the given quote.
	 *
	 * @param 	q
	 * 			Quote to confirm
	 * 
	 * @throws ReservationException
	 * 			Confirmation of given quote failed.	
	 */
	public void confirmQuote(Quote q) throws ReservationException {
		// FIXME: use persistence instead

		CarRentalCompany crc = CRCS.get(q.getRentalCompany());
        crc.confirmQuote(q);
	}
	
    /**
	 * Confirm the given list of quotes
	 * 
	 * @param 	quotes 
	 * 			the quotes to confirm
	 * @return	The list of reservations, resulting from confirming all given quotes.
	 * 
	 * @throws 	ReservationException
	 * 			One of the quotes cannot be confirmed. 
	 * 			Therefore none of the given quotes is confirmed.
	 */
    public List<Reservation> confirmQuotes(List<Quote> quotes) throws ReservationException {    	
		// TODO add implementation
    	return null;
    }
	
	/**
	 * Get all reservations made by the given car renter.
	 *
	 * @param 	renter
	 * 			name of the car renter
	 * @return	the list of reservations of the given car renter
	 */
	public List<Reservation> getReservations(String renter) {
		// FIXME: use persistence instead
		
		EntityManager em = EMF.get().createEntityManager();
		
		@SuppressWarnings("unchecked")
		List<Reservation> query = em.createQuery(
					"SELECT r FROM Reservation r WHERE r.carRenter = :renter"
				).setParameter("renter", renter).getResultList();
		em.close();
		return query;
    }

    /**
     * Get the car types available in the given car rental company.
     *
     * @param 	crcName
     * 			the given car rental company
     * @return	The list of car types in the given car rental company.
     */
    public Collection<CarType> getCarTypesOfCarRentalCompany(String crcName) {
		// FIXME: use persistence instead

    	EntityManager em = EMF.get().createEntityManager();
    	
    	@SuppressWarnings("unchecked")
		Map<String,CarType> query = (Map<String,CarType>)em.createQuery(
    				"SELECT crc.carTypes FROM CarRentalCompany crc WHERE crc.name = :crcName"
    			).setParameter("crcName", crcName).getResultList().get(0);
    	em.close();
    	
        return query.values();
    }
	
    /**
     * Get the list of cars of the given car type in the given car rental company.
     *
     * @param	crcName
	 * 			name of the car rental company
     * @param 	carType
     * 			the given car type
     * @return	A list of car IDs of cars with the given car type.
     */
    public Collection<Integer> getCarIdsByCarType(String crcName, CarType carType) {
    	Collection<Integer> out = new ArrayList<Integer>();
    	for (Car c : getCarsByCarType(crcName, carType)) {
    		out.add(c.getId());
    	}
    	return out;
    }
    
    /**
     * Get the amount of cars of the given car type in the given car rental company.
     *
     * @param	crcName
	 * 			name of the car rental company
     * @param 	carType
     * 			the given car type
     * @return	A number, representing the amount of cars of the given car type.
     */
    public int getAmountOfCarsByCarType(String crcName, CarType carType) {
    	return this.getCarsByCarType(crcName, carType).size();
    }

	/**
	 * Get the list of cars of the given car type in the given car rental company.
	 *
	 * @param	crcName
	 * 			name of the car rental company
	 * @param 	carType
	 * 			the given car type
	 * @return	List of cars of the given car type
	 */
	private List<Car> getCarsByCarType(String crcName, CarType carType) {				
		// FIXME: use persistence instead

		EntityManager em = EMF.get().createEntityManager();
		
		@SuppressWarnings("unchecked")
		Set<Car> query = (Set<Car>)em.createQuery(
					"SELECT crc.cars FROM CarRentalCompany crc WHERE crc.name = :crcName"
				).setParameter("crcName", crcName).getResultList().get(0);
		em.close();
		List<Car> output = new ArrayList<>();
		for (Car c : query) {
			if (c.getType().equals(carType)) {
				output.add(c);
			}
		}
		
		return output;
	}

	/**
	 * Check whether the given car renter has reservations.
	 *
	 * @param 	renter
	 * 			the car renter
	 * @return	True if the number of reservations of the given car renter is higher than 0.
	 * 			False otherwise.
	 */
	public boolean hasReservations(String renter) {
		return this.getReservations(renter).size() > 0;		
	}

	public void persistCarRentalCompany(CarRentalCompany company) {
		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			em.persist(company);
			tx.commit();
		} finally {
			//If I close the EntityManager I will get an com.google.appengine.datanucleus.EntityUtils$ChildWithoutParentException
			//exception. If I don't close the EntityManager I don't get this error...
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
		
//		try{
//		    EntityTransaction t = em.getTransaction();
//		    try{
//		        t.begin();
//		        //Do stuff
//		        em.persist(company);
//		        t.commit();
//		    }finally {
//		        if (t.isActive())
//		            t.rollback();
//		    }
//		} finally {
//		    em.close();
//		}
	}
}