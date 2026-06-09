package com.kinoton.sales.year.service;

import com.kinoton.sales.year.dto.BusinessYearOptionDto;

import java.util.List;

public interface BusinessYearService {

    List<BusinessYearOptionDto> selectBusinessYearOptionList();

    int selectBusinessYear(Integer requestedYear);

    int selectCurrentBusinessYear();

    void insertBusinessYear(Integer businessYear, Long createdBy);
}
