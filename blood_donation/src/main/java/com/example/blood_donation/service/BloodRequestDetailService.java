package com.example.blood_donation.service;

import com.example.blood_donation.entity.BloodRequestDetail;
import com.example.blood_donation.entity.BloodRequestDetailId;
import com.example.blood_donation.repositoty.BloodRequestDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloodRequestDetailService {
    @Autowired
    private BloodRequestDetailRepository detailRepository;
    public List<BloodRequestDetail> getAll() {
        return detailRepository.findAll();
    }
    public BloodRequestDetail getById(Long reqID, String bloodType) {
        BloodRequestDetailId id = new BloodRequestDetailId(reqID, bloodType);
        return detailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin chi tiết của đơn yêu cầu cần máu"));
    }
    public BloodRequestDetail create(BloodRequestDetail detail) {
        return detailRepository.save(detail);
    }
    public BloodRequestDetail update(Long reqID, String bloodType, BloodRequestDetail updated) {
        BloodRequestDetail existing = getById(reqID, bloodType);
        existing.setPackCount(updated.getPackCount());
        existing.setPackVolume(updated.getPackVolume());
        return detailRepository.save(existing);
    }
    public void delete(Long reqID, String bloodType) {
        detailRepository.deleteById(new BloodRequestDetailId(reqID, bloodType));
    }
}
