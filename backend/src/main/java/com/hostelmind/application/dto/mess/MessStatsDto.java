package com.hostelmind.application.dto.mess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessStatsDto {
    private long totalServedToday;
    private long breakfastServedToday;
    private long lunchServedToday;
    private long snacksServedToday;
    private long dinnerServedToday;
    private double averageRating;
}
