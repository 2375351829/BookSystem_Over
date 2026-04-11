package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.Book;
import com.example.demo.model.BorrowRecord;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.BookMapper;
import com.example.demo.repository.BorrowRecordMapper;
import com.example.demo.repository.UserAccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private UserAccountMapper userAccountMapper;

    @InjectMocks
    private BorrowService borrowService;

    private UserAccount testUser;
    private Book testBook;
    private BorrowRecord testBorrowRecord;

    @BeforeEach
    void setUp() {
        testUser = new UserAccount();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setStatus(1);
        testUser.setDeleted(0);

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("测试图书");
        testBook.setAvailableCopies(5);
        testBook.setTotalCopies(10);
        testBook.setStatus("available");
        testBook.setDeleted(0);

        testBorrowRecord = new BorrowRecord();
        testBorrowRecord.setId(1L);
        testBorrowRecord.setUserId(1L);
        testBorrowRecord.setBookId(1L);
        testBorrowRecord.setBookBarcode("BAR001");
        testBorrowRecord.setBorrowDate(new Date());
        testBorrowRecord.setDueDate(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
        testBorrowRecord.setRenewCount(0);
        testBorrowRecord.setStatus(0);
        testBorrowRecord.setDeleted(0);
    }

    @Test
    @DisplayName("借书功能 - 成功借书")
    void borrowBook_Success() {
        when(userAccountMapper.selectById(1L)).thenReturn(testUser);
        when(borrowRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(bookMapper.selectById(1L)).thenReturn(testBook);
        when(borrowRecordMapper.insert(any(BorrowRecord.class))).thenAnswer(invocation -> {
            BorrowRecord record = invocation.getArgument(0);
            record.setId(1L);
            return 1;
        });

        BorrowRecord result = borrowService.borrowBook(1L, 1L, "BAR001");

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getBookId());
        assertEquals(0, result.getStatus());
        verify(bookMapper).updateById(any(Book.class));
    }

    @Test
    @DisplayName("借书功能 - 用户达到借阅上限")
    void borrowBook_UserReachedLimit() {
        when(userAccountMapper.selectById(1L)).thenReturn(testUser);
        when(borrowRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(10L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.borrowBook(1L, 1L, "BAR001");
        });

        assertEquals("用户借阅数量已达上限", exception.getMessage());
        verify(bookMapper, never()).selectById(any());
    }

    @Test
    @DisplayName("借书功能 - 图书无库存")
    void borrowBook_NoAvailableCopies() {
        testBook.setAvailableCopies(0);
        when(userAccountMapper.selectById(1L)).thenReturn(testUser);
        when(borrowRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(bookMapper.selectById(1L)).thenReturn(testBook);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.borrowBook(1L, 1L, "BAR001");
        });

        assertEquals("图书无可用副本", exception.getMessage());
    }

    @Test
    @DisplayName("借书功能 - 用户不存在")
    void borrowBook_UserNotFound() {
        when(userAccountMapper.selectById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.borrowBook(1L, 1L, "BAR001");
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    @DisplayName("借书功能 - 图书不存在")
    void borrowBook_BookNotFound() {
        when(userAccountMapper.selectById(1L)).thenReturn(testUser);
        when(borrowRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(bookMapper.selectById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.borrowBook(1L, 1L, "BAR001");
        });

        assertEquals("图书不存在", exception.getMessage());
    }

    @Test
    @DisplayName("还书功能 - 成功还书")
    void returnBook_Success() {
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);
        when(borrowRecordMapper.updateById(any(BorrowRecord.class))).thenReturn(1);
        when(bookMapper.selectById(1L)).thenReturn(testBook);
        when(bookMapper.updateById(any(Book.class))).thenReturn(1);

        BorrowRecord result = borrowService.returnBook(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.getStatus());
        assertNotNull(result.getReturnDate());
        verify(bookMapper).updateById(any(Book.class));
    }

    @Test
    @DisplayName("还书功能 - 逾期罚款计算")
    void returnBook_OverdueFineCalculation() {
        testBorrowRecord.setDueDate(new Date(System.currentTimeMillis() - 10L * 24 * 60 * 60 * 1000));
        
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);
        when(borrowRecordMapper.updateById(any(BorrowRecord.class))).thenReturn(1);
        when(bookMapper.selectById(1L)).thenReturn(testBook);
        when(bookMapper.updateById(any(Book.class))).thenReturn(1);

        BorrowRecord result = borrowService.returnBook(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.getStatus());
        assertNotNull(result.getRemarks());
        assertTrue(result.getRemarks().contains("逾期罚款"));
    }

    @Test
    @DisplayName("还书功能 - 借阅记录不存在")
    void returnBook_RecordNotFound() {
        when(borrowRecordMapper.selectById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.returnBook(1L, 1L);
        });

        assertEquals("借阅记录不存在", exception.getMessage());
    }

    @Test
    @DisplayName("还书功能 - 无权操作")
    void returnBook_NoPermission() {
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.returnBook(1L, 2L);
        });

        assertEquals("无权操作此借阅记录", exception.getMessage());
    }

    @Test
    @DisplayName("还书功能 - 图书已归还")
    void returnBook_AlreadyReturned() {
        testBorrowRecord.setStatus(1);
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.returnBook(1L, 1L);
        });

        assertEquals("该图书已归还", exception.getMessage());
    }

    @Test
    @DisplayName("续借功能 - 成功续借")
    void renewBook_Success() {
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);
        when(borrowRecordMapper.updateById(any(BorrowRecord.class))).thenReturn(1);

        BorrowRecord result = borrowService.renewBook(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.getRenewCount());
        verify(borrowRecordMapper).updateById(any(BorrowRecord.class));
    }

    @Test
    @DisplayName("续借功能 - 达到续借上限")
    void renewBook_ReachedLimit() {
        testBorrowRecord.setRenewCount(2);
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.renewBook(1L, 1L);
        });

        assertEquals("续借次数已达上限", exception.getMessage());
    }

    @Test
    @DisplayName("续借功能 - 图书已逾期")
    void renewBook_BookOverdue() {
        testBorrowRecord.setDueDate(new Date(System.currentTimeMillis() - 1L * 24 * 60 * 60 * 1000));
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.renewBook(1L, 1L);
        });

        assertEquals("图书已逾期，无法续借，请先归还", exception.getMessage());
    }

    @Test
    @DisplayName("续借功能 - 图书已归还无法续借")
    void renewBook_AlreadyReturned() {
        testBorrowRecord.setStatus(1);
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.renewBook(1L, 1L);
        });

        assertEquals("该图书已归还，无法续借", exception.getMessage());
    }

    @Test
    @DisplayName("续借功能 - 借阅记录不存在")
    void renewBook_RecordNotFound() {
        when(borrowRecordMapper.selectById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.renewBook(1L, 1L);
        });

        assertEquals("借阅记录不存在", exception.getMessage());
    }

    @Test
    @DisplayName("借阅记录查询 - 成功查询")
    void getBorrowRecords_Success() {
        Page<BorrowRecord> mockPage = new Page<>(1, 10);
        mockPage.setTotal(1);
        mockPage.setRecords(new java.util.ArrayList<>(java.util.Collections.singletonList(testBorrowRecord)));
        
        when(borrowRecordMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        IPage<BorrowRecord> result = borrowService.getBorrowRecords(1L, 1, 10, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("借阅记录查询 - 按状态筛选")
    void getBorrowRecords_WithStatus() {
        Page<BorrowRecord> mockPage = new Page<>(1, 10);
        mockPage.setTotal(1);
        mockPage.setRecords(new java.util.ArrayList<>(java.util.Collections.singletonList(testBorrowRecord)));
        
        when(borrowRecordMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        IPage<BorrowRecord> result = borrowService.getBorrowRecords(1L, 1, 10, 0);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("获取当前借阅数量")
    void getCurrentBorrowCount_Success() {
        when(borrowRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

        int count = borrowService.getCurrentBorrowCount(1L);

        assertEquals(5, count);
    }

    @Test
    @DisplayName("检查用户是否可以借书 - 可以借书")
    void canUserBorrow_True() {
        when(borrowRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

        boolean canBorrow = borrowService.canUserBorrow(1L);

        assertTrue(canBorrow);
    }

    @Test
    @DisplayName("检查用户是否可以借书 - 不能借书")
    void canUserBorrow_False() {
        when(borrowRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(10L);

        boolean canBorrow = borrowService.canUserBorrow(1L);

        assertFalse(canBorrow);
    }

    @Test
    @DisplayName("根据ID获取借阅记录 - 成功")
    void getBorrowRecordById_Success() {
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);

        BorrowRecord result = borrowService.getBorrowRecordById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("根据ID获取借阅记录 - 记录不存在")
    void getBorrowRecordById_NotFound() {
        when(borrowRecordMapper.selectById(1L)).thenReturn(null);

        BorrowRecord result = borrowService.getBorrowRecordById(1L);

        assertNull(result);
    }

    @Test
    @DisplayName("验证记录所有者 - 是所有者")
    void isRecordOwner_True() {
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);

        boolean isOwner = borrowService.isRecordOwner(1L, 1L);

        assertTrue(isOwner);
    }

    @Test
    @DisplayName("验证记录所有者 - 不是所有者")
    void isRecordOwner_False() {
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);

        boolean isOwner = borrowService.isRecordOwner(1L, 2L);

        assertFalse(isOwner);
    }

    @Test
    @DisplayName("获取逾期数量")
    void getOverdueCount_Success() {
        when(borrowRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(2L);

        int count = borrowService.getOverdueCount(1L);

        assertEquals(2, count);
    }

    @Test
    @DisplayName("检查是否有未归还图书")
    void hasUnreturnedBook_True() {
        when(borrowRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        boolean hasUnreturned = borrowService.hasUnreturnedBook(1L, 1L);

        assertTrue(hasUnreturned);
    }

    @Test
    @DisplayName("检查是否有未归还图书 - 没有")
    void hasUnreturnedBook_False() {
        when(borrowRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        boolean hasUnreturned = borrowService.hasUnreturnedBook(1L, 1L);

        assertFalse(hasUnreturned);
    }
}
