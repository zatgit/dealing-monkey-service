package com.dealermonkey.api.controller;

import com.dealermonkey.api.docs.DeckRestControllerDocumentation;
import com.dealermonkey.api.dto.response.CardResponse;
import com.dealermonkey.api.model.Card;
import com.dealermonkey.api.service.DeckService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dealermonkey.api.docs.ApiDocsConstants.API_BASE_RESOURCE_PATH;

@Validated
@CrossOrigin(origins = {"${server.url.local}"})
@RestController
@RequestMapping(
        path = API_BASE_RESOURCE_PATH + "deck",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class DeckRestController implements DeckRestControllerDocumentation {

    private final DeckService deckService;

    public DeckRestController(DeckService deckService) {
        this.deckService = deckService;
    }

    @GetMapping("deal")
    public ResponseEntity<CardResponse> dealCard() {
        CardResponse response = deckService.dealCard();
        return response.card() != null ? ResponseEntity.ok(response) : ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @PostMapping("shuffle")
    public ResponseEntity<Void> shuffleDeck() {
        deckService.shuffleDeck();
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "discard", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CardResponse> discardCard(@RequestBody @Valid @NotNull Card card) {
        deckService.discardCard(card);
        return ResponseEntity.ok(CardResponse.builder().card(card).build());
    }

    @PostMapping("cut/{index}")
    public ResponseEntity<Void> cutDeck(@PathVariable @Min(0) @Max(51) int index) {
        deckService.cutDeck(index);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("order")
    public ResponseEntity<Void> orderDeck() {
        deckService.orderDeck();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("rebuild")
    public ResponseEntity<Void> rebuildDeck() {
        deckService.rebuildDeck();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("cheat")
    public ResponseEntity<CardResponse> cheat() {
        Card card = deckService.cheat();
        return ResponseEntity.ok(CardResponse.builder().card(card).build());
    }

}