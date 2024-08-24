package com.moodmate.moodmatebe.domain.chat.dto.response.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class ChatPageableDto<T> {
    private int size;
    private int page;
    private int totalPages;
    private long totalElements;

    public ChatPageableDto(int size, int page, Page<T> pageable) {
        this.size = size;
        this.page = page;
        this.totalPages = pageable.getTotalPages();
        this.totalElements = pageable.getTotalElements();
    }
}
