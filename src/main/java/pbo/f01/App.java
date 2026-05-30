package pbo.f01;

import model.*;
import jakarta.persistence.*;
import java.util.Scanner;
import java.util.List;

/**
 * Driver class utama
 * Nama: Yesika Nadia Saragih
 * Nim: 12S24024
 */

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        
        try {
            emf = Persistence.createEntityManagerFactory("parkit-pu");
            em = emf.createEntityManager();
            
            Scanner scanner = new Scanner(System.in);
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }
                
                String[] tokens = line.split("#");
                if (tokens.length == 0) {
                    continue;
                }
                
                String command = tokens[0].trim();
                
                if (command.equals("area-add")) {
                    if (tokens.length >= 4) {
                        String name = tokens[1].trim();
                        int capacity = Integer.parseInt(tokens[2].trim());
                        String allowedType = tokens[3].trim();
                        
                        ParkingArea area = em.find(ParkingArea.class, name);
                        if (area == null) {
                            area = new ParkingArea(name, capacity, allowedType);
                            try {
                                em.getTransaction().begin();
                                em.persist(area);
                                em.getTransaction().commit();
                            } catch (Exception ex) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                            }
                        }
                    }
                } else if (command.equals("vehicle-add")) {
                    if (tokens.length >= 4) {
                        String plateNumber = tokens[1].trim();
                        String owner = tokens[2].trim();
                        String type = tokens[3].trim();
                        
                        Vehicle vehicle = em.find(Vehicle.class, plateNumber);
                        if (vehicle == null) {
                            vehicle = new Vehicle(plateNumber, owner, type);
                            try {
                                em.getTransaction().begin();
                                em.persist(vehicle);
                                em.getTransaction().commit();
                            } catch (Exception ex) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                            }
                        }
                    }
                } else if (command.equals("park")) {
                    if (tokens.length >= 3) {
                        String plateNumber = tokens[1].trim();
                        String areaName = tokens[2].trim();
                        
                        Vehicle vehicle = em.find(Vehicle.class, plateNumber);
                        ParkingArea area = em.find(ParkingArea.class, areaName);
                        
                        if (vehicle != null && area != null) {
                            // Validasi tipe kendaraan
                            if (vehicle.getType().equals(area.getAllowedType())) {
                                // Hitung okupansi saat ini untuk area tersebut
                                Long currentOccupancy = em.createQuery(
                                    "SELECT COUNT(v) FROM Vehicle v WHERE v.parkingArea = :area", Long.class)
                                    .setParameter("area", area)
                                    .getSingleResult();
                                
                                // Validasi kapasitas
                                if (currentOccupancy < area.getCapacity()) {
                                    try {
                                        em.getTransaction().begin();
                                        vehicle.setParkingArea(area);
                                        em.merge(vehicle);
                                        em.getTransaction().commit();
                                        
                                        // Refresh entity state
                                        em.refresh(vehicle);
                                        em.refresh(area);
                                    } catch (Exception ex) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (command.equals("display-all")) {
                    // Query all areas sorted by name asc
                    List<ParkingArea> areas = em.createQuery(
                        "SELECT a FROM ParkingArea a ORDER BY a.name ASC", ParkingArea.class)
                        .getResultList();
                    
                    for (ParkingArea a : areas) {
                        // Query vehicles parked in this area sorted by plate number asc
                        List<Vehicle> parkedVehicles = em.createQuery(
                            "SELECT v FROM Vehicle v WHERE v.parkingArea = :area ORDER BY v.plateNumber ASC", Vehicle.class)
                            .setParameter("area", a)
                            .getResultList();
                        
                        System.out.println(a.getName() + " " + a.getAllowedType() + " " + a.getCapacity() + "|" + parkedVehicles.size());
                        for (Vehicle v : parkedVehicles) {
                            System.out.println(v.getPlateNumber() + " " + v.getOwner() + " " + v.getType());
                        }
                    }
                }
            }
            
            scanner.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }
}
