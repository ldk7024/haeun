package com.haeun.service;

import com.haeun.dto.AnalysisRequest;
import com.haeun.dto.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorAnalyzerService {

    private final AIClientService aiClientService;

    public AnalysisResponse analyze(AnalysisRequest request) {
        String result = aiClientService.analyzeError(request.getContent());
        return new AnalysisResponse(
            "error",
            result,
            "에러가 있어도 괜찮아요. 에러 없는 코드는 없으니까요. 같이 해결해봐요! 💪"
        );
    }
}
