package com.example.officefiles.repository;

import com.example.officefiles.entity.Segment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SegmentRepository extends JpaRepository<Segment, Long> {
//    @Query("select c from Segment c where c.document.id = ?1 and c.paragraph is not null")
//    List<Segment> findByDocument_IdAnAndPaAndParagraphNotNull(Long documentId);
//
//    @Query("select c from Segment c where c.document.id = ?1 and c.segmentId in ?2")
//    List<Segment> findByDocument_IdAndParagraph(Long documentId, List<Long> paragraphId);

    @Query("select c from Segment c where c.document.id = ?1 and c.paragraph is null")
    List<Segment> findByDocument_IdAnAndPaAndParagraphNull(Long documentId);
}
