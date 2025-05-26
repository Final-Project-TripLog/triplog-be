package com.ssafy.triplog.attraction.engine;

import com.ssafy.triplog.attraction.dto.AttractionResponseDto;
import com.ssafy.triplog.attraction.mapper.AttractionMapper;
import lombok.RequiredArgsConstructor;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer.KoreanToken;
import org.springframework.stereotype.Component;
import scala.collection.Seq;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendEngineImpl implements RecommendEngine {

    private final AttractionMapper attractionMapper;

    @Override
    public List<AttractionResponseDto> recommend(Long no, Integer topK) {
        if (topK == null) {
            topK = 20;
        }

        List<String> tokens = tokenize(attractionMapper.findById(no));
        Map<String, Double> idf = getIDF();
        return attractionMapper.findAttractions(null, null, "sortBy", 100, 0)
                .stream()
                .sorted(Comparator.comparingDouble(
                        item -> cosineSimilarity(
                                getTFIDF(tokens, idf),
                                getTFIDF(tokenize((AttractionResponseDto) item), idf)
                        )
                ).reversed())
                .skip(1)
                .limit(topK)
                .toList();
    }

    private String dto2Str(AttractionResponseDto dto) {
        StringBuilder sb = new StringBuilder();
        return sb.append(dto.getTitle()).append(" ")
                .append(dto.getOverview()).append(" ")
                .append(dto.getAddress()).append(" ")
                .append(dto.getAttractionTypeName())
                .toString();
    }

    // 텍스트 토크나이징
    private List<String> tokenize(AttractionResponseDto item) {
        String text = dto2Str(item).replaceAll("[\\[\\],=().]", "");
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text); // 정규화
        Seq<KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized); // 토크나이징
        return OpenKoreanTextProcessorJava.tokensToJavaStringList(tokens); // 문자열 리스트로 추출
    }

    // 각 단어 빈도 계산
    private Map<String, Integer> termFreq(List<String> tokens) {
        Map<String, Integer> freqMap = new HashMap<>();
        for (String token : tokens) {
            freqMap.put(token, freqMap.getOrDefault(token, 0) + 1);
        }
        return freqMap;
    }

    // IDF 계산
    private Map<String, Double> getIDF() {
        List<AttractionResponseDto> items = attractionMapper.findAttractions(null, null, "sortBy", 100, 0);
        List<List<String>> docs = items.stream().map(this::tokenize).toList();
        Map<String, Double> idfMap = new HashMap<>();
        int docCount = docs.size();

        Set<String> allTerms = docs.stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        for (String term : allTerms) {
            int containingDocs = 0;
            for (List<String> doc : docs) {
                if (doc.contains(term)) containingDocs++;
            }
            double idf = Math.log((double) docCount / (1 + containingDocs)); // 1 smoothing
            idfMap.put(term, idf);
        }
        return idfMap;
    }

    // TF-IDF 벡터 생성
    private Map<String, Double> getTFIDF(List<String> tokens, Map<String, Double> idfMap) {
        Map<String, Integer> tfMap = termFreq(tokens);
        Map<String, Double> tfidfMap = new HashMap<>();

        for (String term : idfMap.keySet()) {
            double tf = tfMap.getOrDefault(term, 0);
            double idf = idfMap.get(term);
            tfidfMap.put(term, tf * idf);
        }
        return tfidfMap;
    }

    // 코사인 유사도 계산
    private double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (String key : vec1.keySet()) {
            double v1 = vec1.getOrDefault(key, 0.0);
            double v2 = vec2.getOrDefault(key, 0.0);
            dot += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }
        return norm1 == 0 || norm2 == 0 ? 0.0 : dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}