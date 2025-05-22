package com.dealermonkey.api.service;

import com.dealermonkey.api.dto.response.CardResponse;
import com.dealermonkey.api.model.Card;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
@Scope("prototype")
public class DeckService {

    private final LinkedList<Card> deck = new LinkedList<>();
    private final List<Card> discardPile = new ArrayList<>();
    private final Set<Card> dealtCards = new HashSet<>();

    public DeckService() {
        rebuildDeck();
    }

    public synchronized CardResponse dealCard() {
        checkDeckNotEmpty();
        final Card card = deck.removeFirst();
        dealtCards.add(card);
        log.debug("Dealt card: {}", card);

        return CardResponse.builder().card(card).build();
    }


    public synchronized void discardCard(Card card) {
        if (discardPile.contains(card)) {
            throw new IllegalArgumentException("Card has already been discarded: " + card);
        }
        if (!dealtCards.contains(card)) {
            throw new IllegalArgumentException("Cannot discard card that was not dealt: " + card);
        }
        dealtCards.remove(card);
        discardPile.add(card);
        log.debug("Card discarded: {}", card);
    }

    public synchronized void shuffleDeck() {
        checkDeckNotEmpty();
        Collections.shuffle(deck);
        log.debug("Deck shuffled");
    }

    public synchronized void cutDeck(int index) {
        checkDeckNotEmpty();
        List<Card> bottomSplit = List.copyOf(deck.subList(index, deck.size()));
        List<Card> topSplit = List.copyOf(deck.subList(0, index));
        deck.clear();
        deck.addAll(bottomSplit);
        deck.addAll(topSplit);
        log.debug("Deck cut at index: {}", index);
    }

    public synchronized void orderDeck() {
        checkDeckNotEmpty();
        deck.sort(defaultComparator());
        log.debug("Deck ordered in default sequence");
    }

    public synchronized void rebuildDeck() {
        deck.clear();
        discardPile.clear();
        Arrays.stream(Card.Suit.values())
                .flatMap(suit -> Arrays.stream(Card.Rank.values())
                        .map(rank -> new Card(suit, rank)))
                .forEach(deck::add);
        orderDeck();
        log.debug("Deck rebuilt and ordered with all 52 cards");
    }

    public synchronized Card cheat() {
        if (deck.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cards left in the deck");
        }
        Card peek = deck.peekFirst();
        log.debug("Cheat peek at card: {}", peek);
        return peek;
    }

    public synchronized List<Card> getDeck() {
        return new ArrayList<>(deck);
    }

    public synchronized List<Card> getDiscardPile() {
        return new ArrayList<>(discardPile);
    }

    private synchronized Comparator<Card> defaultComparator() {
        return Comparator
                .comparing((Card c) -> c.suit().ordinal())
                .thenComparing(c -> c.rank().ordinal());
    }

    public synchronized void setDeck(List<Card> cards) {
        this.deck.clear();
        this.deck.addAll(cards);
    }

    private void checkDeckNotEmpty() {
        if (deck == null || deck.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cards left in the deck");
        }
    }
}