package com.sfs.controllers;

import com.sfs.entities.Groups;
import com.sfs.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/groups")
public class GroupsController {

    private final GroupService groupService;

    public GroupsController(GroupService groupService) {
        this.groupService = groupService;
    }


    @PostMapping
    public ResponseEntity<Groups> createGroup(@Valid @RequestBody Groups groups) {
        Groups savedGroups = groupService.save(groups);
        return new ResponseEntity<>(savedGroups, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Groups> getGroupById(@PathVariable Long id) {
        return groupService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{groupId}/devices")
    public ResponseEntity<Groups> assignDevices(@PathVariable Long groupId,
                                                @RequestBody Map<String, Set<Long>> payload) {
        Set<Long> deviceIds = payload.getOrDefault("deviceIds", Collections.emptySet());
        Groups updatedGroup = groupService.assignToDevices(groupId, deviceIds);
        return ResponseEntity.ok(updatedGroup);
    }
}