package org.example.stock.scrapper;

import org.example.stock.model.Company;
import org.example.stock.model.Dividend;
import org.example.stock.model.ScrapedResult;
import org.example.stock.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class YahooFinanceScrapper {

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history/?frequency=1mo&period1=%d&period2=%d";
    private static final long START_TIME = 86400;


    public ScrapedResult scrap(Company company) {
        var scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);

        try {
            long now = System.currentTimeMillis()/1000;

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection
                    .userAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
                    .header("scheme", "https")
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("accept-encoding", "gzip, deflate, br")
                    .header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6")
                    .header("cache-control", "no-cache")
                    .header("pragma", "no-cache")
                    .header("upgrade-insecure-requests", "1")
                    .get();

            Elements parsingDiv = document.select("table.table.svelte-ewueuo");
            Element table = parsingDiv.get(0);

            Element tbody = table.children().get(1);

            List<Dividend> dividendList = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (txt.endsWith("Dividend")) {
                    String[] splits = txt.split(" ");
                    int month = Month.strToNumber(splits[0]);
                    int day = Integer.valueOf(splits[1].replace(",", ""));
                    int year = Integer.valueOf(splits[2]);
                    String dividend = splits[3];


                    if(month < 0){
                        throw new RuntimeException("UnExpected Month enum value ->" + splits[0]);
                    }

                    dividendList.add(Dividend.builder()
                                            .date(LocalDateTime.of(year, month, day, 0, 0))
                                            .dividend(dividend)
                                            .build());


//                    System.out.println(year + " / " + month + " /" + day + " -> " + dividend);
                }

            }
            scrapedResult.setDividendList(dividendList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return scrapedResult;
    }


}
