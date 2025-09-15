package com.TripFinder.serviceImpl;

import com.TripFinder.dto.ExpenseDto;
import com.TripFinder.entity.Expense;
import com.TripFinder.entity.User;
import com.TripFinder.repository.ExpenseRepo;
import com.TripFinder.repository.UserRepo;
import com.TripFinder.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public List<Expense> getExpensesByUserId(Integer userId) {
        return expenseRepo.findByUserId(userId);
    }

    @Override
    public Expense saveExpense(ExpenseDto expenseDto) {
        User user = userRepo.findById(expenseDto.userId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + expenseDto.userId()));

        Expense expense = new Expense();
        expense.setAmount(expenseDto.amount());
        expense.setCategory(expenseDto.category());
        expense.setDescription(expenseDto.description());
        expense.setUser(user);
        // The 'date' field is now handled automatically by @CreationTimestamp

        return expenseRepo.save(expense);
    }

    @Override
    public void deleteExpenseById(Integer expenseId) {
        if (!expenseRepo.existsById(expenseId)) {
            throw new RuntimeException("Expense not found with ID: " + expenseId);
        }
        expenseRepo.deleteById(expenseId);
    }
}