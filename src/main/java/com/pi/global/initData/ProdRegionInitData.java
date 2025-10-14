package com.pi.global.initData;

import com.pi.domain.region.region.dto.RegionCreateReqBody;
import com.pi.domain.region.region.entity.Region;
import com.pi.domain.region.region.repository.RegionRepository;
import com.pi.domain.region.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

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

                Region seoul = regionService.createRegion(new RegionCreateReqBody("서울특별시", null));
                regionService.createRegion(new RegionCreateReqBody("강남구", seoul.getId()));
                regionService.createRegion(new RegionCreateReqBody("서초구", seoul.getId()));
                regionService.createRegion(new RegionCreateReqBody("송파구", seoul.getId()));
                regionService.createRegion(new RegionCreateReqBody("마포구", seoul.getId()));

                Region busan = regionService.createRegion(new RegionCreateReqBody("부산광역시", null));
                regionService.createRegion(new RegionCreateReqBody("해운대구", busan.getId()));
                regionService.createRegion(new RegionCreateReqBody("수영구", busan.getId()));
                regionService.createRegion(new RegionCreateReqBody("동래구", busan.getId()));

                Region daegu = regionService.createRegion(new RegionCreateReqBody("대구광역시", null));
                regionService.createRegion(new RegionCreateReqBody("중구", daegu.getId()));
                regionService.createRegion(new RegionCreateReqBody("동구", daegu.getId()));
                regionService.createRegion(new RegionCreateReqBody("수성구", daegu.getId()));

                Region incheon = regionService.createRegion(new RegionCreateReqBody("인천광역시", null));
                regionService.createRegion(new RegionCreateReqBody("남동구", incheon.getId()));
                regionService.createRegion(new RegionCreateReqBody("연수구", incheon.getId()));
                regionService.createRegion(new RegionCreateReqBody("부평구", incheon.getId()));

                Region gwangju = regionService.createRegion(new RegionCreateReqBody("광주광역시", null));
                regionService.createRegion(new RegionCreateReqBody("동구", gwangju.getId()));
                regionService.createRegion(new RegionCreateReqBody("서구", gwangju.getId()));
                regionService.createRegion(new RegionCreateReqBody("광산구", gwangju.getId()));

                Region daejeon = regionService.createRegion(new RegionCreateReqBody("대전광역시", null));
                regionService.createRegion(new RegionCreateReqBody("서구", daejeon.getId()));
                regionService.createRegion(new RegionCreateReqBody("유성구", daejeon.getId()));
                regionService.createRegion(new RegionCreateReqBody("동구", daejeon.getId()));

                Region ulsan = regionService.createRegion(new RegionCreateReqBody("울산광역시", null));
                regionService.createRegion(new RegionCreateReqBody("남구", ulsan.getId()));
                regionService.createRegion(new RegionCreateReqBody("동구", ulsan.getId()));
                regionService.createRegion(new RegionCreateReqBody("중구", ulsan.getId()));

                Region sejong = regionService.createRegion(new RegionCreateReqBody("세종특별자치시", null));
                regionService.createRegion(new RegionCreateReqBody("세종시", sejong.getId()));

                Region jeju = regionService.createRegion(new RegionCreateReqBody("제주특별자치도", null));
                regionService.createRegion(new RegionCreateReqBody("제주시", jeju.getId()));
                regionService.createRegion(new RegionCreateReqBody("서귀포시", jeju.getId()));
            }
        }
    }
}
