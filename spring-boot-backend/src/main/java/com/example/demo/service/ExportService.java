package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.model.Book;
import com.example.demo.model.BorrowRecord;
import com.example.demo.model.ExportTask;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.BookMapper;
import com.example.demo.repository.BorrowRecordMapper;
import com.example.demo.repository.ExportTaskMapper;
import com.example.demo.repository.UserAccountMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private ExportTaskMapper exportTaskMapper;

    @Value("${export.path:./exports}")
    private String exportPath;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public byte[] exportBooksToExcel() {
        List<Book> books = bookMapper.selectList(
            new LambdaQueryWrapper<Book>().eq(Book::getDeleted, 0)
        );

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("图书数据");

            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "ISBN", "书名", "英文书名", "作者", "英文作者", "出版社", 
                              "出版年份", "分类", "分类名称", "语言", "页数", "价格", "状态", 
                              "总副本数", "可借副本数", "位置", "书架号", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(book.getId());
                row.createCell(1).setCellValue(book.getIsbn() != null ? book.getIsbn() : "");
                row.createCell(2).setCellValue(book.getTitle() != null ? book.getTitle() : "");
                row.createCell(3).setCellValue(book.getTitleEn() != null ? book.getTitleEn() : "");
                row.createCell(4).setCellValue(book.getAuthor() != null ? book.getAuthor() : "");
                row.createCell(5).setCellValue(book.getAuthorEn() != null ? book.getAuthorEn() : "");
                row.createCell(6).setCellValue(book.getPublisher() != null ? book.getPublisher() : "");
                row.createCell(7).setCellValue(book.getPublishYear() != null ? book.getPublishYear() : "");
                row.createCell(8).setCellValue(book.getCategory() != null ? book.getCategory() : "");
                row.createCell(9).setCellValue(book.getCategoryName() != null ? book.getCategoryName() : "");
                row.createCell(10).setCellValue(book.getLanguage() != null ? book.getLanguage() : "");
                row.createCell(11).setCellValue(book.getPages() != null ? book.getPages() : 0);
                row.createCell(12).setCellValue(book.getPrice() != null ? book.getPrice().doubleValue() : 0);
                row.createCell(13).setCellValue(book.getStatus() != null ? book.getStatus() : "");
                row.createCell(14).setCellValue(book.getTotalCopies() != null ? book.getTotalCopies() : 0);
                row.createCell(15).setCellValue(book.getAvailableCopies() != null ? book.getAvailableCopies() : 0);
                row.createCell(16).setCellValue(book.getLocation() != null ? book.getLocation() : "");
                row.createCell(17).setCellValue(book.getShelfNo() != null ? book.getShelfNo() : "");
                row.createCell(18).setCellValue(book.getCreateTime() != null ? DATE_FORMAT.format(book.getCreateTime()) : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出图书数据失败", e);
        }
    }

    public byte[] exportBorrowStatisticsToExcel() {
        List<BorrowRecord> records = borrowRecordMapper.selectList(
            new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getDeleted, 0)
        );

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("借阅统计");

            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "用户ID", "图书ID", "图书条码", "借阅日期", "应还日期", 
                              "归还日期", "续借次数", "状态", "操作员ID", "备注", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (BorrowRecord record : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.getId());
                row.createCell(1).setCellValue(record.getUserId() != null ? record.getUserId() : 0);
                row.createCell(2).setCellValue(record.getBookId() != null ? record.getBookId() : 0);
                row.createCell(3).setCellValue(record.getBookBarcode() != null ? record.getBookBarcode() : "");
                row.createCell(4).setCellValue(record.getBorrowDate() != null ? DATE_FORMAT.format(record.getBorrowDate()) : "");
                row.createCell(5).setCellValue(record.getDueDate() != null ? DATE_FORMAT.format(record.getDueDate()) : "");
                row.createCell(6).setCellValue(record.getReturnDate() != null ? DATE_FORMAT.format(record.getReturnDate()) : "");
                row.createCell(7).setCellValue(record.getRenewCount() != null ? record.getRenewCount() : 0);
                row.createCell(8).setCellValue(getStatusText(record.getStatus()));
                row.createCell(9).setCellValue(record.getOperatorId() != null ? record.getOperatorId() : 0);
                row.createCell(10).setCellValue(record.getRemarks() != null ? record.getRemarks() : "");
                row.createCell(11).setCellValue(record.getCreateTime() != null ? DATE_FORMAT.format(record.getCreateTime()) : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出借阅统计失败", e);
        }
    }

    public byte[] exportUsersToExcel() {
        List<UserAccount> users = userAccountMapper.selectList(
            new LambdaQueryWrapper<UserAccount>().eq(UserAccount::getDeleted, 0)
        );

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("用户数据");

            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "用户名", "用户类型", "真实姓名", "电话", "邮箱", 
                              "身份证号", "机构", "角色", "语言", "状态", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (UserAccount user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getUsername() != null ? user.getUsername() : "");
                row.createCell(2).setCellValue(user.getUserType() != null ? user.getUserType() : "");
                row.createCell(3).setCellValue(user.getRealName() != null ? user.getRealName() : "");
                row.createCell(4).setCellValue(user.getPhone() != null ? user.getPhone() : "");
                row.createCell(5).setCellValue(user.getEmail() != null ? user.getEmail() : "");
                row.createCell(6).setCellValue(user.getIdCard() != null ? user.getIdCard() : "");
                row.createCell(7).setCellValue(user.getInstitution() != null ? user.getInstitution() : "");
                row.createCell(8).setCellValue(user.getRole() != null ? user.getRole() : "");
                row.createCell(9).setCellValue(user.getLanguage() != null ? user.getLanguage() : "");
                row.createCell(10).setCellValue(user.getStatus() != null ? (user.getStatus() == 1 ? "正常" : "禁用") : "");
                row.createCell(11).setCellValue(user.getCreateTime() != null ? DATE_FORMAT.format(user.getCreateTime()) : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出用户数据失败", e);
        }
    }

    public byte[] exportToCSV(String type) {
        StringBuilder csv = new StringBuilder();
        
        switch (type) {
            case ExportTask.TYPE_BOOK_EXPORT:
                csv.append("ID,ISBN,书名,英文书名,作者,英文作者,出版社,出版年份,分类,分类名称,语言,页数,价格,状态,总副本数,可借副本数,位置,书架号,创建时间\n");
                List<Book> books = bookMapper.selectList(
                    new LambdaQueryWrapper<Book>().eq(Book::getDeleted, 0)
                );
                for (Book book : books) {
                    csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        book.getId(),
                        escapeCSV(book.getIsbn()),
                        escapeCSV(book.getTitle()),
                        escapeCSV(book.getTitleEn()),
                        escapeCSV(book.getAuthor()),
                        escapeCSV(book.getAuthorEn()),
                        escapeCSV(book.getPublisher()),
                        escapeCSV(book.getPublishYear()),
                        escapeCSV(book.getCategory()),
                        escapeCSV(book.getCategoryName()),
                        escapeCSV(book.getLanguage()),
                        book.getPages() != null ? book.getPages() : "",
                        book.getPrice() != null ? book.getPrice() : "",
                        escapeCSV(book.getStatus()),
                        book.getTotalCopies() != null ? book.getTotalCopies() : "",
                        book.getAvailableCopies() != null ? book.getAvailableCopies() : "",
                        escapeCSV(book.getLocation()),
                        escapeCSV(book.getShelfNo()),
                        book.getCreateTime() != null ? DATE_FORMAT.format(book.getCreateTime()) : ""
                    ));
                }
                break;
                
            case ExportTask.TYPE_BORROW_STATISTICS:
                csv.append("ID,用户ID,图书ID,图书条码,借阅日期,应还日期,归还日期,续借次数,状态,操作员ID,备注,创建时间\n");
                List<BorrowRecord> records = borrowRecordMapper.selectList(
                    new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getDeleted, 0)
                );
                for (BorrowRecord record : records) {
                    csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        record.getId(),
                        record.getUserId() != null ? record.getUserId() : "",
                        record.getBookId() != null ? record.getBookId() : "",
                        escapeCSV(record.getBookBarcode()),
                        record.getBorrowDate() != null ? DATE_FORMAT.format(record.getBorrowDate()) : "",
                        record.getDueDate() != null ? DATE_FORMAT.format(record.getDueDate()) : "",
                        record.getReturnDate() != null ? DATE_FORMAT.format(record.getReturnDate()) : "",
                        record.getRenewCount() != null ? record.getRenewCount() : "",
                        getStatusText(record.getStatus()),
                        record.getOperatorId() != null ? record.getOperatorId() : "",
                        escapeCSV(record.getRemarks()),
                        record.getCreateTime() != null ? DATE_FORMAT.format(record.getCreateTime()) : ""
                    ));
                }
                break;
                
            case ExportTask.TYPE_USER_STATISTICS:
                csv.append("ID,用户名,用户类型,真实姓名,电话,邮箱,身份证号,机构,角色,语言,状态,创建时间\n");
                List<UserAccount> users = userAccountMapper.selectList(
                    new LambdaQueryWrapper<UserAccount>().eq(UserAccount::getDeleted, 0)
                );
                for (UserAccount user : users) {
                    csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        user.getId(),
                        escapeCSV(user.getUsername()),
                        escapeCSV(user.getUserType()),
                        escapeCSV(user.getRealName()),
                        escapeCSV(user.getPhone()),
                        escapeCSV(user.getEmail()),
                        escapeCSV(user.getIdCard()),
                        escapeCSV(user.getInstitution()),
                        escapeCSV(user.getRole()),
                        escapeCSV(user.getLanguage()),
                        user.getStatus() != null ? (user.getStatus() == 1 ? "正常" : "禁用") : "",
                        user.getCreateTime() != null ? DATE_FORMAT.format(user.getCreateTime()) : ""
                    ));
                }
                break;
                
            default:
                throw new IllegalArgumentException("不支持的导出类型: " + type);
        }
        
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public ExportTask createExportTask(Long userId, String type, String fileName) {
        ExportTask task = new ExportTask();
        task.setUserId(userId);
        task.setType(type);
        task.setFileName(fileName);
        task.setStatus(ExportTask.STATUS_PENDING);
        task.setCreateTime(new Date());
        exportTaskMapper.insert(task);
        return task;
    }

    public void updateExportTaskStatus(Long taskId, String status, String filePath, String errorMessage) {
        ExportTask task = exportTaskMapper.selectById(taskId);
        if (task != null) {
            task.setStatus(status);
            if (filePath != null) {
                task.setFilePath(filePath);
            }
            if (errorMessage != null) {
                task.setErrorMessage(errorMessage);
            }
            if (ExportTask.STATUS_COMPLETED.equals(status) || ExportTask.STATUS_FAILED.equals(status)) {
                task.setCompleteTime(new Date());
            }
            exportTaskMapper.updateById(task);
        }
    }

    public List<ExportTask> getExportTasksByUserId(Long userId) {
        return exportTaskMapper.selectList(
            new LambdaQueryWrapper<ExportTask>()
                .eq(ExportTask::getUserId, userId)
                .orderByDesc(ExportTask::getCreateTime)
        );
    }

    public List<ExportTask> getAllExportTasks() {
        return exportTaskMapper.selectList(
            new LambdaQueryWrapper<ExportTask>()
                .orderByDesc(ExportTask::getCreateTime)
        );
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    private String getStatusText(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "借阅中";
            case 1: return "已归还";
            case 2: return "已逾期";
            default: return String.valueOf(status);
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public String generateFileName(String type) {
        String prefix;
        switch (type) {
            case ExportTask.TYPE_BOOK_EXPORT:
                prefix = "图书数据";
                break;
            case ExportTask.TYPE_BORROW_STATISTICS:
                prefix = "借阅统计";
                break;
            case ExportTask.TYPE_USER_STATISTICS:
                prefix = "用户数据";
                break;
            default:
                prefix = "导出数据";
        }
        return prefix + "_" + FILE_DATE_FORMAT.format(new Date());
    }

    public byte[] exportBooksToPDF() {
        List<Book> books = bookMapper.selectList(
            new LambdaQueryWrapper<Book>().eq(Book::getDeleted, 0)
        );

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, outputStream);
            document.open();

            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD
            );
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD
            );
            com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 10
            );

            document.add(new com.itextpdf.text.Paragraph("图书数据导出报告", titleFont));
            document.add(new com.itextpdf.text.Paragraph("导出时间: " + DATE_FORMAT.format(new Date()), normalFont));
            document.add(com.itextpdf.text.Chunk.NEWLINE);

            for (Book book : books) {
                document.add(new com.itextpdf.text.Paragraph("─────────────────────────────────────", normalFont));
                document.add(new com.itextpdf.text.Paragraph("书名: " + (book.getTitle() != null ? book.getTitle() : ""), headerFont));
                document.add(new com.itextpdf.text.Paragraph("ISBN: " + (book.getIsbn() != null ? book.getIsbn() : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("作者: " + (book.getAuthor() != null ? book.getAuthor() : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("出版社: " + (book.getPublisher() != null ? book.getPublisher() : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("分类: " + (book.getCategoryName() != null ? book.getCategoryName() : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("状态: " + (book.getStatus() != null ? book.getStatus() : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("可借副本: " + (book.getAvailableCopies() != null ? book.getAvailableCopies() : 0) + "/" + (book.getTotalCopies() != null ? book.getTotalCopies() : 0), normalFont));
                document.add(com.itextpdf.text.Chunk.NEWLINE);
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出图书PDF失败", e);
        }
    }

    public byte[] exportBorrowStatisticsToPDF() {
        List<BorrowRecord> records = borrowRecordMapper.selectList(
            new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getDeleted, 0)
        );

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, outputStream);
            document.open();

            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD
            );
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD
            );
            com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 10
            );

            document.add(new com.itextpdf.text.Paragraph("借阅统计导出报告", titleFont));
            document.add(new com.itextpdf.text.Paragraph("导出时间: " + DATE_FORMAT.format(new Date()), normalFont));
            document.add(new com.itextpdf.text.Paragraph("总记录数: " + records.size(), normalFont));
            document.add(com.itextpdf.text.Chunk.NEWLINE);

            for (BorrowRecord record : records) {
                document.add(new com.itextpdf.text.Paragraph("─────────────────────────────────────", normalFont));
                document.add(new com.itextpdf.text.Paragraph("记录ID: " + record.getId(), headerFont));
                document.add(new com.itextpdf.text.Paragraph("用户ID: " + record.getUserId(), normalFont));
                document.add(new com.itextpdf.text.Paragraph("图书ID: " + record.getBookId(), normalFont));
                document.add(new com.itextpdf.text.Paragraph("借阅日期: " + (record.getBorrowDate() != null ? DATE_FORMAT.format(record.getBorrowDate()) : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("应还日期: " + (record.getDueDate() != null ? DATE_FORMAT.format(record.getDueDate()) : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("归还日期: " + (record.getReturnDate() != null ? DATE_FORMAT.format(record.getReturnDate()) : "未归还"), normalFont));
                document.add(new com.itextpdf.text.Paragraph("状态: " + getStatusText(record.getStatus()), normalFont));
                document.add(com.itextpdf.text.Chunk.NEWLINE);
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出借阅统计PDF失败", e);
        }
    }

    public byte[] exportUsersToPDF() {
        List<UserAccount> users = userAccountMapper.selectList(
            new LambdaQueryWrapper<UserAccount>().eq(UserAccount::getDeleted, 0)
        );

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, outputStream);
            document.open();

            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD
            );
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD
            );
            com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 10
            );

            document.add(new com.itextpdf.text.Paragraph("用户数据导出报告", titleFont));
            document.add(new com.itextpdf.text.Paragraph("导出时间: " + DATE_FORMAT.format(new Date()), normalFont));
            document.add(new com.itextpdf.text.Paragraph("总用户数: " + users.size(), normalFont));
            document.add(com.itextpdf.text.Chunk.NEWLINE);

            for (UserAccount user : users) {
                document.add(new com.itextpdf.text.Paragraph("─────────────────────────────────────", normalFont));
                document.add(new com.itextpdf.text.Paragraph("用户名: " + (user.getUsername() != null ? user.getUsername() : ""), headerFont));
                document.add(new com.itextpdf.text.Paragraph("真实姓名: " + (user.getRealName() != null ? user.getRealName() : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("用户类型: " + (user.getUserType() != null ? user.getUserType() : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("邮箱: " + (user.getEmail() != null ? user.getEmail() : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("电话: " + (user.getPhone() != null ? user.getPhone() : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("机构: " + (user.getInstitution() != null ? user.getInstitution() : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("状态: " + (user.getStatus() != null ? (user.getStatus() == 1 ? "正常" : "禁用") : ""), normalFont));
                document.add(com.itextpdf.text.Chunk.NEWLINE);
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出用户PDF失败", e);
        }
    }

    public byte[] exportBorrowRecordsToCSV(Long userId, Long bookId, Integer status) {
        LambdaQueryWrapper<BorrowRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BorrowRecord::getDeleted, 0);
        if (userId != null) queryWrapper.eq(BorrowRecord::getUserId, userId);
        if (bookId != null) queryWrapper.eq(BorrowRecord::getBookId, bookId);
        if (status != null) queryWrapper.eq(BorrowRecord::getStatus, status);

        List<BorrowRecord> records = borrowRecordMapper.selectList(queryWrapper);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,用户ID,图书ID,图书条码,借阅日期,应还日期,归还日期,续借次数,状态,操作员ID,备注,创建时间\n");
        
        for (BorrowRecord record : records) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                record.getId(),
                record.getUserId() != null ? record.getUserId() : "",
                record.getBookId() != null ? record.getBookId() : "",
                escapeCSV(record.getBookBarcode()),
                record.getBorrowDate() != null ? DATE_FORMAT.format(record.getBorrowDate()) : "",
                record.getDueDate() != null ? DATE_FORMAT.format(record.getDueDate()) : "",
                record.getReturnDate() != null ? DATE_FORMAT.format(record.getReturnDate()) : "",
                record.getRenewCount() != null ? record.getRenewCount() : "",
                getStatusText(record.getStatus()),
                record.getOperatorId() != null ? record.getOperatorId() : "",
                escapeCSV(record.getRemarks()),
                record.getCreateTime() != null ? DATE_FORMAT.format(record.getCreateTime()) : ""
            ));
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportBorrowRecordsToExcel(Long userId, Long bookId, Integer status) {
        LambdaQueryWrapper<BorrowRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BorrowRecord::getDeleted, 0);
        if (userId != null) queryWrapper.eq(BorrowRecord::getUserId, userId);
        if (bookId != null) queryWrapper.eq(BorrowRecord::getBookId, bookId);
        if (status != null) queryWrapper.eq(BorrowRecord::getStatus, status);

        List<BorrowRecord> records = borrowRecordMapper.selectList(queryWrapper);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("借阅记录");

            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "用户ID", "图书ID", "图书条码", "借阅日期", "应还日期", 
                              "归还日期", "续借次数", "状态", "操作员ID", "备注", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (BorrowRecord record : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.getId());
                row.createCell(1).setCellValue(record.getUserId() != null ? record.getUserId() : 0);
                row.createCell(2).setCellValue(record.getBookId() != null ? record.getBookId() : 0);
                row.createCell(3).setCellValue(record.getBookBarcode() != null ? record.getBookBarcode() : "");
                row.createCell(4).setCellValue(record.getBorrowDate() != null ? DATE_FORMAT.format(record.getBorrowDate()) : "");
                row.createCell(5).setCellValue(record.getDueDate() != null ? DATE_FORMAT.format(record.getDueDate()) : "");
                row.createCell(6).setCellValue(record.getReturnDate() != null ? DATE_FORMAT.format(record.getReturnDate()) : "");
                row.createCell(7).setCellValue(record.getRenewCount() != null ? record.getRenewCount() : 0);
                row.createCell(8).setCellValue(getStatusText(record.getStatus()));
                row.createCell(9).setCellValue(record.getOperatorId() != null ? record.getOperatorId() : 0);
                row.createCell(10).setCellValue(record.getRemarks() != null ? record.getRemarks() : "");
                row.createCell(11).setCellValue(record.getCreateTime() != null ? DATE_FORMAT.format(record.getCreateTime()) : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出借阅记录失败", e);
        }
    }

    public byte[] exportBorrowRecordsToPDF(Long userId, Long bookId, Integer status) {
        LambdaQueryWrapper<BorrowRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BorrowRecord::getDeleted, 0);
        if (userId != null) queryWrapper.eq(BorrowRecord::getUserId, userId);
        if (bookId != null) queryWrapper.eq(BorrowRecord::getBookId, bookId);
        if (status != null) queryWrapper.eq(BorrowRecord::getStatus, status);

        List<BorrowRecord> records = borrowRecordMapper.selectList(queryWrapper);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, outputStream);
            document.open();

            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD
            );
            com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 10
            );

            document.add(new com.itextpdf.text.Paragraph("借阅记录导出报告", titleFont));
            document.add(new com.itextpdf.text.Paragraph("导出时间: " + DATE_FORMAT.format(new Date()), normalFont));
            document.add(new com.itextpdf.text.Paragraph("记录数: " + records.size(), normalFont));
            document.add(com.itextpdf.text.Chunk.NEWLINE);

            for (BorrowRecord record : records) {
                document.add(new com.itextpdf.text.Paragraph("─────────────────────────────────────", normalFont));
                document.add(new com.itextpdf.text.Paragraph("记录ID: " + record.getId() + " | 用户ID: " + record.getUserId() + " | 图书ID: " + record.getBookId(), normalFont));
                document.add(new com.itextpdf.text.Paragraph("借阅日期: " + (record.getBorrowDate() != null ? DATE_FORMAT.format(record.getBorrowDate()) : "") + " | 应还日期: " + (record.getDueDate() != null ? DATE_FORMAT.format(record.getDueDate()) : ""), normalFont));
                document.add(new com.itextpdf.text.Paragraph("状态: " + getStatusText(record.getStatus()), normalFont));
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出借阅记录PDF失败", e);
        }
    }
}
