package com.pi.global.initData;

import com.pi.domain.region.region.dto.RegionCreateReqBody;
import com.pi.domain.region.region.entity.Region;
import com.pi.domain.region.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Profile("prod")
@RequiredArgsConstructor
@Configuration
public class ProdRegionInitData {
    private final RegionService regionService;

    @Bean
    ApplicationRunner prodRegionInitDataRunner() {
        return args -> {
            initRegions();
        };
    }

        @Transactional
        public void initRegions() {
            if (regionService.count() == 0) {
                Map<String, List<String>> regionMap = Map.of(
                        "서울특별시", List.of("강남구", "서초구", "송파구", "마포구"),
                        "부산광역시", List.of("해운대구", "수영구", "동래구"),
                        "대구광역시", List.of("중구", "동구", "수성구"),
                        "인천광역시", List.of("남동구", "연수구", "부평구"),
                        "광주광역시", List.of("동구", "서구", "광산구"),
                        "대전광역시", List.of("서구", "유성구", "동구"),
                        "울산광역시", List.of("남구", "동구", "중구"),
                        "세종특별자치시", List.of("세종시"),
                        "제주특별자치도", List.of("제주시", "서귀포시")
                );

                for (Map.Entry<String, List<String>> entry : regionMap.entrySet()) {
                    Region parent = regionService.create(new RegionCreateReqBody(entry.getKey(), null));
                    for (String child : entry.getValue()) {
                        regionService.create(new RegionCreateReqBody(child, parent.getId()));
                    }
                }
            }
        }
    }
