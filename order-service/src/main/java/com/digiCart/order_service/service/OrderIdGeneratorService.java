package com.digiCart.order_service.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.digiCart.order_service.model.OrderSequence;
import com.digiCart.order_service.repository.OrderRepository;
import com.digiCart.order_service.repository.OrderSequenceRepository;

@Service
public class OrderIdGeneratorService {

    private static final String SEQUENCE_NAME = "order";
    private final OrderSequenceRepository sequenceRepository;
    private final OrderRepository orderRepository;

    public OrderIdGeneratorService(OrderSequenceRepository sequenceRepository, OrderRepository orderRepository) {
        this.sequenceRepository = sequenceRepository;
        this.orderRepository = orderRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(OrderIdGeneratorService.class);

    @Transactional
    public String nextOrderId() {
        log.info("Entering nextOrderId");
        long currentValue = determineCurrentOrderId();
        try {
            sequenceRepository.save(new OrderSequence(SEQUENCE_NAME, currentValue + 1));
            return formatOrderId(currentValue);
        } catch (DataIntegrityViolationException ex) {
            // Sequence row already exists; fall through to locked read/update.
        }

        OrderSequence sequence = sequenceRepository.findByNameForUpdate(SEQUENCE_NAME)
                .orElseThrow(() -> new IllegalStateException("Unable to obtain order sequence row"));

        currentValue = sequence.getNextVal();
        if (currentValue > 999_999L) {
            throw new IllegalStateException("Order ID limit exceeded: only six digits are supported");
        }
        sequence.setNextVal(currentValue + 1);
        sequenceRepository.save(sequence);

        return formatOrderId(currentValue);
    }

    private long determineCurrentOrderId() {
        String maxOrderId = orderRepository.findMaxOrderId();
        if (maxOrderId == null || maxOrderId.isBlank()) {
            return 1L;
        }
        if (!maxOrderId.matches("\\d+")) {
            return 1L;
        }
        long parsed = Long.parseLong(maxOrderId);
        long next = parsed + 1;
        if (next > 999_999L) {
            throw new IllegalStateException("Order ID limit exceeded: only six digits are supported");
        }
        return next;
    }

    private String formatOrderId(long value) {
        return String.format("%06d", value);
    }
}
