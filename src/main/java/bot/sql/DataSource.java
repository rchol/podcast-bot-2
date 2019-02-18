package bot.sql;

import java.sql.ResultSet;

public interface DataSource {

    ResultSet executeQuery(String query);

    long executeUpdate(String query, Object... params);

    long executeUpdate(String query);


}
