package UMC.Data.Sql;

import UMC.Data.Utility;

public class SQLIteDBProvider extends DbProvider {
    static {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String conntionString() {
        String file = Utility.isNull(this.provider.get("db"), "umc.db");

        return "jdbc:sqlite:" + Utility.mapPath("~/App_Data/" + file);

    }

    @Override
    public String quotePrefix() {
        return "[";
    }

    @Override
    public String quoteSuffix() {
        return "]";
    }

    @Override
    public String paginationText(int start, int limit, String selectText) {
        return String.format("%s limit %d,%d", selectText, start, limit);

    }
}
