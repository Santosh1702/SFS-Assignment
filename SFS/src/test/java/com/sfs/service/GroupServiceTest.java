package com.sfs.service;

import com.sfs.entities.Groups;
import com.sfs.entities.Device;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupServiceTest extends AbstractServiceTest {

    private GroupService groupService;

    @BeforeEach
    void setUp() {
        // Initialize the service under test, injecting the mocks
        groupService = new GroupService(groupRepository, plantService, deviceService);
    }

    // --- Test 1: Successful Assignment to Same-Plant Device ---
    @Test
    void assignToDevices_success_samePlant() {
        // GIVEN
        Long groupId = 1L;
        Long deviceId = 101L;
        String plantKey = "PLANT_A";

        Groups groupA = createGroup(groupId, "Team A", plantKey);
        Device deviceA = createDevice(deviceId, "CNC", 1, plantKey);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupA));
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(deviceA));
        when(groupRepository.save(any(Groups.class))).thenReturn(groupA);

        Set<Long> deviceIds = Set.of(deviceId);

        // WHEN
        Groups result = groupService.assignToDevices(groupId, deviceIds);

        // THEN
        assertTrue(result.getDevices().contains(deviceA));
        verify(groupRepository, times(1)).save(groupA);
    }

    // --- Test 2: Validation Failure - Cross-Plant Assignment ---
    @Test
    void assignToDevices_failure_crossPlant() {
        // GIVEN
        Long groupId = 2L;
        Long deviceId = 201L;
        String groupPlantKey = "PLANT_A";
        String devicePlantKey = "PLANT_B"; // DIFFERENT PLANT

        Groups groupA = createGroup(groupId, "Team A", groupPlantKey);
        Device deviceB = createDevice(deviceId, "Laser", 1, devicePlantKey);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupA));
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(deviceB));

        Set<Long> deviceIds = Set.of(deviceId);

        // WHEN / THEN
        // Expect an IllegalArgumentException due to plant mismatch
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            groupService.assignToDevices(groupId, deviceIds);
        });

        assertTrue(thrown.getMessage().contains("They must match"));
        verify(groupRepository, never()).save(any());
    }
    
    // --- Test 3: Validation Failure - Invalid Plant Key on Group Creation ---
    @Test
    void saveGroup_failure_invalidPlantKey() {
        // GIVEN
        Groups newGroup = createGroup(null, "New Team", "INVALID_PLANT");
        
        when(plantService.findByPlantKey("INVALID_PLANT")).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(IllegalArgumentException.class, () -> {
            groupService.save(newGroup);
        });
        verify(groupRepository, never()).save(any());
    }
}