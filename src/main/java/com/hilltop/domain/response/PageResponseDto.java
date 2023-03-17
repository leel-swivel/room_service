package com.hilltop.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

/**
 * Base class for paging dto
 */
@Setter
@Getter
@AllArgsConstructor
public class PageResponseDto extends ResponseDto {

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private final long totalItems;
    private final int totalPages;
    private final int page;
    private final int size;

    public PageResponseDto(Page<?> page) {
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.page = page.getNumber();
        this.size = page.getSize();
    }

    public PageResponseDto(int totalItems, int page, int size) {
        this.totalItems = totalItems;
        this.totalPages = calculateTotalPages(totalItems, size);
        this.page = page;
        this.size = size;
    }

    /**
     * This method is used to calculate total pages for pagination
     *
     * @param totalItems totalItems
     * @param size       size
     * @return number of pages.
     */
    private int calculateTotalPages(int totalItems, int size) {
        if (totalItems == ZERO)
            return ZERO;
        if (totalItems <= size)
            return ONE;
        if (totalItems % size == ZERO)
            return totalItems / size;
        return totalItems / size + ONE;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
