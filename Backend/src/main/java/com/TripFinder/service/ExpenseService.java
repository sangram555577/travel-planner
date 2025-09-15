package com.TripFinder.service;

import com.TripFinder.dto.ExpenseDto;
import com.TripFinder.entity.Expense;
import java.util.List;

/**
 * Service interface for managing expense-related business logic.
 */
public interface ExpenseService {
    List<Expense> getExpensesByUserId(Integer userId);
    Expense saveExpense(ExpenseDto expenseDto);
    void deleteExpenseById(Integer expenseId);
}