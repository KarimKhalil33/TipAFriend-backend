package com.tipafriend.repository;

import com.tipafriend.model.Post;
import com.tipafriend.model.enums.PostCategory;
import com.tipafriend.model.enums.PostStatus;
import com.tipafriend.model.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByAuthorIdInAndStatus(Collection<Long> authorIds, PostStatus status, Pageable pageable);
    Page<Post> findByAuthorIdInAndTypeAndStatus(Collection<Long> authorIds, PostType type, PostStatus status, Pageable pageable);
    Page<Post> findByAuthorIdInAndCategoryAndStatus(Collection<Long> authorIds, PostCategory category, PostStatus status, Pageable pageable);
    Page<Post> findByAuthorIdInAndTypeAndCategoryAndStatus(Collection<Long> authorIds, PostType type, PostCategory category, PostStatus status, Pageable pageable);
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
}
