package com.example.driver.mapper;

public interface Mapper<T, U> {
    T mapToEntity(U dto);
    U mapToDTO(T entity);
}