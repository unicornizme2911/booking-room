package com.booking.services;

import com.booking.dto.request.FeatureRequest;
import com.booking.dto.response.FeatureResponse;
import com.booking.models.FeatureModel;
import com.booking.repository.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureService {

    @Autowired
    private FeatureRepository featureRepository;

    public FeatureResponse toResponse(FeatureModel feature) {
        return FeatureResponse.builder()
                .id(feature.getId())
                .name(feature.getName())
                .icon(feature.getIcon())
                .build();
    }

    public List<FeatureResponse> toResponse(List<FeatureModel> features) {
        return features.stream().map(this::toResponse).toList();
    }

    public FeatureModel toEntity(FeatureRequest feature) {
        return FeatureModel.builder()
                .id(feature.getId())
                .name(feature.getName())
                .icon(feature.getIcon())
                .build();
    }

    public FeatureResponse add(FeatureRequest request) {
        var feature = toEntity(request);
        featureRepository.save(feature);
        return toResponse(feature);
    }
}
