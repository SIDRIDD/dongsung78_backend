package kr.co.backend.repository;

import kr.co.backend.domain.ContactComment;
import kr.co.backend.repository.custom.CustomContactCommentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactCommentRepository extends JpaRepository<ContactComment, Integer>, CustomContactCommentRepository {

    List<ContactComment> findByContactId(Integer contactId);

    Integer countComment(Integer integer);
}
