package com.kinoton.sales.opportunity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OpportunityStatusUpdateRequest {

    @NotBlank(message = "변경할 영업 상태를 선택하세요.")
    @Size(max = 30, message = "영업 상태 값이 너무 깁니다.")
    private String status;

    @Size(max = 500, message = "변경 사유는 500자 이하로 입력하세요.")
    private String reason;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
