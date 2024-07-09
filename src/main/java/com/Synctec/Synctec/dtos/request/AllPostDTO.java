package com.Synctec.Synctec.dtos.request;

import lombok.Data;

@Data
public class AllPostDTO {
    private int page = 0; // Default page number
    private int size = 10; // Default page size
    private String sortBy = "createdDate"; // Default sorting field

    private String sortDirection = "DESC"; // Default sorting direction
}
