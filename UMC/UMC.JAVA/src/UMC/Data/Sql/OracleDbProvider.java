package UMC.Data.Sql;

public class OracleDbProvider extends DbProvider {
    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String quotePrefix() {
        return "\"";
    }

    @Override
    public String quoteSuffix() {
        return "\"";
    }

    @Override
    public String paginationText(int start, int limit, String selectText) {
        return String.format("SELECT * FROM (SELECT A.*, ROWNUM R__ FROM (%s) A WHERE ROWNUM <= %d)WHERE R__ > %d ", selectText, start + limit, start);

    }
}
