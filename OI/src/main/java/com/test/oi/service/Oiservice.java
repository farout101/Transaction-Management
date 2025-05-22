package com.test.oi.service;

import com.test.oi.model.IndividualUser;
import com.test.oi.repository.IndiUserRepo;
import com.test.oi.repository.OiRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oiservice {

    private final OiRepo oiRepo;
    private final IndiUserRepo indiUserRepo;

    Logger logger = Logger.getLogger(Oiservice.class.getName());

    public String pay(Long id, String amount) {
        IndividualUser user = indiUserRepo.findById(id).orElse(null);

        if (user != null) {
            String currentAmount = user.getAmount();
            // Assuming amount is a string that can be converted to a number
            double newAmount = Double.parseDouble(currentAmount) - Double.parseDouble(amount);
            user.setAmount(String.valueOf(newAmount));
            indiUserRepo.save(user);
            logger.info("<UNK>" + user.getAmount());
            return "Payment successful. New amount: " + newAmount;
        } else {
            logger.warning("User not found with ID: " + id);
            return "User not found.";
        }
    }
}
