package com.ssafy.triplog.attraction.service;

import com.ssafy.triplog.attraction.dto.AttractionDto;
import com.ssafy.triplog.attraction.dto.AttractionRequest;

public interface AttractionService {
    public Long createAttractionWithImages(AttractionRequest request);
}
