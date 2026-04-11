package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.Book;
import com.example.demo.repository.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    // 添加图书
    public boolean addBook(Book book) {
        book.setStatus("1");
        book.setDeleted(0);
        book.setCreateTime(new Date());
        book.setUpdateTime(new Date());
        int result = bookMapper.insert(book);
        return result > 0;
    }

    // 更新图书
    public boolean updateBook(Book book) {
        book.setUpdateTime(new Date());
        int result = bookMapper.updateById(book);
        return result > 0;
    }

    // 删除图书（软删除）
    public boolean deleteBook(Long id) {
        Book book = bookMapper.selectById(id);
        if (book != null) {
            book.setDeleted(1);
            book.setUpdateTime(new Date());
            int result = bookMapper.updateById(book);
            return result > 0;
        }
        return false;
    }

    // 根据ID获取图书
    public Book getBookById(Long id) {
        return bookMapper.selectById(id);
    }

    // 获取所有图书（分页）
    public IPage<Book> getAllBooks(int page, int size) {
        Page<Book> pageInfo = new Page<>(page, size);
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        return bookMapper.selectPage(pageInfo, queryWrapper);
    }

    // 搜索图书
    public IPage<Book> searchBooks(String keyword, int page, int size) {
        Page<Book> pageInfo = new Page<>(page, size);
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .and(qw -> qw.like("title", keyword)
                        .or().like("author", keyword)
                        .or().like("isbn", keyword)
                        .or().like("publisher", keyword));
        return bookMapper.selectPage(pageInfo, queryWrapper);
    }

    // 获取可借图书
    public IPage<Book> getAvailableBooks(int page, int size) {
        Page<Book> pageInfo = new Page<>(page, size);
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0).eq("status", 1);
        return bookMapper.selectPage(pageInfo, queryWrapper);
    }

    // 更新图书状态
    public boolean updateBookStatus(Long id, int status) {
        Book book = bookMapper.selectById(id);
        if (book != null) {
            book.setStatus(String.valueOf(status));
            book.setUpdateTime(new Date());
            int result = bookMapper.updateById(book);
            return result > 0;
        }
        return false;
    }

    // 根据分类获取图书
    public IPage<Book> getBooksByCategory(String category, int page, int size) {
        Page<Book> pageInfo = new Page<>(page, size);
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0).eq("category", category);
        return bookMapper.selectPage(pageInfo, queryWrapper);
    }
}
