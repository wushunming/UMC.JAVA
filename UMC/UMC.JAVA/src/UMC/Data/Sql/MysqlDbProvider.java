package UMC.Data.Sql;

public class MysqlDbProvider extends DbProvider {
    static {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String quotePrefix() {
        return "`";
    }

    @Override
    public String quoteSuffix() {
        return "`";
    }

    @Override
    public String paginationText(int start, int limit, String selectText) {
        return String.format("%s limit %d,%d", selectText, start, limit);

    }
}
