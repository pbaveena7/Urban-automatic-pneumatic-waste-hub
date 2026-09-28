package com.example.pneumaticwaste.service;

import com.example.pneumaticwaste.model.JournalEntry;
import com.example.pneumaticwaste.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;

    public JournalEntryService(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    public List<JournalEntry> getAllJournalEntries() {
        return journalEntryRepository.findAll();
    }

    public JournalEntry createJournalEntry(JournalEntry entry) {
        if (entry.getDebitAccountId() == null || entry.getCreditAccountId() == null) {
            throw new RuntimeException("Debit and Credit accounts must be specified");
        }
        if (entry.getAmount() == null || entry.getAmount() <= 0) {
            throw new RuntimeException("Journal entry amount must be greater than 0");
        }
        if (entry.getTransactionDate() == null) {
            entry.setTransactionDate(LocalDate.now());
        }
        return journalEntryRepository.save(entry);
    }
}
