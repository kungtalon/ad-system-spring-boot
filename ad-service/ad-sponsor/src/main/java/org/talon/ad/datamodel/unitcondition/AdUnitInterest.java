package org.talon.ad.datamodel.unitcondition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.datamodel.AdEntity;

import javax.persistence.*;

/**
 * Created by Zelong
 * On 2022/5/2
 * Restrictions on ad_unit about interests
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ad_unit_interest")
public class AdUnitInterest extends AdEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "unit_id", nullable = false)
    private Long unitId;

    @Column(name = "interest_tag", nullable = false)
    private String interestTag;

    public AdUnitInterest(Long unitId, String interestTag) {
        this.unitId = unitId;
        this.interestTag = interestTag;
    }
}
