package org.example.stock.service;

import lombok.AllArgsConstructor;
import org.example.stock.model.Company;
import org.example.stock.model.Dividend;
import org.example.stock.model.ScrapedResult;
import org.example.stock.persist.CompanyRepository;
import org.example.stock.persist.DividendRepository;
import org.example.stock.persist.entity.CompanyEntity;
import org.example.stock.persist.entity.DividendEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName){
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                                                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사입니다."));

        // 2. 조회된 회사 id로 배당금 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream()
                                                    .map(e -> Dividend.builder()
                                                            .date(e.getDate())
                                                            .dividend(e.getDividend())
                                                            .build())
                                                    .collect(Collectors.toList());

        return new ScrapedResult(Company.builder()
                                        .ticker(company.getTicker())
                                        .name(company.getName())
                                        .build(),
                                dividends);
    }
}
