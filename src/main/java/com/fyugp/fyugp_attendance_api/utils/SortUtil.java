package com.fyugp.fyugp_attendance_api.utils;

import org.springframework.data.domain.Sort;

/**
 * Sort Utils.
 */
public class SortUtil {

    /**
     * Converting string sort to Sort Type for pagination.

     * @param sort sort value
     * @return value to sort type.
     */
    public static Sort sortToSortType(String sort) {
        return sort != null && !sort.isEmpty()
                ? Sort.by(Sort.Order.by(sort.split(",")[0])
                .with(Sort.Direction.fromString(sort.split(",")[1].equalsIgnoreCase("desc") ? "desc" : "asc")))
                : Sort.by(Sort.Order.asc("id"));
    }
}
