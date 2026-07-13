package com.westudy.bookmark.service;

import com.westudy.bookmark.mapper.BookmarkMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookmarkService {

    private final BookmarkMapper bookmarkMapper;

    public BookmarkService(BookmarkMapper bookmarkMapper) {
        this.bookmarkMapper = bookmarkMapper;
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(long postId, long userId) {
        return bookmarkMapper.isBookmarked(postId, userId) > 0;
    }

    public boolean toggleBookmark(long postId, long userId) {
        if (isBookmarked(postId, userId)) {
            bookmarkMapper.deleteBookmark(postId, userId);
            return false;
        } else {
            bookmarkMapper.insertBookmark(postId, userId);
            return true;
        }
    }
}
