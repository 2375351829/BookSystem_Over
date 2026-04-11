package com.example.demo.integration;

import com.example.demo.model.Book;
import com.example.demo.repository.BookMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookIntegrationTest {

    @Autowired
    private BookMapper bookMapper;

    @Test
    void testBookLifecycle() {
        Book book = new Book();
        book.setIsbn("9787111234567");
        book.setTitle("Integration Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setCategory("Fiction");
        book.setCategoryName("小说");
        book.setLanguage("zh-CN");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);
        book.setStatus("available");
        book.setDeleted(0);

        int insertResult = bookMapper.insert(book);
        assertEquals(1, insertResult);
        assertNotNull(book.getId());

        Book retrievedBook = bookMapper.selectById(book.getId());
        assertNotNull(retrievedBook);
        assertEquals("Integration Test Book", retrievedBook.getTitle());
        assertEquals("Test Author", retrievedBook.getAuthor());

        retrievedBook.setStatus("borrowed");
        retrievedBook.setAvailableCopies(4);
        int updateResult = bookMapper.updateById(retrievedBook);
        assertEquals(1, updateResult);

        Book updatedBook = bookMapper.selectById(book.getId());
        assertNotNull(updatedBook);
        assertEquals("borrowed", updatedBook.getStatus());
        assertEquals(4, updatedBook.getAvailableCopies());

        bookMapper.deleteById(book.getId());
    }
}
