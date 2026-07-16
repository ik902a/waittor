package by.klihal.waittor.common.dto;

import java.util.List;

public record PageResponse<T>(List<T> content,
                              long totalElements,
                              int totalPages,
                              int pageNumber,
                              int pageSize) {
}
