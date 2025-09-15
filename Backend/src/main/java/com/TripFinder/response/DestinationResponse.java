package com.TripFinder.response;

import lombok.Data;
import java.util.List;

@Data
public class DestinationResponse<T> {
    private List<T> allDestinations;
    private int skip;
    private int limit;
    private int total;
}
