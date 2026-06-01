package com.haeun.service;

import com.haeun.dto.AnalysisRequest;
import com.haeun.dto.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequirementPlannerService {

    private final AIClientService aiClientService;

    public AnalysisResponse plan(AnalysisRequest request) {
        String result = aiClientService.planRequirements(request.getContent());
        return new AnalysisResponse(
            "requirement",
            result,
            "좋은 계획이 좋은 코드를 만들어요. 같이 잘 만들어봐요! 📋"
        );
    }
}
