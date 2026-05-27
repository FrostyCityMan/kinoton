package com.kinoton.sales.attachment.dao;

import com.kinoton.sales.attachment.dto.AttachmentCreateCommandDto;
import com.kinoton.sales.attachment.dto.AttachmentDetailsDto;
import com.kinoton.sales.attachment.dto.AttachmentItemDto;
import com.kinoton.sales.attachment.dto.AttachmentLookupCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttachmentDao {

    List<AttachmentItemDto> selectAttachmentListByOpportunityId(Long opportunityId);

    AttachmentDetailsDto selectAttachmentDetails(AttachmentLookupCondition condition);

    void insertAttachment(AttachmentCreateCommandDto command);

    void deleteAttachment(AttachmentLookupCondition condition);
}
