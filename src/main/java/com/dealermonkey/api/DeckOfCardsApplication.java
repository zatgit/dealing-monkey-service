package com.dealermonkey.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.dealermonkey.api.docs.ApiDocsConstants.API_DESCRIPTION;
import static com.dealermonkey.api.docs.ApiDocsConstants.GPL3_URL;

@OpenAPIDefinition(info=@Info(
        title="Dealer Monkey Service",
        description = API_DESCRIPTION,
        license = @License(name = "GPL 3", url = GPL3_URL)),
        servers = {
                @Server(url = "${server.url.local}", description = "Local")
        })
@SpringBootApplication(scanBasePackages = {"com.dealermonkey.api"})
public class DeckOfCardsApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeckOfCardsApplication.class, args);
    }
}
