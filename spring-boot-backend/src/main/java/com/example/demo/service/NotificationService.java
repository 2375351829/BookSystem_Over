package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.Book;
import com.example.demo.model.BorrowRecord;
import com.example.demo.model.Notification;
import com.example.demo.model.NotificationTemplate;
import com.example.demo.repository.BookMapper;
import com.example.demo.repository.BorrowRecordMapper;
import com.example.demo.repository.NotificationMapper;
import com.example.demo.repository.NotificationTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private NotificationTemplateMapper templateMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private BookMapper bookMapper;

    public Notification createNotification(Long userId, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(0);
        notification.setDeleted(0);
        notification.setCreateTime(new Date());
        notificationMapper.insert(notification);
        return notification;
    }

    public Notification sendNotification(Long userId, String type, String title, String content) {
        return createNotification(userId, title, content, type);
    }

    public Notification sendBorrowNotification(Long userId, Long borrowRecordId) {
        BorrowRecord record = borrowRecordMapper.selectById(borrowRecordId);
        if (record == null) {
            return null;
        }
        Book book = bookMapper.selectById(record.getBookId());
        String bookTitle = book != null ? book.getTitle() : "未知图书";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dueDateStr = sdf.format(record.getDueDate());
        String title = "借阅成功通知";
        String content = String.format("您已成功借阅《%s》，应还日期为%s，请按时归还。", bookTitle, dueDateStr);
        return createNotification(userId, title, content, Notification.TYPE_BORROW);
    }

    public Notification sendReturnNotification(Long userId, Long borrowRecordId) {
        BorrowRecord record = borrowRecordMapper.selectById(borrowRecordId);
        if (record == null) {
            return null;
        }
        Book book = bookMapper.selectById(record.getBookId());
        String bookTitle = book != null ? book.getTitle() : "未知图书";
        String title = "归还成功通知";
        String content = String.format("您已成功归还《%s》，感谢您的使用。", bookTitle);
        return createNotification(userId, title, content, Notification.TYPE_RETURN);
    }

    public Notification sendDueReminder(Long userId, Long borrowRecordId, int daysBeforeDue) {
        BorrowRecord record = borrowRecordMapper.selectById(borrowRecordId);
        if (record == null) {
            return null;
        }
        Book book = bookMapper.selectById(record.getBookId());
        String bookTitle = book != null ? book.getTitle() : "未知图书";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dueDateStr = sdf.format(record.getDueDate());
        String title = "图书即将到期提醒";
        String content = String.format("您借阅的《%s》将于%s到期，还剩%d天，请及时归还或续借。", bookTitle, dueDateStr, daysBeforeDue);
        return createNotification(userId, title, content, Notification.TYPE_DUE_REMINDER);
    }

    public Notification sendOverdueNotification(Long userId, Long borrowRecordId, int overdueDays) {
        BorrowRecord record = borrowRecordMapper.selectById(borrowRecordId);
        if (record == null) {
            return null;
        }
        Book book = bookMapper.selectById(record.getBookId());
        String bookTitle = book != null ? book.getTitle() : "未知图书";
        String title = "图书逾期通知";
        String content = String.format("您借阅的《%s》已逾期%d天，请尽快归还，以免产生更多罚款。", bookTitle, overdueDays);
        return createNotification(userId, title, content, Notification.TYPE_OVERDUE);
    }

    public Notification sendSystemNotification(Long userId, String title, String content) {
        return createNotification(userId, title, content, Notification.TYPE_SYSTEM);
    }

    public boolean markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            return false;
        }
        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getId, notificationId)
                .set(Notification::getIsRead, 1);
        return notificationMapper.update(null, updateWrapper) > 0;
    }

    public int markAllAsRead(Long userId) {
        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1);
        return notificationMapper.update(null, updateWrapper);
    }

    public IPage<Notification> getUserNotifications(Long userId, int page, int size, String type, Integer isRead) {
        Page<Notification> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getDeleted, 0);
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq(Notification::getType, type);
        }
        if (isRead != null) {
            queryWrapper.eq(Notification::getIsRead, isRead);
        }
        queryWrapper.orderByDesc(Notification::getCreateTime);
        return notificationMapper.selectPage(pageInfo, queryWrapper);
    }

    public long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .eq(Notification::getDeleted, 0);
        return notificationMapper.selectCount(queryWrapper);
    }

    public Map<String, Long> getUnreadCountByType(Long userId) {
        Map<String, Long> result = new HashMap<>();
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .eq(Notification::getDeleted, 0);
        for (Notification notification : notificationMapper.selectList(queryWrapper)) {
            String type = notification.getType();
            result.put(type, result.getOrDefault(type, 0L) + 1);
        }
        return result;
    }

    public boolean deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            return false;
        }
        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getId, notificationId)
                .set(Notification::getDeleted, 1);
        return notificationMapper.update(null, updateWrapper) > 0;
    }

    public NotificationTemplate getTemplateByCode(String code) {
        LambdaQueryWrapper<NotificationTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NotificationTemplate::getCode, code)
                .eq(NotificationTemplate::getDeleted, 0);
        return templateMapper.selectOne(queryWrapper);
    }

    public Notification createNotificationFromTemplate(Long userId, String templateCode, Map<String, String> params) {
        NotificationTemplate template = getTemplateByCode(templateCode);
        if (template == null) {
            return null;
        }
        String title = template.getTitle();
        String content = template.getContent();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            title = title.replace("{{" + entry.getKey() + "}}", entry.getValue());
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return createNotification(userId, title, content, template.getType());
    }
}
