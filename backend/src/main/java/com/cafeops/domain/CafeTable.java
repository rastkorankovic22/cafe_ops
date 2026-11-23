package com.cafeops.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cafe_table")
public class CafeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cafe_id", nullable = false)
    private Long cafeId;

    @Column(nullable = false, length = 20)
    private String label;

    @Column(name = "pos_x", nullable = false)
    private int posX;

    @Column(name = "pos_y", nullable = false)
    private int posY;

    @Column(nullable = false)
    private boolean active;

    public Long getId() {
        return id;
    }

    public Long getCafeId() {
        return cafeId;
    }

    public String getLabel() {
        return label;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public boolean isActive() {
        return active;
    }

    public void setCafeId(Long cafeId) {
        this.cafeId = cafeId;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}