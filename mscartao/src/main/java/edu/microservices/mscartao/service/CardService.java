package edu.microservices.mscartao.service;

import edu.microservices.mscartao.domain.Card;
import edu.microservices.mscartao.dto.CardDTO;
import edu.microservices.mscartao.expection.erros.CardNotFoundException;
import edu.microservices.mscartao.expection.erros.DuplicateCardException;
import edu.microservices.mscartao.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public List<CardDTO> findAll() {
        List<Card> cards = cardRepository.findAll();
        if (!cards.isEmpty()) {
            return cards.stream().map(CardDTO::new).collect(Collectors.toList());
        } else {
            throw new CardNotFoundException("No cards found");
        }
    }

    public CardDTO findById(Long id) {
        Optional<Card> optionalCard = cardRepository.findById(id);
        if (optionalCard.isPresent()) {
            return new CardDTO(optionalCard.get());
        } else {
            throw new CardNotFoundException("Card not found with ID: " + id);
        }
    }

    @Transactional
    public CardDTO saveCard(CardDTO cardDTO) {
        String name = cardDTO.getCardName();
        if (cardRepository.existsByCardName(name)) {
            throw new DuplicateCardException("Card with name '" + name + "' already exists");
        }
        Card card = cardDTO.toModel();
        card = cardRepository.save(card);
        return new CardDTO(card);
    }

    @Transactional
    public CardDTO update(Long id, CardDTO cardDTO) {
        Optional<Card> optionalCard = cardRepository.findById(id);
        if (optionalCard.isPresent()) {
            Card existingCard = optionalCard.get();
            String newName = cardDTO.getCardName();

            if (!existingCard.getCardName().equals(newName) && cardRepository.existsByCardName(newName)) {
                throw new DuplicateCardException("Another card with name '" + newName + "' already exists");
            }

            // Update the existing card with new data
            existingCard.setCardName(newName);
            existingCard.setIncome(cardDTO.getIncome());
            existingCard.setCreditCardBrand(cardDTO.getCreditCardBrand());
            existingCard.setMinLimit(cardDTO.getMinLimit());

            cardRepository.save(existingCard);
            return new CardDTO(existingCard);
        } else {
            throw new CardNotFoundException("Card not found with ID: " + id);
        }
    }

    @Transactional
    public void deleteCard(Long id) {
        Optional<Card> optionalCard = cardRepository.findById(id);
        if (optionalCard.isPresent()) {
            cardRepository.delete(optionalCard.get());
        } else {
            throw new CardNotFoundException("Card not found with ID: " + id);
        }
    }
}
