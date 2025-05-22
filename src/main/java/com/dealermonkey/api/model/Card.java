package com.dealermonkey.api.model;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Represents a single playing card with suit and rank.")
public record Card(
        @Schema(description = "The suit of the card", example = "spades") Suit suit,
        @Schema(description = "The rank of the card", example = "ace") Rank rank
) {
    @NotNull
    @Schema(
            description = "The suit of the card",
            example = "spades",
            allowableValues = { "spades", "hearts", "clubs", "diamonds" }
    )
    public enum Suit {
        SPADES, HEARTS, CLUBS, DIAMONDS;

        @JsonValue
        public String toLowerCase() {
            return name().toLowerCase();
        }
    }

    @NotNull
    @Schema(
            description = "The rank of the card",
            example = "ace",
            allowableValues = {
                    "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
                    "jack", "queen", "king", "ace"
            }
    )
    public enum Rank {
        TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN,
        JACK, QUEEN, KING, ACE;

        @JsonValue
        public String toLowerCase() {
            return name().toLowerCase();
        }
    }
}