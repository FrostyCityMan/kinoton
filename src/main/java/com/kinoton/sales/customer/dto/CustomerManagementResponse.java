package com.kinoton.sales.customer.dto;

import java.util.List;

public record CustomerManagementResponse(
    List<CustomerListItemDto> customers
) {
}
