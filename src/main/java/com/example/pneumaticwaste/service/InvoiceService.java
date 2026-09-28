package com.example.pneumaticwaste.service;

import com.example.pneumaticwaste.model.Account;
import com.example.pneumaticwaste.model.Invoice;
import com.example.pneumaticwaste.model.JournalEntry;
import com.example.pneumaticwaste.model.Payment;
import com.example.pneumaticwaste.repository.AccountRepository;
import com.example.pneumaticwaste.repository.ContactRepository;
import com.example.pneumaticwaste.repository.InvoiceRepository;
import com.example.pneumaticwaste.repository.JournalEntryRepository;
import com.example.pneumaticwaste.repository.PaymentRepository;
import com.example.pneumaticwaste.repository.SalesOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ContactRepository contactRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;
    private final JournalEntryRepository journalEntryRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          ContactRepository contactRepository,
                          SalesOrderRepository salesOrderRepository,
                          PaymentRepository paymentRepository,
                          AccountRepository accountRepository,
                          JournalEntryRepository journalEntryRepository) {
        this.invoiceRepository = invoiceRepository;
        this.contactRepository = contactRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.paymentRepository = paymentRepository;
        this.accountRepository = accountRepository;
        this.journalEntryRepository = journalEntryRepository;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }

    public Invoice createInvoice(Invoice invoice) {
        if (invoice.getCustomerId() == null || !contactRepository.existsById(invoice.getCustomerId())) {
            throw new RuntimeException("Customer not found with id: " + invoice.getCustomerId());
        }
        if (invoice.getSalesOrderId() != null && !salesOrderRepository.existsById(invoice.getSalesOrderId())) {
            throw new RuntimeException("Sales order not found with id: " + invoice.getSalesOrderId());
        }
        if (invoice.getAmount() == null || invoice.getAmount() <= 0) {
            throw new RuntimeException("Invoice amount must be greater than 0");
        }
        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(LocalDate.now());
        }
        // Initial payment status must be false
        invoice.setPaid(false);
        return invoiceRepository.save(invoice);
    }

    // Process customer invoice payment and create double-entry journal entry
    public Invoice payInvoice(Long id) {
        Invoice invoice = getInvoiceById(id);
        if (Boolean.TRUE.equals(invoice.getPaid())) {
            throw new RuntimeException("Invoice with ID " + id + " is already paid.");
        }

        invoice.setPaid(true);
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // 1. Create Payment record for Customer
        Payment payment = new Payment(
                invoice.getCustomerId(),
                "RECEIVED",
                "INVOICE",
                invoice.getId(),
                invoice.getAmount(),
                LocalDate.now(),
                "BANK_TRANSFER"
        );
        paymentRepository.save(payment);

        // 2. Fetch or create Bank & Revenue accounts
        Account bankAccount = accountRepository.findByAccountCode("A001")
                .orElseGet(() -> accountRepository.save(new Account("A001", "Bank", "ASSET", 50000.0)));

        Account revenueAccount = accountRepository.findByAccountCode("I001")
                .orElseGet(() -> accountRepository.save(new Account("I001", "Commercial Pneumatic Waste Fees", "INCOME", 0.0)));

        // 3. Create Double Entry Accounting record
        // Debit: Bank Account, Credit: Waste Fees Income Account
        JournalEntry entry = new JournalEntry(
                LocalDate.now(),
                "Customer payment for invoice #" + invoice.getId() + " - " + invoice.getDescription(),
                bankAccount.getId(),
                revenueAccount.getId(),
                invoice.getAmount()
        );
        journalEntryRepository.save(entry);

        // 4. Update balances
        bankAccount.setBalance(bankAccount.getBalance() + invoice.getAmount());
        revenueAccount.setBalance(revenueAccount.getBalance() + invoice.getAmount());
        accountRepository.save(bankAccount);
        accountRepository.save(revenueAccount);

        return savedInvoice;
    }
}
