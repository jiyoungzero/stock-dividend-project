package org.example.stock.scrapper;

import org.example.stock.model.Company;
import org.example.stock.model.Dividend;
import org.example.stock.model.ScrapedResult;
import org.example.stock.model.constants.Month;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScrapper implements Scrapper{

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history/?frequency=1mo&period1=%d&period2=%d";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s/";

    private static final long START_TIME = 86400;

    @Override
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
            scrapedResult.setDividends(dividendList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return scrapedResult;
    }

    // ticker를 통해 회사명(회사 메타 정보) 긇어오기
    @Override
    public Company scrapCompanyByTicker(String ticker){
        String url = String.format(SUMMARY_URL, ticker);

        try{
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
                    .header("scheme", "https")
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("accept-encoding", "gzip, deflate, br")
                    .header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6")
                    .header("cache-control", "no-cache")
                    .header("pragma", "no-cache")
                    .header("upgrade-insecure-requests", "1")
                    .get();

            String titleEle = document.select("h1.svelte-3a2v0c").text();
            StringBuffer sbTitle = new StringBuffer();
            for(Character c : titleEle.toCharArray()){
                if(c.equals('(')) break;

                sbTitle.append(c);
            }
            String title = sbTitle.toString().trim();

            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
