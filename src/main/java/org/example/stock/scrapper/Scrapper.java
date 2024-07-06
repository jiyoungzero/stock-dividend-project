package org.example.stock.scrapper;

import org.example.stock.model.Company;
import org.example.stock.model.ScrapedResult;

public interface Scrapper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
