package com.konyaco.keeptally.storage

// Generate snowflake id
object SnowFlakeIDGenerator {
    private const val EPOCH = 1609459200000L
    private const val WORKER_ID_BITS = 5
    private const val DATACENTER_ID_BITS = 5
    private const val SEQUENCE_BITS = 12
    private const val WORKER_ID_SHIFT = SEQUENCE_BITS
    private const val DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS
    private const val TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS
    private const val SEQUENCE_MASK = (-1L shl SEQUENCE_BITS).inv()
    private const val WORKER_ID_MAX = (-1L shl WORKER_ID_BITS).inv()
    private const val DATACENTER_ID_MAX = (-1L shl DATACENTER_ID_BITS).inv()
    private const val WORKER_ID = 0L
    private const val DATACENTER_ID = 0L
    private var lastTimestamp = -1L
    private var sequence = 0L

    @Synchronized
    fun nextId(): Long {
        var timestamp = System.currentTimeMillis()
        if (timestamp < lastTimestamp) {
            throw RuntimeException("Clock moved backwards. Refusing to generate id for ${lastTimestamp - timestamp} milliseconds")
        }
        if (lastTimestamp == timestamp) {
            sequence = sequence + 1 and SEQUENCE_MASK
            if (sequence == 0L) {
                timestamp = tilNextMillis(lastTimestamp)
            }
        } else {
            sequence = 0L
        }
        lastTimestamp = timestamp
        return timestamp - EPOCH shl TIMESTAMP_LEFT_SHIFT or (DATACENTER_ID and DATACENTER_ID_MAX shl DATACENTER_ID_SHIFT) or (WORKER_ID and WORKER_ID_MAX shl WORKER_ID_SHIFT) or sequence
    }

    private fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp = System.currentTimeMillis()
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis()
        }
        return timestamp
    }
}