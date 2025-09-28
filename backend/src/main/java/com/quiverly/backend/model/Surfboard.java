package com.quiverly.backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "surfboards")
public class Surfboard {

    @Id
    @GeneratedValue
    private Long id;

    private String model;
    private String shaper;
    private Double length;
    private Double width;
    private Double volume;
    private LocalDate purchasedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    public Surfboard(){

    }

    public Surfboard(String model, String shaper, Double length, Double width, Double volume, User owner){
        this.model = model;
        this. shaper = shaper;
        this. length = length;
        this.width = width;
        this.volume = volume;
        this. owner = owner;
        this.purchasedAt = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getShaper() {
        return shaper;
    }

    public void setShaper(String shaper) {
        this.shaper = shaper;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public LocalDate getPurchasedAt() {
        return purchasedAt;
    }

    public void setPurchasedAt(LocalDate purchasedAt) {
        this.purchasedAt = purchasedAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}


