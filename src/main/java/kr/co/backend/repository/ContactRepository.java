package kr.co.backend.repository;

import com.querydsl.core.Tuple;
import kr.co.backend.repository.custom.CustomContactRepository;
import kr.co.backend.domain.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Integer>, CustomContactRepository {

    Page<Tuple> getAll(Pageable pageable);


    Page<Tuple> getProductContact(Pageable pageable, Integer contactType, Integer typeId);
}
