/**
 * @author Nabiha Fatima
 * @version 0.1.0
 * @since 8/4/2026
 */
package adminarena;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ArenaDAO {

    Arena insert(Arena arena) throws SQLException;

    Optional<Arena> findById(int arenaId) throws SQLException;

    List<Arena> findAll() throws SQLException;

    boolean update(Arena arena) throws SQLException;

    boolean delete(int arenaId) throws SQLException;
}