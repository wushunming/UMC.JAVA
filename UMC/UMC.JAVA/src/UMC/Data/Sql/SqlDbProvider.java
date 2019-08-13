package UMC.Data.Sql;

public class SqlDbProvider extends DbProvider {
    static {

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String year(String feild) {
        return String.format("DATEPART(yy,%s)", feild);
    }

    @Override
    public String month(String feild) {
        return String.format("DATEPART(mm,%s)", feild);
    }

    @Override
    public String day(String feild) {
        return String.format("DATEPART(dd,%s)", feild);
    }

    @Override
    public String hour(String feild) {
        return String.format("DATEPART(hh,%s)", feild);
    }

    @Override
    public String week(String feild) {
        return String.format("DATEPART(dw,%s)", feild);
    }

    @Override
    public String minute(String feild) {
        return String.format("DATEPART(mi,%s)", feild);
    }

    @Override
    public String delimiter() {
        return super.delimiter();
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
        StringBuilder sb = new StringBuilder(selectText);
        int topIndex = selectText.toLowerCase().indexOf("select") + 6;

        if (start > 0) {
            ;

            sb.insert(topIndex, String.format(" TOP %d ", start + limit));
            sb.insert(0, "SELECT IDENTITY(INT,0,1) AS __WDK_Page_ID , WebADNukePagge.* INTO #__WebADNukePagges FROM(");

            sb.append(") AS WebADNukePagge");
            sb.append("\n");
            sb.append("SELECT *FROM  #__WebADNukePagges  WHERE __WDK_Page_ID >=");
            sb.append(start);
            sb.append("\n");

            sb.append("DROP TABLE #__WebADNukePagges");

        } else {
            sb.insert(topIndex, String.format(" TOP %d", limit));
        }
        return sb.toString();

    }
}
