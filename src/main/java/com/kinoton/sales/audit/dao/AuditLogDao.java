package com.kinoton.sales.audit.dao;

import com.kinoton.sales.audit.dto.AuditLogCreateCommandDto;
import com.kinoton.sales.audit.dto.AuditLogListItemDto;
import com.kinoton.sales.audit.dto.AuditLogSearchCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuditLogDao {

    void insertAuditLog(AuditLogCreateCommandDto command);

    List<AuditLogListItemDto> selectAuditLogList(AuditLogSearchCondition condition);
}
