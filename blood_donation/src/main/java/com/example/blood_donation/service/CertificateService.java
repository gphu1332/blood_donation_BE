package com.example.blood_donation.service;

import com.example.blood_donation.entity.Certificate;
import com.example.blood_donation.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CertificateService {
    @Autowired
    private CertificateRepository certificateRepository;
    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }
    public Certificate getCertificateById(Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chứng chỉ với id: "+ id));
    }
    public Certificate createCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }
    public Certificate updateCertificate(Long id, Certificate certificateDetails) {
        Certificate cert = getCertificateById(id);
        cert.setIssueDate(certificateDetails.getIssueDate());
        cert.setDonation(certificateDetails.getDonation());
        cert.setMember(certificateDetails.getMember());
        cert.setAdmin(certificateDetails.getAdmin());
        return certificateRepository.save(cert);
    }
    public void deleteCertificate(Long id) {
        Certificate cert = getCertificateById(id);
        certificateRepository.delete(cert);
    }

}
