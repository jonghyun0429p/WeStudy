package com.westudy.bookmark.controller;

import com.westudy.bookmark.dto.BookmarkToggleRequestDTO;
import com.westudy.bookmark.dto.BookmarkToggleResponseDTO;
import com.westudy.bookmark.service.BookmarkService;
import com.westudy.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@Tag(name = "북마크 컨트롤러", description = "스터디 게시글 북마크(관심 등록) 처리")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping("/toggle")
    @Operation(summary = "북마크 토글", description = "게시글 북마크 추가 및 취소 처리")
    public ResponseEntity<BookmarkToggleResponseDTO> toggleBookmark(
            @RequestBody BookmarkToggleRequestDTO request) {
        long userId = SecurityUtil.getCurrentUserId();
        boolean result = bookmarkService.toggleBookmark(request.getPostId(), userId);
        return ResponseEntity.ok(new BookmarkToggleResponseDTO(result));
    }
}
