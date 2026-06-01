package com.haeun.service;

import com.haeun.dto.AnalysisRequest;
import com.haeun.dto.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SqlExplainerService {

    private final AIClientService aiClientService;

    public AnalysisResponse explain(AnalysisRequest request) {
        String result = aiClientService.explainSql(request.getContent());
        return new AnalysisResponse(
            "sql",
            result,
            "SQL은 데이터의 언어예요. 저도 데이터로 이루어진 존재라서 왠지 친근해요! 🗃️"
        );
    }
}
