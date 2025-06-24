package com.example.blood_donation.entity;

import java.io.Serializable;
import java.util.Objects;

public class BloodRequestDetailId  implements Serializable {
    private Long reqID;
    private String bloodType;
    public BloodRequestDetailId() {}
    public BloodRequestDetailId(Long reqID, String bloodType) {
        this.reqID = reqID;
        this.bloodType = bloodType;
    }
    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof BloodRequestDetailId))
            return false;
        BloodRequestDetailId that = (BloodRequestDetailId) o;
        return Objects.equals(reqID, that.reqID) && Objects.equals(bloodType, that.bloodType);
    }
    @Override
    public int hashCode() {
        return Objects.hash(reqID, bloodType);
    }
}
