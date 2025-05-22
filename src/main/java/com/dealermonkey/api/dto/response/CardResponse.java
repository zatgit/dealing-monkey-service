package com.dealermonkey.api.dto.response;

import com.dealermonkey.api.model.Card;
import lombok.Builder;

@Builder
public record CardResponse(
        Card card
) implements DeckResponse {
}
