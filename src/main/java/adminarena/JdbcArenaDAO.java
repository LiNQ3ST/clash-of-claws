/**
 * @author Nabiha Fatima
 * @version 0.1.0
 * @since 8/4/2026
 */
package adminarena;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcArenaDAO implements ArenaDAO {

    private final DataSource dataSource;

    public JdbcArenaDAO(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException(
                    "DataSource cannot be null."
            );
        }

        this.dataSource = dataSource;
    }

    @Override
    public Arena insert(Arena arena) throws SQLException {
        validateArena(arena);

        String sql = """
                INSERT INTO arenas (
                    arena_name,
                    town_name,
                    opponent_cat_id,
                    difficulty,
                    reward_amount,
                    active
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {
            statement.setString(
                    1,
                    arena.getArenaName()
            );

            statement.setString(
                    2,
                    arena.getTownName()
            );

            statement.setInt(
                    3,
                    arena.getOpponentCatId()
            );

            statement.setString(
                    4,
                    arena.getDifficulty()
            );

            statement.setInt(
                    5,
                    arena.getRewardAmount()
            );

            statement.setBoolean(
                    6,
                    arena.isActive()
            );

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Arena insert did not affect exactly one row."
                );
            }

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    arena.setArenaId(
                            generatedKeys.getInt(1)
                    );
                } else {
                    arena.setArenaId(
                            getLastInsertedId(connection)
                    );
                }
            }

            return arena;
        }
    }

    @Override
    public Optional<Arena> findById(int arenaId)
            throws SQLException {

        String sql = """
                SELECT
                    arena_id,
                    arena_name,
                    town_name,
                    opponent_cat_id,
                    difficulty,
                    reward_amount,
                    active
                FROM arenas
                WHERE arena_id = ?
                """;

        try (
                Connection connection =
                        dataSource.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, arenaId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapRow(resultSet)
                    );
                }

                return Optional.empty();
            }
        }
    }

    @Override
    public List<Arena> findAll() throws SQLException {
        String sql = """
                SELECT
                    arena_id,
                    arena_name,
                    town_name,
                    opponent_cat_id,
                    difficulty,
                    reward_amount,
                    active
                FROM arenas
                ORDER BY arena_id
                """;

        List<Arena> arenas = new ArrayList<>();

        try (
                Connection connection =
                        dataSource.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                arenas.add(
                        mapRow(resultSet)
                );
            }
        }

        return arenas;
    }

    @Override
    public boolean update(Arena arena)
            throws SQLException {

        validateArena(arena);

        if (arena.getArenaId() == null) {
            throw new IllegalArgumentException(
                    "Arena ID is required for update."
            );
        }

        String sql = """
                UPDATE arenas
                SET
                    arena_name = ?,
                    town_name = ?,
                    opponent_cat_id = ?,
                    difficulty = ?,
                    reward_amount = ?,
                    active = ?
                WHERE arena_id = ?
                """;

        try (
                Connection connection =
                        dataSource.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    arena.getArenaName()
            );

            statement.setString(
                    2,
                    arena.getTownName()
            );

            statement.setInt(
                    3,
                    arena.getOpponentCatId()
            );

            statement.setString(
                    4,
                    arena.getDifficulty()
            );

            statement.setInt(
                    5,
                    arena.getRewardAmount()
            );

            statement.setBoolean(
                    6,
                    arena.isActive()
            );

            statement.setInt(
                    7,
                    arena.getArenaId()
            );

            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int arenaId)
            throws SQLException {

        String sql = """
                DELETE FROM arenas
                WHERE arena_id = ?
                """;

        try (
                Connection connection =
                        dataSource.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, arenaId);

            return statement.executeUpdate() == 1;
        }
    }

    private Arena mapRow(ResultSet resultSet)
            throws SQLException {

        return new Arena(
                resultSet.getInt("arena_id"),
                resultSet.getString("arena_name"),
                resultSet.getString("town_name"),
                resultSet.getInt("opponent_cat_id"),
                resultSet.getString("difficulty"),
                resultSet.getInt("reward_amount"),
                resultSet.getBoolean("active")
        );
    }

    private int getLastInsertedId(
            Connection connection
    ) throws SQLException {

        String sql = """
                SELECT last_insert_rowid()
                """;

        try (
                Statement statement =
                        connection.createStatement();

                ResultSet resultSet =
                        statement.executeQuery(sql)
        ) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            throw new SQLException(
                    "Could not retrieve generated arena ID."
            );
        }
    }

    private void validateArena(Arena arena) {
        if (arena == null) {
            throw new IllegalArgumentException(
                    "Arena cannot be null."
            );
        }

        if (
                arena.getArenaName() == null
                        || arena.getArenaName().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Arena name is required."
            );
        }

        if (
                arena.getTownName() == null
                        || arena.getTownName().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Town name is required."
            );
        }

        if (arena.getOpponentCatId() == null) {
            throw new IllegalArgumentException(
                    "Opponent cat ID is required."
            );
        }

        if (
                arena.getDifficulty() == null
                        || arena.getDifficulty().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Difficulty is required."
            );
        }

        if (arena.getRewardAmount() < 0) {
            throw new IllegalArgumentException(
                    "Reward amount cannot be negative."
            );
        }
    }
}