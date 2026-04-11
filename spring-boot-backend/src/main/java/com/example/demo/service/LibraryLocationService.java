package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.model.Book;
import com.example.demo.model.LibraryLocation;
import com.example.demo.repository.BookMapper;
import com.example.demo.repository.LibraryLocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LibraryLocationService {

    @Autowired
    private LibraryLocationMapper libraryLocationMapper;

    @Autowired
    private BookMapper bookMapper;

    public List<LibraryLocation> getAllActiveLocations() {
        return libraryLocationMapper.findAllActive();
    }

    public List<String> getAllFloors() {
        return libraryLocationMapper.findAllFloors();
    }

    public List<LibraryLocation> getLocationsByFloor(String floor) {
        return libraryLocationMapper.findByFloor(floor);
    }

    public LibraryLocation getLocationById(Long id) {
        return libraryLocationMapper.selectById(id);
    }

    public Map<String, Object> getLocationMap() {
        List<LibraryLocation> locations = getAllActiveLocations();
        Map<String, List<LibraryLocation>> floorMap = new LinkedHashMap<>();
        
        for (LibraryLocation location : locations) {
            String floor = location.getFloor();
            if (!floorMap.containsKey(floor)) {
                floorMap.put(floor, new ArrayList<>());
            }
            floorMap.get(floor).add(location);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("floors", getAllFloors());
        result.put("floorMap", floorMap);
        return result;
    }

    public Map<String, Object> findBookLocation(Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null || book.getDeleted() == 1) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("book", book);
        result.put("location", book.getLocation());
        result.put("shelfNo", book.getShelfNo());
        
        LambdaQueryWrapper<LibraryLocation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LibraryLocation::getDeleted, 0)
                   .eq(LibraryLocation::getStatus, 1);
        
        List<LibraryLocation> locations = libraryLocationMapper.selectList(queryWrapper);
        
        for (LibraryLocation loc : locations) {
            if (loc.getCategoryRange() != null && book.getCategory() != null) {
                if (loc.getCategoryRange().contains(book.getCategory())) {
                    result.put("libraryLocation", loc);
                    break;
                }
            }
        }
        
        return result;
    }

    public LibraryLocation createLocation(LibraryLocation location) {
        location.setDeleted(0);
        location.setStatus(1);
        location.setCreateTime(new Date());
        location.setUpdateTime(new Date());
        libraryLocationMapper.insert(location);
        return location;
    }

    public LibraryLocation updateLocation(Long id, LibraryLocation location) {
        LibraryLocation existing = libraryLocationMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return null;
        }
        
        location.setId(id);
        location.setDeleted(existing.getDeleted());
        location.setCreateTime(existing.getCreateTime());
        location.setUpdateTime(new Date());
        libraryLocationMapper.updateById(location);
        return location;
    }

    public boolean deleteLocation(Long id) {
        LibraryLocation location = libraryLocationMapper.selectById(id);
        if (location == null || location.getDeleted() == 1) {
            return false;
        }
        
        location.setDeleted(1);
        location.setUpdateTime(new Date());
        libraryLocationMapper.updateById(location);
        return true;
    }
}
