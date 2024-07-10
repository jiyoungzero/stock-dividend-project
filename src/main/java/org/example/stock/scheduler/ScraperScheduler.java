package org.example.stock.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stock.model.Company;
import org.example.stock.model.ScrapedResult;
import org.example.stock.model.constants.CacheKey;
import org.example.stock.persist.CompanyRepository;
import org.example.stock.persist.DividendRepository;
import org.example.stock.persist.entity.CompanyEntity;
import org.example.stock.persist.entity.DividendEntity;
import org.example.stock.scrapper.Scrapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final Scrapper yahooFinanceScrap;

    private final DividendRepository dividendRepository;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling(){
        // 0. 스크래핑 시 로깅 남기기
        log.info("scrapping starts now !");

        // 1. 저장된 회사 목록 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 2. 회사마다의 배당금 정보 새로 스크래핑
        for(var company : companies){
            log.info("now scrapping for.." + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScrap.scrap(new Company(company.getTicker(), company.getName()));

            // 3. 스크래핑한 배당금 결과 중 기존 db에 없는 것만 저장
            scrapedResult.getDividends().stream()
                    .map(e -> new DividendEntity(company.getId(), e))
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists){
                            this.dividendRepository.save(e);
                        }
                    });

            // 4. 연속적으로 스크래핑하는 일 일시정지하기 -> 서버 부하 지양을 위함
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }



    }
}
