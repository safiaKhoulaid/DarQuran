package com.darquran.application.service;

import com.darquran.application.dto.room.RoomRequest;
import com.darquran.application.dto.room.RoomResponse;

import java.util.List;

public interface RoomService {

    RoomResponse create(RoomRequest request);

    RoomResponse getById(String id);

    List<RoomResponse> getAll();

    RoomResponse update(String id, RoomRequest request);

    void delete(String id);
}

