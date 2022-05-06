package org.talon.ad.datamodel.unitcondition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.datamodel.AdEntity;

import javax.persistence.*;

/**
 * Created by Zelong
 * On 2022/5/2
 * Lookup table for mapping between creative_id and unit_id
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "creative_unit")
public class CreativeUnit extends AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "creative_id", nullable = false)
    private Long creativeId;

    @Column(name = "unit_id", nullable = false)
    private Long unitId;

    public CreativeUnit(Long creativeId, Long unitId) {
        this.creativeId = creativeId;
        this.unitId = unitId;
    }
}
