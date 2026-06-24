package com.reservex.inventory.service.impl;

import com.reservex.inventory.service.SeatLockRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatLockRedisServiceImpl
        implements SeatLockRedisService {

    private final StringRedisTemplate redisTemplate;

    private static final Duration LOCK_TTL =
            Duration.ofMinutes(5);

    private String buildKey(
            UUID showId,
            UUID seatId
    ) {
        return "seat-lock:" +
                showId +
                ":" +
                seatId;
    }

    @Override
    public boolean lockSeat(
            UUID showId,
            UUID seatId,
            UUID userId
    ) {

        String key =
                buildKey(showId, seatId);

        Boolean success =
                redisTemplate.opsForValue()
                        .setIfAbsent(
                                key,
                                userId.toString(),
                                LOCK_TTL
                        );

        return Boolean.TRUE.equals(success);
    }

    @Override
    public void unlockSeat(
            UUID showId,
            UUID seatId
    ) {

        redisTemplate.delete(
                buildKey(showId, seatId)
        );
    }

    @Override
    public boolean isLocked(
            UUID showId,
            UUID seatId
    ) {

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(
                        buildKey(showId, seatId)
                )
        );
    }

    @Override
    public String getLockOwner(
            UUID showId,
            UUID seatId
    ) {

        return redisTemplate.opsForValue()
                .get(
                        buildKey(showId, seatId)
                );
    }
}