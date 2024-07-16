package kr.co.backend.repository.custom;

import com.querydsl.core.Tuple;

import java.util.List;

public interface CustomCategoryRepository {

    List<Tuple> getAll();
}
