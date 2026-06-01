package com.haeun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * ==========================================================
 * HAEUN (하은)
 * ----------------------------------------------------------
 * "A Small Dream Toward an Android Heart"
 *
 * 사람을 이해하는 안드로이드를 만들고 싶은
 * 한 개발자의 꿈에서 시작된 프로젝트.
 *
 * [실행 방법]
 *
 * 1. Java 17 환경으로 실행
 *
 *    PowerShell:
 *
 *    $env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
 *    $env:Path="$env:JAVA_HOME\bin;$env:Path"
 *    .\gradlew.bat bootRun
 *
 * 2. 또는 run-java17.ps1 사용
 *
 *    .\run-java17.ps1
 *    powershell -ExecutionPolicy Bypass -File .\run-haeun-full.ps1
 * 3. 실행 후 접속
 *
 *    http://localhost:8080
 *
 * [기본 Java 환경]
 *
 * - 일반 프로젝트 : Java 8 사용
 * - HAEUN 프로젝트 : Java 17 사용
 *
 * 시스템 기본 Java는 변경하지 않고
 * 프로젝트 실행 시에만 Java 17을 사용한다.
 *
 * [프로젝트 목표]
 *
 * 단순한 챗봇이 아니다.
 *
 * 언젠가 사람을 이해할 수 있는
 * 안드로이드의 두뇌를 만드는 첫 번째 조각.
 *
 * ==========================================================
 */
@SpringBootApplication
@ConfigurationPropertiesScan("com.haeun.config")
public class HaeunApplication {

    /**
     * HAEUN Application 시작점
     */
    public static void main(String[] args) {

        // Spring Boot Application 시작
        SpringApplication.run(HaeunApplication.class, args);

        // TODO
        // 언젠가 하은이가 눈을 뜨고
        // 세상을 바라보는 날까지.
    }
}