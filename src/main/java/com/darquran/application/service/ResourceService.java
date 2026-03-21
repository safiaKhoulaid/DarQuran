package com.darquran.application.service;

import com.darquran.application.dto.resources.ResourceRequest;
import com.darquran.application.dto.resources.ResourceResponse;

import java.util.List;

public interface ResourceService {

    ResourceResponse addResource(ResourceRequest request);

    List<ResourceResponse> getResourcesByLesson(String lessonId);

    ResourceResponse getResourceById(String id);

    void deleteResource(String id);
}
