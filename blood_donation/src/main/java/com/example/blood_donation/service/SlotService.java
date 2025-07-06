package com.example.blood_donation.service;

import com.example.blood_donation.dto.SlotRequest;
import com.example.blood_donation.dto.SlotResponse;
import com.example.blood_donation.entity.Slot;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    public SlotResponse getSlotById(Long id) {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Slot not found with id: " + id));

        SlotResponse response = new SlotResponse();
        response.setSlotID(slot.getSlotID());
        response.setLabel(slot.getLabel());
        response.setStart(slot.getStart());
        response.setEnd(slot.getEnd());
        return response;
    }


    public SlotResponse create(SlotRequest request) {
        // Validate request
        if (request.getLabel() == null || request.getLabel().isBlank()) {
            throw new BadRequestException("Label must not be empty");
        }
        if (request.getStart() == null || request.getEnd() == null) {
            throw new BadRequestException("Start and end times must not be null");
        }
        if (request.getStart().isAfter(request.getEnd())) {
            throw new BadRequestException("Start time must be before end time");
        }

        // Map request -> entity
        Slot slot = new Slot();
        slot.setLabel(request.getLabel());
        slot.setStart(request.getStart());
        slot.setEnd(request.getEnd());
        slot.setProgram(null); // Slot chưa gán Program, sau này khi gán Program sẽ set

        // Save
        Slot saved = slotRepository.save(slot);

        // Map entity -> response
        return mapToResponse(saved);
    }

    private SlotResponse mapToResponse(Slot slot) {
        SlotResponse response = new SlotResponse();
        response.setSlotID(slot.getSlotID());
        response.setLabel(slot.getLabel());
        response.setStart(slot.getStart());
        response.setEnd(slot.getEnd());
        return response;
    }
}
