package com.example.pneumaticwaste.service;

import com.example.pneumaticwaste.model.Account;
import com.example.pneumaticwaste.model.JournalEntry;
import com.example.pneumaticwaste.model.Payment;
import com.example.pneumaticwaste.model.VendorBill;
import com.example.pneumaticwaste.repository.AccountRepository;
import com.example.pneumaticwaste.repository.ContactRepository;
import com.example.pneumaticwaste.repository.JournalEntryRepository;
import com.example.pneumaticwaste.repository.PaymentRepository;
import com.example.pneumaticwaste.repository.PurchaseOrderRepository;
import com.example.pneumaticwaste.repository.VendorBillRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VendorBillService {

    private final VendorBillRepository vendorBillRepository;
    private final ContactRepository contactRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;
    private final JournalEntryRepository journalEntryRepository;

    public VendorBillService(VendorBillRepository vendorBillRepository,
                             ContactRepository contactRepository,
                             PurchaseOrderRepository purchaseOrderRepository,
                             PaymentRepository paymentRepository,
                             AccountRepository accountRepository,
                             JournalEntryRepository journalEntryRepository) {
        this.vendorBillRepository = vendorBillRepository;
        this.contactRepository = contactRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.paymentRepository = paymentRepository;
        this.accountRepository = accountRepository;
        this.journalEntryRepository = journalEntryRepository;
    }

    public List<VendorBill> getAllVendorBills() {
        return vendorBillRepository.findAll();
    }

    public VendorBill getVendorBillById(Long id) {
        return vendorBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor bill not found with id: " + id));
    }

    public VendorBill createVendorBill(VendorBill bill) {
        if (bill.getVendorId() != null && !contactRepository.existsById(bill.getVendorId())) {
            throw new RuntimeException("Vendor not found with id: " + bill.getVendorId());
        }
        if (bill.getPurchaseOrderId() != null && !purchaseOrderRepository.existsById(bill.getPurchaseOrderId())) {
            throw new RuntimeException("Purchase order not found with id: " + bill.getPurchaseOrderId());
        }
        if (bill.getBillDate() == null) {
            bill.setBillDate(LocalDate.now());
        }
        // paid must default to false
        bill.setPaid(false);
        return vendorBillRepository.save(bill);
    }

    // Process payment for a vendor bill and automatically record double-entry journal entry
    public VendorBill payVendorBill(Long id) {
        VendorBill bill = getVendorBillById(id);
        if (Boolean.TRUE.equals(bill.getPaid())) {
            throw new RuntimeException("Vendor bill with ID " + id + " is already paid.");
        }

        bill.setPaid(true);
        VendorBill savedBill = vendorBillRepository.save(bill);

        // 1. Create Payment record for Vendor
        Payment payment = new Payment(
                bill.getVendorId(),
                "PAID",
                "VENDOR_BILL",
                bill.getId(),
                bill.getAmount(),
                LocalDate.now(),
                "BANK_TRANSFER"
        );
        paymentRepository.save(payment);

        // 2. Fetch or create Expense & Bank accounts
        Account expenseAccount = accountRepository.findByAccountCode("E001")
                .orElseGet(() -> accountRepository.save(new Account("E001", "Mechanical Maintenance Expenses", "EXPENSE", 0.0)));

        Account bankAccount = accountRepository.findByAccountCode("A001")
                .orElseGet(() -> accountRepository.save(new Account("A001", "Bank", "ASSET", 100000.0)));

        // 3. Create Double Entry Accounting record
        // Debit: Expense Account, Credit: Bank Account
        JournalEntry entry = new JournalEntry(
                LocalDate.now(),
                "Vendor bill payment #" + bill.getId() + " - " + bill.getDescription(),
                expenseAccount.getId(),
                bankAccount.getId(),
                bill.getAmount()
        );
        journalEntryRepository.save(entry);

        // 4. Update balances
        expenseAccount.setBalance(expenseAccount.getBalance() + bill.getAmount());
        bankAccount.setBalance(bankAccount.getBalance() - bill.getAmount());
        accountRepository.save(expenseAccount);
        accountRepository.save(bankAccount);

        return savedBill;
    }
}
