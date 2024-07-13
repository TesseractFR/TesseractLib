package onl.tesseract.tesseractlib.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.cosmetics.FlyFilter;
import onl.tesseract.tesseractlib.cosmetics.TeleportationAnimation;
import onl.tesseract.tesseractlib.cosmetics.familier.Pet;
import onl.tesseract.tesseractlib.player.Gender;
import org.hibernate.annotations.JdbcTypeCode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Data
@Table(name = "t_player", uniqueConstraints = {@UniqueConstraint(columnNames = "uuid")}, indexes = @Index(name = "idx_uuid", columnList = "uuid"))
public class TPlayerInfo implements Serializable {
    @Id
    @Column(updatable = false, nullable = false, columnDefinition = "VARCHAR(36)", unique = true)
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    UUID uuid;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Gender genre;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ElytraTrails active_trail;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    FlyFilter active_fly_filter;

    @Column(nullable = false)
    int market_currency;

    @ManyToMany
    @JoinTable(name = "t_player_achievement", joinColumns = @JoinColumn(name = "player_uuid"), inverseJoinColumns = @JoinColumn(name = "achievement_id"))
    Set<Achievement> achievements = new HashSet<>();


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "t_player_cosmetics", joinColumns = @JoinColumn(name = "player_uuid"))
    @AttributeOverrides({@AttributeOverride(name = "cosmetic_type", column = @Column(name = "cosmetic_type")), @AttributeOverride(name = "cosmetic", column = @Column(name = "cosmetic"))})
    private Set<CosmeticEntity> cosmetics = new HashSet<>();

    @Transient
    private Set<ElytraTrails> elytraTrails = new HashSet<>();

    @Transient
    private Set<FlyFilter> flyFilters = new HashSet<>();

    @Transient
    private Set<Pet> pets = new HashSet<>();

    @Transient
    private Set<TeleportationAnimation> teleportationAnimations = new HashSet<>();

    @PostLoad
    private void populateCosmetics() {
        for (CosmeticEntity cosmetic : cosmetics) {
            if (ElytraTrails.getTypeName().equals(cosmetic.getCosmetic_type())) {
                elytraTrails.add(ElytraTrails.valueOf(cosmetic.getCosmetic()));
            } else if (FlyFilter.getTypeName().equals(cosmetic.getCosmetic_type())) {
                flyFilters.add(FlyFilter.valueOf(cosmetic.getCosmetic()));
            } else if (TeleportationAnimation.getTypeName().equals(cosmetic.getCosmetic_type())) {
                teleportationAnimations.add(TeleportationAnimation.valueOf(cosmetic.getCosmetic()));
            } else if (Pet.getTypeName().equals(cosmetic.getCosmetic_type())) {
                pets.add(Pet.valueOf(cosmetic.getCosmetic()));
            }
        }
    }

    @PrePersist
    @PreUpdate
    private void populateCosmeticEntities() {
        cosmetics.clear();
        for (ElytraTrails trail : elytraTrails) {
            cosmetics.add(new CosmeticEntity(ElytraTrails.getTypeName(), trail.name()));
        }
        for (FlyFilter filter : flyFilters) {
            cosmetics.add(new CosmeticEntity("FlyFilter", filter.name()));
        }
    }

}
