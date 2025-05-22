package com.dealermonkey.api.service;

import com.dealermonkey.api.dto.response.CardResponse;
import com.dealermonkey.api.model.Card;
import com.dealermonkey.api.model.Card.Rank;
import com.dealermonkey.api.model.Card.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeckServiceTest {

    private DeckService deckService;

    @BeforeEach
    void setUp() {
        deckService = new DeckService();
    }

    /**
     * Unit test to verify the deterministic order of the deck in {@link DeckService}.
     *
     * <p><b>Requirement:</b> The default sort order of the cards is:
     * <ul>
     *   <li>Suits ordered as: {@code [spades, hearts, clubs, diamonds]}</li>
     *   <li>Within each suit, ranks ordered as: {@code [2, 3, 4, 5, 6, 7, 8, 9, 10, jack, queen, king, ace]}</li>
     * </ul>
     *
     * This test asserts that calling {@code dealCard()} repeatedly returns cards in the expected order.
     */
    @Test
    public void testDeckOrderIsDeterministic() {
        AtomicInteger index = new AtomicInteger(0);

        Stream.of(Suit.values())
                .flatMap(suit -> Stream.of(Rank.values())
                        .map(rank -> new Card(suit, rank)))
                .forEach(expectedCard -> {
                    CardResponse response = deckService.dealCard();
                    Card actualCard = response.card();

                    assertNotNull(actualCard, "Card at index " + index.get() + " should not be null");
                    assertEquals(expectedCard, actualCard, "Mismatch at index " + index.getAndIncrement());
                });
    }


    /**
     * Unit test to verify tracking of dealt and discarded cards in {@link DeckService}.
     *
     * <p><b>Requirement:</b> The system will keep track of the cards dealt from
     * a discard pile of no longer in-play cards.
     *
     * <ul>
     *   <li>When a card is dealt, it should no longer be in the deck.</li>
     *   <li>When a card is discarded, it should be tracked in the discard pile.</li>
     *   <li>A card cannot be discarded if it hasn't been dealt.</li>
     *   <li>A card cannot be discarded more than once.</li>
     * </ul>
     */
    @Test
    public void testDealtAndDiscardPileTracking() {
        CardResponse dealtResponse = deckService.dealCard();
        Card dealtCard = dealtResponse.card();
        assertNotNull(dealtCard, "Dealt card should not be null");

        deckService.discardCard(dealtCard);
        assertTrue(deckService.getDiscardPile().contains(dealtCard), "Discard pile should contain the discarded card");

        assertThrows(IllegalArgumentException.class, () -> deckService.discardCard(dealtCard), "Should not discard the same card twice");

        Card undealtCard = new Card(Card.Suit.HEARTS, Card.Rank.KING);
        if (!undealtCard.equals(dealtCard)) {
            assertThrows(IllegalArgumentException.class, () -> deckService.discardCard(undealtCard), "Cannot discard a card that wasn't dealt");
        }
    }

    @Test
    public void testDealCardThrowsWhenDeckIsEmpty() {
        int deckSize = deckService.getDeck().size();

        IntStream.range(0, deckSize).forEach(i -> {
            CardResponse response = deckService.dealCard();
            assertNotNull(response.card(), "Expected a valid card at deal " + (i + 1));
        });

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, deckService::dealCard);

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("No cards left in the deck", exception.getReason());
    }

    @Test
    public void testShuffleDeckChangesOrder() {
        List<Card> originalOrder = new ArrayList<>(deckService.getDeck());
        deckService.shuffleDeck();
        List<Card> shuffledOrder = deckService.getDeck();

        assertEquals(originalOrder.size(), shuffledOrder.size(), "Deck size should remain the same after shuffling");
        boolean isDifferent = !originalOrder.equals(shuffledOrder);
        assertTrue(isDifferent, "Deck should be in a different order after shuffling");
    }

    @Test
    public void testCutDeckReordersCorrectly() {
        deckService.setDeck(List.of(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.HEARTS, Rank.KING),
                new Card(Suit.CLUBS, Rank.QUEEN),
                new Card(Suit.DIAMONDS, Rank.JACK)
        ));

        deckService.cutDeck(2);

        List<Card> expectedOrder = List.of(
                new Card(Suit.CLUBS, Rank.QUEEN),
                new Card(Suit.DIAMONDS, Rank.JACK),
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.HEARTS, Rank.KING)
        );

        assertEquals(expectedOrder, deckService.getDeck(), "Deck was not reordered correctly after cut");
    }

    @Test
    public void testOrderDeckSortsInDefaultSequence() {
        deckService.shuffleDeck();
        deckService.orderDeck();

        List<Card> expected = new ArrayList<>();
        Arrays.stream(Suit.values())
                .forEach(suit -> Arrays.stream(Rank.values())
                        .forEach(rank -> expected.add(new Card(suit, rank))));
        System.out.println(deckService.getDeck());

        assertEquals(expected, deckService.getDeck(), "Deck should be ordered in default suit and rank order");
    }

    @Test
    public void testRebuildDeckRestoresFullOrderedDeck() {
        deckService.shuffleDeck();
        deckService.dealCard();
        deckService.discardCard(deckService.dealCard().card());

        assertTrue(deckService.getDeck().size() < 52);
        assertTrue(deckService.getDiscardPile().size() > 0);

        deckService.rebuildDeck();

        List<Card> expected = new ArrayList<>();
        Arrays.stream(Suit.values())
                .forEach(suit -> Arrays.stream(Rank.values())
                        .forEach(rank -> expected.add(new Card(suit, rank))));

        assertEquals(52, deckService.getDeck().size());
        assertEquals(expected, deckService.getDeck());
        assertTrue(deckService.getDiscardPile().isEmpty());
    }

    @Test
    public void testCheatReturnsTopCardWithoutRemovingIt() {
        Card expectedTopCard = deckService.getDeck().get(0);

        Card peekedCard = deckService.cheat();

        assertEquals(expectedTopCard, peekedCard, "cheatPeek should return the top card without removing it");
        assertEquals(52, deckService.getDeck().size(), "Deck size should remain unchanged after cheatPeek");
    }

    @Test
    public void testCheatThrowsWhenDeckIsEmpty() {
        deckService.setDeck(List.of());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> deckService.cheat());
        assertEquals(404, exception.getStatusCode().value());
        assertEquals("No cards left in the deck", exception.getReason());
    }

}