package onl.tesseract.tesseractlib.achievement;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "t_achievement",
        uniqueConstraints = {@UniqueConstraint(columnNames = "id")},
        indexes = @Index(name = "idx_id", columnList = "id"))
public class Achievement {

    @ManyToOne
    Title title;
    @Column(columnDefinition = "VARCHAR(255)")
    String condition;
    String name;
    float lys;
    int ptsIllumination;
    @Id
    int id;
    String displayName;


    public Achievement(int id, Title title, String name,String displayName, String condition, float lys, int ptsIllumination)
    {
        this.id = id;
        this.title = title;
        this.name = name;
        this.displayName = displayName;
        this.condition = condition;
        this.lys = lys;
        this.ptsIllumination = ptsIllumination;
    }
}



