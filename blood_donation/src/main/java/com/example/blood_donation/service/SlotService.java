package com.example.blood_donation.service;

import com.example.blood_donation.dto.SlotRequest;
import com.example.blood_donation.dto.SlotResponse;
import com.example.blood_donation.entity.Slot;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repository.SlotRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Lấy slot theo ID.
     */
    public SlotResponse getSlotById(Long id) {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Slot not found with id: " + id));

        return mapToResponse(slot);
    }

    /**
     * Tạo mới slot không gắn chương trình.
     */
    public SlotResponse create(SlotRequest request) {
        validateSlotRequest(request);

        Slot slot = new Slot();
        slot.setLabel(request.getLabel());
        slot.setStart(request.getStart());
        slot.setEnd(request.getEnd());

        // Chưa gán với chương trình nào, sẽ được gán sau
        Slot saved = slotRepository.save(slot);
        return mapToResponse(saved);
    }

    /**
     * Trả về danh sách tất cả slot.
     */
    public List<SlotResponse> getAll() {
        return slotRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Ánh xạ từ entity sang response DTO.
     */
    private SlotResponse mapToResponse(Slot slot) {
        SlotResponse response = new SlotResponse();
        response.setSlotID(slot.getSlotID());
        response.setLabel(slot.getLabel());
        response.setStart(slot.getStart());
        response.setEnd(slot.getEnd());

        // Optional: bạn có thể trả về danh sách programId nếu cần
        // response.setProgramIds(slot.getPrograms().stream().map(p -> p.getId()).toList());

        return response;
    }

    /**
     * Validate logic đầu vào cho tạo mới.
     */
    private void validateSlotRequest(SlotRequest request) {
        if (request.getLabel() == null || request.getLabel().isBlank()) {
            throw new BadRequestException("Label must not be empty");
        }
        if (request.getStart() == null || request.getEnd() == null) {
            throw new BadRequestException("Start and end times must not be null");
        }
        if (request.getStart().isAfter(request.getEnd())) {
            throw new BadRequestException("Start time must be before end time");
        }
    }
}
