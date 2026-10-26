package za.co.shieldher.dto;

import za.co.shieldher.model.SupportResource;

public record ResourceResponse(Long id, String title, String description, String category,
                               String phone, String url) {

    public static ResourceResponse from(SupportResource r) {
        return new ResourceResponse(r.getId(), r.getTitle(), r.getDescription(),
                r.getCategory(), r.getPhone(), r.getUrl());
    }
}
