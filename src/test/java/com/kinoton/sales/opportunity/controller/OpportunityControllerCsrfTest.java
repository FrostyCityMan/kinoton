package com.kinoton.sales.opportunity.controller;

import com.kinoton.sales.attachment.service.AttachmentService;
import com.kinoton.sales.customer.service.CustomerService;
import com.kinoton.sales.opportunity.service.OpportunityService;
import com.kinoton.sales.probability.service.ProbabilityStageService;
import com.kinoton.sales.user.service.UserManagementService;
import com.kinoton.sales.year.service.BusinessYearService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OpportunityControllerCsrfTest {

    @Mock
    private OpportunityService opportunityService;

    @Mock
    private AttachmentService attachmentService;

    @Mock
    private ProbabilityStageService probabilityStageService;

    @Mock
    private UserManagementService userManagementService;

    @Mock
    private CustomerService customerService;

    @Mock
    private BusinessYearService businessYearService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        OpportunityController controller = new OpportunityController(
            opportunityService,
            attachmentService,
            probabilityStageService,
            userManagementService,
            customerService,
            businessYearService
        );
        CsrfFilter csrfFilter = new CsrfFilter(new HttpSessionCsrfTokenRepository());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .addFilters(csrfFilter)
            .build();
    }

    @Test
    void updateOpportunityStatusShouldRejectRequestWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/opportunities/41/status")
                .param("status", "HOLD")
                .param("reason", "고객사 예산 재검토"))
            .andExpect(status().isForbidden());

        verifyNoInteractions(opportunityService);
    }
}
