package com.example.blood_donation.controller;

import com.example.blood_donation.entity.Certificate;
import com.example.blood_donation.service.CertificateService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@SecurityRequirement(name = "api")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;
    @GetMapping
    public List<Certificate> getAll() {
        return certificateService.getAllCertificates();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getById(@PathVariable Long id) {
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }
    @PostMapping("/{id}")
    public ResponseEntity<Certificate> create(@RequestBody Certificate certificate) {
        return new ResponseEntity<>(certificateService.createCertificate(certificate), HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Certificate> update(@PathVariable Long id, @RequestBody Certificate certificate) {
        return ResponseEntity.ok(certificateService.updateCertificate(id, certificate));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        certificateService.deleteCertificate(id);
        return ResponseEntity.noContent().build();
    }
}
