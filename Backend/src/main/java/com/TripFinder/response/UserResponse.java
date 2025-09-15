package com.TripFinder.response;

import lombok.Data;
import java.util.List;

@Data
public class UserResponse<T> {
    private List<T> allUsers;
    private int skip;
    private int limit;
    private int total;

    // Getters and Setters
    public List<T> getAllUsers() { return allUsers; }
    public void setAllUsers(List<T> allUsers) { this.allUsers = allUsers; }
    public int getSkip() { return skip; }
    public void setSkip(int skip) { this.skip = skip; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}