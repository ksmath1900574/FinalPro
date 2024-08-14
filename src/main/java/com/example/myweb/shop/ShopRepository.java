package com.example.myweb.shop;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShopRepository extends JpaRepository<ShopEntity, Long> {
    List<ShopEntity> findByProductnameContainingOrDescriptionContaining(String productname, String description);
    
    // 페이징기능 추가 합니다
    Page<ShopEntity> findByProductnameContainingOrDescriptionContaining(String productname, String description, Pageable pageable);
    
    // 판매자가 올린 다른 상품을 조회 (현재 상품 제외)
    List<ShopEntity> findBySellernicknameAndNomNot(String sellernickname, Long nom);
    
}
