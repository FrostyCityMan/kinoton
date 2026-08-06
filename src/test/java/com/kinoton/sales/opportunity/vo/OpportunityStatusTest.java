package com.kinoton.sales.opportunity.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpportunityStatusTest {

    @Test
    void selectByCodeShouldNormalizeCode() {
        assertThat(OpportunityStatus.selectByCode(" won ")).contains(OpportunityStatus.WON);
    }

    @Test
    void statusMetadataShouldExposeKoreanLabelsAndReasonPolicy() {
        assertThat(OpportunityStatus.IN_PROGRESS.getLabel()).isEqualTo("진행중");
        assertThat(OpportunityStatus.WON.getLabel()).isEqualTo("수주완료");
        assertThat(OpportunityStatus.HOLD.isReasonRequired()).isTrue();
        assertThat(OpportunityStatus.LOST.isReasonRequired()).isTrue();
    }
}
