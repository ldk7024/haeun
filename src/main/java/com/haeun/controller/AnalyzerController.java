package com.haeun.controller;

import com.haeun.dto.AnalysisRequest;
import com.haeun.dto.AnalysisResponse;
import com.haeun.service.ErrorAnalyzerService;
import com.haeun.service.RequirementPlannerService;
import com.haeun.service.SqlExplainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analyze")
@RequiredArgsConstructor
@Tag(name = "Developer Tools", description = "개발자를 위한 분석 도구")
public class AnalyzerController {

    private final ErrorAnalyzerService errorAnalyzerService;
    private final SqlExplainerService sqlExplainerService;
    private final RequirementPlannerService requirementPlannerService;

    @PostMapping("/error")
    @Operation(summary = "Error Analyzer", description = "Java/Spring 에러 메시지를 분석해요")
    public ResponseEntity<AnalysisResponse> analyzeError(@RequestBody AnalysisRequest request) {
        return ResponseEntity.ok(errorAnalyzerService.analyze(request));
    }

    @PostMapping("/sql")
    @Operation(summary = "SQL Explainer", description = "SQL 쿼리를 한국어로 단계별 설명해요")
    public ResponseEntity<AnalysisResponse> explainSql(@RequestBody AnalysisRequest request) {
        return ResponseEntity.ok(sqlExplainerService.explain(request));
    }

    @PostMapping("/plan")
    @Operation(summary = "Requirement Planner", description = "요구사항을 개발 계획으로 정리해요")
    public ResponseEntity<AnalysisResponse> planRequirement(@RequestBody AnalysisRequest request) {
        return ResponseEntity.ok(requirementPlannerService.plan(request));
    }
}
