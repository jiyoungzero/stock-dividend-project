package org.example.stock.service;


import lombok.AllArgsConstructor;
import org.example.stock.model.Company;
import org.example.stock.model.ScrapedResult;
import org.example.stock.persist.CompanyRepository;
import org.example.stock.persist.DividendRepository;
import org.example.stock.persist.entity.CompanyEntity;
import org.example.stock.persist.entity.DividendEntity;
import org.example.stock.scrapper.Scrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.apache.commons.collections4.Trie;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scrapper yahooFinanceScrapper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker){
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if(exists){
            throw new RuntimeException("already exists ticker ->" + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable){
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker){
        // ticker를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScrapper.scrapCompanyByTicker(ticker);
        if(ObjectUtils.isEmpty(company)){
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScrapper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity  = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()
                                                                            .map(e -> new DividendEntity(companyEntity.getId(), e))
                                                                            .collect(Collectors.toList());


        this.dividendRepository.saveAll(dividendEntityList);
        return company;
    }
}
