package com.dealermonkey.api.docs;

import com.dealermonkey.api.dto.response.CardResponse;
import com.dealermonkey.api.model.Card;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Deck", description = "Deck management APIs including dealing and discarding cards")
public interface DeckRestControllerDocumentation {

    @GetMapping("deal")
    @Operation(
            summary = "Deal a card",
            description = "Returns the top card from the deck if available.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card dealt successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CardResponse.class),
                                    examples = @ExampleObject(name = "Deal Success", value = """
                                    {
                                      "data": {
                                        "card": {
                                          "suit": "spades",
                                          "rank": "two"
                                        }
                                      }
                                    }
                                """))
                    ),
                    @ApiResponse(responseCode = "404", description = "No card left to deal",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "Deck Empty", value = """
                                    {
                                      "timestamp": "2025-05-22T01:20:00.000Z",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "No cards left in the deck",
                                      "path": "/api/v1/deck/deal"
                                    }
                                """)))
            }
    )
    ResponseEntity<CardResponse> dealCard();

    @PostMapping("discard")
    @Operation(
            summary = "Discard a card",
            description = "Adds a specified card to the discard pile.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Card to be discarded",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Card.class),
                            examples = {
                                    @ExampleObject(name = "Discard Request", value = """
                                    {
                                      "suit": "spades",
                                      "rank": "ace"
                                    }
                                """)
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card discarded successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CardResponse.class),
                                    examples = @ExampleObject(name = "Discard Success", value = """
                                    {
                                      "data": {
                                        "card": {
                                          "suit": "spades",
                                          "rank": "ace"
                                        }
                                      }
                                    }
                                """)
                            )),
                    @ApiResponse(responseCode = "400", description = "Invalid card data",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Card not dealt",
                                                    value = """
                                                    {
                                                      "timestamp": "2025-05-22T01:35:41.649Z",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Cannot discard card that was not dealt: Card[suit=SPADES, rank=ACE]"
                                                    }
                                                """),
                                            @ExampleObject(
                                                    name = "Card already discarded",
                                                    value = """
                                                    {
                                                      "timestamp": "2025-05-22T01:44:10.134Z",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Card has already been discarded: Card[suit=SPADES, rank=TWO]"
                                                    }
                                                """)
                                    }
                            ))
            }
    )
    ResponseEntity<CardResponse> discardCard(@Valid @org.springframework.web.bind.annotation.RequestBody Card card);

    @PostMapping("shuffle")
    @Operation(
            summary = "Shuffle the deck",
            description = "Shuffles the current deck.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deck shuffled successfully"),
                    @ApiResponse(responseCode = "404", description = "Cannot shuffle an empty deck",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "Empty Deck", value = """
                                            {
                                              "timestamp": "2025-05-22T01:50:00.000Z",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "No cards left in the deck",
                                              "path": "/api/v1/deck/shuffle"
                                            }
                                            """)))
            }
    )
    ResponseEntity<Void> shuffleDeck();

    @PostMapping("cut/{index}")
    @Operation(
            summary = "Cut the deck",
            description = "Cuts the deck at the specified index.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deck cut successfully"),
                    @ApiResponse(responseCode = "404", description = "Cannot cut an empty deck",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "Empty Deck", value = """
                                            {
                                              "timestamp": "2025-05-22T01:52:00.000Z",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "No cards left in the deck",
                                              "path": "/api/v1/deck/cut/10"
                                            }
                                            """)))
            }
    )
    ResponseEntity<Void> cutDeck(int index);

    @PostMapping("order")
    @Operation(
            summary = "Order the deck",
            description = "Sorts the deck in its default order.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deck ordered successfully"),
                    @ApiResponse(responseCode = "404", description = "Cannot order an empty deck",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "Empty Deck", value = """
                                            {
                                              "timestamp": "2025-05-22T01:53:00.000Z",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "No cards left in the deck",
                                              "path": "/api/v1/deck/order"
                                            }
                                            """)))
            }
    )
    ResponseEntity<Void> orderDeck();

    @PostMapping("rebuild")
    @Operation(
            summary = "Rebuild the deck",
            description = "Rebuilds and resets the deck to 52 cards in sorted order.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deck rebuilt successfully")
            }
    )
    ResponseEntity<Void> rebuildDeck();

    @GetMapping("cheat")
    @Operation(
            summary = "Peek at the top card",
            description = "Returns the top card from the deck without removing it.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Top card peeked successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CardResponse.class),
                                    examples = @ExampleObject(name = "Cheat Success", value = """
                                    {
                                      "data": {
                                        "card": {
                                          "suit": "clubs",
                                          "rank": "queen"
                                        }
                                      }
                                    }
                                """))),
                    @ApiResponse(responseCode = "404", description = "Cannot cheat an empty deck",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "Empty Deck", value = """
                                    {
                                      "timestamp": "2025-05-22T01:54:00.000Z",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "No cards left in the deck",
                                      "path": "/api/v1/deck/cheat"
                                    }
                                """)))
            }
    )
    ResponseEntity<CardResponse> cheat();

}