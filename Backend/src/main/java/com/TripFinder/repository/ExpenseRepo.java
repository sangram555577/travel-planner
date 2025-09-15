package com.TripFinder.repository;

import com.TripFinder.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link Expense} entities.
 * Provides standard CRUD operations and custom queries for expense data.
 */
@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Integer> {

    /**
     * Finds all expenses associated with a specific user.
     *
     * @param userId The ID of the user whose expenses are to be retrieved.
     * @return A list of {@link Expense} objects belonging to the specified user.
     */
    List<Expense> findByUserId(int userId);
}