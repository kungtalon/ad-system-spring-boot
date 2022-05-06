package org.talon.ad.datamodel.unitcondition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talon.ad.datamodel.AdEntity;

import javax.persistence.*;

/**
 * Created by Zelong
 * On 2022/5/2
 * Restrictions on ad_unit about districts
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ad_unit_district")
public class AdUnitDistrict extends AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "unit_id", nullable = false)
    private Long unitId;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "city", nullable = false)
    private String city;

    public AdUnitDistrict(Long unitId, String country, String state, String city) {
        this.unitId = unitId;
        this.country = country;
        this.state = state;
        this.city = city;
    }
}
