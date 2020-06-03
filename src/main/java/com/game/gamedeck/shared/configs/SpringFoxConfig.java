package com.game.gamedeck.shared.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

    Contact contact = new Contact(
            "Jorge Luis de Oliveira Barbosa",
            "https://www.linkedin.com/in/jorge-barbosa-295a3334/",
            "jorgeluis6@gmail.com"
    );

    List<VendorExtension> vendorExtensions = new ArrayList<>();

    ApiInfo apiInfo = new ApiInfo(
            "Deck of Cards Game",
            "The game API is a very basic game in which one or more decks are added to create a ‘game\n" +
                      "deck’, commonly referred to as a shoe, along with a group of players getting cards from the\n" +
                      "game deck.",
            "1.0",
            "",
            contact,
            "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0",
            vendorExtensions);

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .protocols(new HashSet<>(Arrays.asList("HTTP","HTTPs")))
            .apiInfo(apiInfo)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.game.gamedeck.controller"))
            .paths(PathSelectors.any())
            .build();
    }
}
