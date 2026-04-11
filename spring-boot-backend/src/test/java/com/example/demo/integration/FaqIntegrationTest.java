package com.example.demo.integration;

import com.example.demo.model.Faq;
import com.example.demo.repository.FaqMapper;
import com.example.demo.service.FaqService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class FaqIntegrationTest {

    @Autowired
    private FaqService faqService;

    @Autowired
    private FaqMapper faqMapper;

    @Test
    void testFaqLifecycle() {
        Faq faq = new Faq();
        faq.setQuestion("How to borrow a book?");
        faq.setAnswer("You can borrow a book at the library counter or through the online system.");
        faq.setCategory("Borrowing");
        faq.setSortOrder(1);
        faq.setStatus(1);
        faq.setDeleted(0);

        Faq createdFaq = faqService.createFaq(faq);
        assertNotNull(createdFaq.getId());
        assertEquals("How to borrow a book?", createdFaq.getQuestion());

        Faq retrievedFaq = faqService.getFaqById(createdFaq.getId());
        assertNotNull(retrievedFaq);
        assertEquals("Borrowing", retrievedFaq.getCategory());

        retrievedFaq.setAnswer("Updated answer");
        Faq updatedFaq = faqService.updateFaq(createdFaq.getId(), retrievedFaq);
        assertNotNull(updatedFaq);
        assertEquals("Updated answer", updatedFaq.getAnswer());

        boolean deleted = faqService.deleteFaq(createdFaq.getId());
        assertTrue(deleted);

        Faq deletedFaq = faqMapper.selectById(createdFaq.getId());
        assertNotNull(deletedFaq);
        assertEquals(1, deletedFaq.getDeleted());
    }
}
