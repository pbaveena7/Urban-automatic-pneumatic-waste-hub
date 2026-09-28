package com.example.pneumaticwaste.service;

import com.example.pneumaticwaste.model.Contact;
import com.example.pneumaticwaste.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public Contact getContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + id));
    }

    public Contact createContact(Contact contact) {
        if (contact.getName() == null || contact.getName().trim().isEmpty()) {
            throw new RuntimeException("Contact name is required");
        }
        return contactRepository.save(contact);
    }

    public Contact updateContact(Long id, Contact contactDetails) {
        Contact contact = getContactById(id);
        contact.setName(contactDetails.getName());
        contact.setContactType(contactDetails.getContactType());
        contact.setPhone(contactDetails.getPhone());
        contact.setEmail(contactDetails.getEmail());
        contact.setAddress(contactDetails.getAddress());
        return contactRepository.save(contact);
    }

    public void deleteContact(Long id) {
        Contact contact = getContactById(id);
        contactRepository.delete(contact);
    }
}
