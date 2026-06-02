
        package com.cinebook.service.ServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SeatLockService {

    private final StringRedisTemplate redisTemplate;

    private static final Duration LOCK_DURATION =
            Duration.ofMinutes(5);

    public boolean lockSeat(
            Long showId,
            String seatNumber,
            Long userId) {

        String key =
                "seat_lock:" + showId + ":" + seatNumber;

        Boolean success =
                redisTemplate.opsForValue()
                        .setIfAbsent(
                                key,
                                userId.toString(),
                                LOCK_DURATION
                        );

        return Boolean.TRUE.equals(success);
    }

    public void unlockSeat(
            Long showId,
            String seatNumber) {

        String key =
                "seat_lock:" + showId + ":" + seatNumber;

        redisTemplate.delete(key);
    }

    public boolean isSeatLocked(
            Long showId,
            String seatNumber) {

        String key =
                "seat_lock:" + showId + ":" + seatNumber;

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key)
        );
    }
}

