package com.capstoneproject.smartattendance.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.capstoneproject.smartattendance.dto.FaceMatchResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaceMatchService {

    private final WebClient webClient;

    @Value("${face-match-api.uri}")
    private String faceMatchApi;


    public boolean matchFaces(byte[] dbImage, byte[] liveImage) {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("image1", new ByteArrayResource(dbImage) {
            @Override
            public String getFilename() {
                return "db.jpg";
            }
        });

        body.add("image2", new ByteArrayResource(liveImage) {
            @Override
            public String getFilename() {
                return "live.jpg";
            }
        });

        try {
            FaceMatchResponse response = webClient.post()
                    .uri(faceMatchApi)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(FaceMatchResponse.class)
                    .block();

            return response != null && response.isSame_person();

        } catch (WebClientResponseException.Unauthorized ex) {
            log.error("Face-match API unauthorized (invalid API key)");
            return false;

        } catch (WebClientResponseException ex) {
            log.error("Face-match API error: {}", ex.getResponseBodyAsString());
            return false;

        } catch (Exception ex) {
            log.error("Face-match service failed", ex);
            return false;
        }
    }
}
