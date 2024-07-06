package org.example.stock;

import org.example.stock.model.Company;
import org.example.stock.scrapper.Scrapper;
import org.example.stock.scrapper.YahooFinanceScrapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class StockApplication {

	public static void main(String[] args) {
		//SpringApplication.run(StockApplication.class, args);

		Scrapper scrapper = new YahooFinanceScrapper();
//		var result = scrapper.scrapCompanyByTicker("MMM");
//		System.out.println(result);

//		String url = String.format(SUMMARY_URL, ticker);



	}

}
