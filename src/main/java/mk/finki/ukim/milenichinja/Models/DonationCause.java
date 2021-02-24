package mk.finki.ukim.milenichinja.Models;

import lombok.Data;
import mk.finki.ukim.milenichinja.Models.Enums.Status;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "donation_cause", schema = "vdomuvanje_milenichinja_wp")
public class DonationCause {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private String imageUrl;
    private double goal;
    @ManyToMany
    private List<Pet> pets;
    @Enumerated(value = EnumType.STRING)
    private Status status;
    private int importance;

    public DonationCause() {
    }

    public DonationCause(String name, String description, String imageUrl, double goal, List<Pet> pets, Status status, int importance) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.goal = goal;
        this.pets = pets;
        this.status = status;
        this.importance = importance;
    }
}