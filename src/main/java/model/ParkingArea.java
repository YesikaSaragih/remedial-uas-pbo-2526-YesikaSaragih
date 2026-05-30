package model;

/**
 * 12S24024 - Yesika Nadia Saragih
 **/

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_areas")
public class ParkingArea {

    @Id
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "allowed_type", nullable = false, length = 20)
    private String allowedType;

    @OneToMany(mappedBy = "parkingArea", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<Vehicle> vehicles = new ArrayList<>();

    public ParkingArea() {
    }

    public ParkingArea(String name, int capacity, String allowedType) {
        this.name = name;
        this.capacity = capacity;
        this.allowedType = allowedType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getAllowedType() {
        return allowedType;
    }

    public void setAllowedType(String allowedType) {
        this.allowedType = allowedType;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}
