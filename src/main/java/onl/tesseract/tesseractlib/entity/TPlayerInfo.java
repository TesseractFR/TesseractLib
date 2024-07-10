package onl.tesseract.tesseractlib.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import onl.tesseract.tesseractlib.achievement.Achievement;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.cosmetics.FlyFilter;
import onl.tesseract.tesseractlib.cosmetics.TeleportationAnimation;
import onl.tesseract.tesseractlib.player.Gender;
import org.hibernate.annotations.JdbcTypeCode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Data
@Table(name = "t_player",
        uniqueConstraints = {@UniqueConstraint(columnNames = "uuid")},
        indexes = @Index(name = "idx_uuid", columnList = "uuid"))
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

    @ElementCollection(targetClass = ElytraTrails.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "t_player_elytra_trails", joinColumns = @JoinColumn(name = "player_uuid"))
    @Column(name = "trails")
    Set<ElytraTrails> elytra_trails;

    @ElementCollection(targetClass = FlyFilter.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "t_player_jetpack_filter", joinColumns = @JoinColumn(name = "player_uuid"))
    @Column(name = "filter")
    Set<FlyFilter> fly_filters;

    @ElementCollection(targetClass = TeleportationAnimation.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "t_player_teleportation_animation", joinColumns = @JoinColumn(name = "player_uuid"))
    @Column(name = "animation")
    Set<TeleportationAnimation> teleportation_animations;

    @ManyToMany
    @JoinTable(
            name = "t_player_achievement",
            joinColumns = @JoinColumn(name = "player_uuid"),
            inverseJoinColumns = @JoinColumn(name = "achievement_id")
    )
    Set<Achievement> achievements = new HashSet<>();


}
