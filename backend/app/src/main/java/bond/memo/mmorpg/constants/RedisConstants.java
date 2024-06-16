package bond.memo.mmorpg.constants;

public final class RedisConstants {

    private RedisConstants() {
    }

    public static final String UPDATE_PLAYER_POSITION_SCRIPT = """
            local playerId = ARGV[1]
            local newX = tonumber(ARGV[2])
            local newY = tonumber(ARGV[3])
            local cellSize = tonumber(ARGV[4])
                
            local function getCellKey(x, y, cellSize)
                local cellX = math.floor(x / cellSize)
                local cellY = math.floor(y / cellSize)
                return 'grid:cell:' .. cellX .. ':' .. cellY
            end
                
            local oldCellKey = redis.call('hget', 'player:positions', playerId)
            local newCellKey = getCellKey(newX, newY, cellSize)
                
            if oldCellKey ~= newCellKey then
                redis.call('hset', 'player:positions', playerId, newCellKey)
                redis.call('zrem', oldCellKey, playerId)
            end
                
            redis.call('zadd', newCellKey, newX + newY * 10000, playerId)
            return 1
            """;
}
