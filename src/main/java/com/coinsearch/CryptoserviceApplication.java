package com.coinsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication// во время запуска оно автоконфигурирует как спринг
public class CryptoserviceApplication {

	public static void main(String[] args) {//могу использовать в других классах не создавая обьекты этогоо класса
		SpringApplication.run(CryptoserviceApplication.class, args);// статический метод ран запускаем класс
	}

}

