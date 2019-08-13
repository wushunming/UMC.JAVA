package UMC.Data.Sql;

import UMC.Data.DataProvider;
import UMC.Data.Utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DbProvider extends DataProvider {


    public Connection instance() throws SQLException {

            return DriverManager.getConnection(this.conntionString(), this.user(), this.password());

    }

    public String year(String feild) {
        return feild;

    }

    public String month(String feild) {

        return feild;
    }

    public String day(String feild) {

        return feild;
    }

    public String quarter(String feild) {

        return feild;
    }

    public String minute(String feild) {
        return feild;

    }

    public String hour(String feild) {

        return feild;
    }

    public String week(String feild) {

        return feild;
    }


    /**数据联接字符串
     * @return
     */
    public String conntionString()

    {
        return this.provider.get("conString");

    }


    /**实体映射表名前缀
     * @return
     */
    public String prefixion() {

        String Prefix = "";
        if (this.provider != null) {
            Prefix = this.provider.get("Prefix");
            if (Utility.isEmpty(Prefix)) {
                return "";
            }
        }
        return Prefix;
    }

    public String user() {
        return this.provider.get("user");
    }

    public String password() {
        return this.provider.get("password");
    }



    /**实体映射表名前缀分割符
     * @return
     */
    public String delimiter()

    {
        String Delimiter = "";
        if (this.provider != null) {
            Delimiter = this.provider.attributes().get("delimiter");
            if (!Utility.isEmpty(Delimiter)) {

                return Delimiter;
            }
        }


        return ".";
    }

    /**指定其名称包含空格或保留标记等字符的数据库对象（例如，表或列）时使用的起始字符
     * @return
     */
    public abstract String quotePrefix();

    /**指定其名称包含空格或保留标记等字符的数据库对象（例如，表或列）时使用的结束字符
     * @return
     */
    public abstract String quoteSuffix();



    /**合成分页的Sql格式脚本
     * @param start 开始记录数
     * @param limit 后面的条数
     * @param selectText 查询的Sql脚本
     * @return 带分页SQL脚本
     */
    public abstract String paginationText(int start, int limit, String selectText);
}
