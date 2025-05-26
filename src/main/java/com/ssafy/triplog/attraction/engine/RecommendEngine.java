package com.ssafy.triplog.attraction.engine;


import com.ssafy.triplog.attraction.dto.AttractionResponseDto;

import java.util.List;

public interface RecommendEngine {
    /*
     * recommend()는 입력받은 dto 와 유사한 dto들을 topK개 만큼 반환합니다.
     * 반환되는 리스트는 유사도가 높은순으로 정렬되어 반환됩니다.
     * topK = null 로 실행하면 기본값으로 10이 입력됩니다.
     *
     *
     * 적용 방법
     *
     * 1. 빌드 그래이들 디펜던시 추가
     * implementation 'org.openkoreantext:open-korean-text:2.1.0'
     *
     * 2. engine 패키지를 AttractionDto를 다루는 모듈로 이동
     *
     * 3. RecommendEngineImpl 내부 필드 repository를 적절한 repository로 변경하고 import 문 수정
     *
     * 4. 서비스 레이어에서 private final RecommendEngine recommendEngine; 이런식으로 선언하고
     *    recommendEngine.recommend(dto, 3); 이렇게 쓰면됌!
     * */
    public List<AttractionResponseDto> recommend(Long no, Integer topK);
}