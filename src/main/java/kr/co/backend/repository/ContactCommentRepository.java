package kr.co.backend.repository;

import kr.co.backend.domain.ContactComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactCommentRepository extends JpaRepository<ContactComment, Long> {

    List<ContactComment> findByContactId(Long contactId);
}
