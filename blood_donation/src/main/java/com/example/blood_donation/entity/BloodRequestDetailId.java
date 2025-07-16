package com.example.blood_donation.entity;

import com.example.blood_donation.enums.TypeBlood;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
@Embeddable
@Data
public class BloodRequestDetailId  implements Serializable {
    private Long reqID;
    private TypeBlood typeBlood;

    public BloodRequestDetailId() {}

    public BloodRequestDetailId(Long reqID, TypeBlood typeBlood) {
        this.reqID = reqID;
        this.typeBlood = typeBlood;
    }
    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof BloodRequestDetailId))
            return false;
        BloodRequestDetailId that = (BloodRequestDetailId) o;
        return Objects.equals(reqID, that.reqID) && Objects.equals(typeBlood, that.typeBlood);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reqID, typeBlood);
    }
}
