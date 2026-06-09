package com.kinoton.sales.year.dao;

import com.kinoton.sales.year.dto.BusinessYearOptionDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BusinessYearDao {

    List<BusinessYearOptionDto> selectBusinessYearOptionList();

    Integer selectBusinessYearDetails(Integer businessYear);

    void insertBusinessYear(Integer businessYear);
}
