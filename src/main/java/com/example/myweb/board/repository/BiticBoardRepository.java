package com.example.myweb.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.myweb.board.entity.BiticBoardEntity;

public interface BiticBoardRepository extends JpaRepository<BiticBoardEntity, Long>{
	// update bitic_board_table set views=views+1 where seq=?
	@Modifying
	@Transactional
	@Query(value = "update BiticBoardEntity b set b.views=b.views+1 where b.seq=:seq")
	void incrementViews(@Param("seq") Long seq);

	
	Page<BiticBoardEntity> findByTag(String tag, Pageable pageable);
	// 추천 수가 높은 3개의 글 가져오는 쿼리
    @Query("SELECT f FROM BiticBoardEntity f ORDER BY f.likeCount DESC")
    List<BiticBoardEntity> findTop3ByOrderByLikeCountDesc();
    
 }
