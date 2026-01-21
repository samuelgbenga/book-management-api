package com.bookmanagement.utils;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long total
) {}
