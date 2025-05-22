package com.dealermonkey.api.integration;

import com.dealermonkey.api.model.Card;
import com.dealermonkey.api.service.DeckService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

@SpringBootTest
public class DeckServicePrototypeScopeTest {

    @Autowired
    private ObjectFactory<DeckService> deckServiceFactory;

    @Test
    public void testPrototypeScopeCreatesIndependentDeckInstances() {
        DeckService deck1 = deckServiceFactory.getObject();
        DeckService deck2 = deckServiceFactory.getObject();
        assertNotSame(deck1, deck2, "Expected prototype-scoped beans to be different instances");

        Card cardFromDeck1 = deck1.dealCard().card();
        Card topOfDeck2 = deck2.cheat();
        assertEquals(cardFromDeck1, topOfDeck2, "Decks should be independent; card dealt from one should not affect the other");

        assertEquals(51, deck1.getDeck().size(), "Deck1 should have 51 cards after dealing one");
        assertEquals(52, deck2.getDeck().size(), "Deck2 should still have 52 cards");
    }
}