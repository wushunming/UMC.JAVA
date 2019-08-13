package UMC.Data.Sql;


import UMC.Data.Utility;

import java.lang.reflect.Array;
import java.util.*;

class SqlParamer {

    //    static void AppendDictionary(System.Collections.IDictionary diction, String pfx)
//    {
//
//        diction["UMC.True"] = true;
//        diction["UMC.False"] = false;
//        diction["UMC.DateTime"] = new Date();//.Now;
//        diction["UMC.Id"] = .NewGuid();
//        diction["UMC.Prefix"] = pfx;
//        diction["UMC.GuidEmpty"] = System.uuid.Empty;
//        diction["UMC.GuidNull"] = System.uuid.Empty;
//        var user = UMC.Security.identity.current;
//        if (user == null)
//        {
//            diction["UMC.Username"] = String.Empty;
//
//        }
//        else
//        {
//            diction["UMC.UserId"] = user.Id;
//            diction["UMC.Username"] = user.name;
//        }
//
//    }
    public SqlParamer() {
        this.keys = new LinkedList<>();
    }

    static String[] works = new String[]{"FROM", "JOIN", "UPDATE", "INSERT", "INTO", "DELETE"};
    static String[] endWorks = new String[]{"ON", "WHERE", "ORDER", "GROUP"};
    DbProvider provider;
    ArrayList cmd;
    Object[] paramers;
    boolean autoPfx;
    String Prefixion = "";
    boolean isFormat = true;

    public static String Format(DbProvider provider, boolean autoPfx, ArrayList cmd, String sql, Map diction) {
        List<Object> parms = new LinkedList<>();
        SqlParamer pfx = new SqlParamer();
        Set<Map.Entry> entries = diction.entrySet();
        for (Map.Entry entry : entries) {
            if (entry.getKey() instanceof String) {
                String key = (String) entry.getKey();
                pfx.keys.add(key);
                parms.add(entry.getValue());
            }
        }

        return Format(pfx, provider, autoPfx, cmd, sql, parms.toArray());
    }

    public static String Format(DbProvider provider, boolean autoPfx, ArrayList cmd, String sql, Object[] paramers) {
        return Format(new SqlParamer(), provider, autoPfx, cmd, sql, paramers);
    }

    /// <summary>
    /// Sql命令进行格式化，把对应的ID={i}转化成对应的ID=?
    /// </summary>
    /// <param name="cmd"></param>
    /// <param name="provider">访问管理器</param>
    /// <param name="SQL">格式化的Sql文本</param>
    /// <param name="paramers">格式化的参数集</param>
    static String Format(SqlParamer pfx, DbProvider provider, boolean autoPfx, ArrayList cmd, String sql, Object[] paramers) {
        pfx.autoPfx = autoPfx;
        pfx.provider = provider;
        pfx.cmd = cmd;
        pfx.paramers = paramers;
        pfx.paramPfx = "A";
        String prefixion = provider.prefixion();
        if (Utility.isEmpty(prefixion) == false) {
            switch (provider.delimiter()) {
                case ".":
                    pfx.Prefixion = String.format("%s%s%s.%s", provider.quotePrefix(), prefixion, provider.quoteSuffix(), provider.quotePrefix());
                    break;
                default:
                    pfx.Prefixion = String.format("%s%s%s", provider.quotePrefix(), prefixion, provider.delimiter());
                    break;
            }
        } else {
            pfx.Prefixion = provider.quotePrefix();
        }
        return pfx.Do(sql).toString();
    }

    public static boolean isArray(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj.getClass().isArray();
    }

    List<String> keys;

    void Append(StringBuilder sql, Object value, String key) {
        if (isArray(value)) {
            if (value instanceof byte[]) {
                this.cmd.add(value);
                sql.append("?");
            } else {
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    if (i > 0) {

                        sql.append(',');
                    }
                    this.cmd.add(Array.get(value, i));

                    sql.append("?");

                }

            }
        } else {
            this.cmd.add(value);
            sql.append("?");
        }
    }


    boolean isFrom;
    boolean isPrefix;
    String sqlText;

    boolean check(String key) {
        for (int i = 0; i < works.length; i++)// (String k in works)
        {
            if (works[i].equalsIgnoreCase(key)) {

                isFrom = i == 0;
                return true;
            }
        }
        if (this.isFrom) {

            for (int i = 0; i < endWorks.length; i++)// (String k in works)
            {
                if (endWorks[i].equalsIgnoreCase(key)) {
                    this.isFrom = false;
                    return false;
                }
            }
        }
        return false;
    }

    void check(int start, int end, StringBuilder sql) {
        int b = start;
        if (start > 0) {
            start++;

        }
        if (end == 0) {
            return;
        }

        if (start > end) {
            return;
        }
        String value = sqlText.substring(start, end + 1).trim();
        if (check(value) == false) {
            if (this.isPrefix) {
                switch (value.charAt(0)) {
                    case '#':
                    case '(':
                        break;
                    default:
                        if (value.indexOf('.') == -1) {
                            sql.insert(sql.length() - value.length(), this.Prefixion);
                            if (value.startsWith("{pfx}")) {
                                int dindex = sql.length() - value.length();
                                sql.delete(dindex, dindex + 5);
                            }
                            if (this.isFormat) {
                                sql.append(this.provider.quoteSuffix());
                            }
                        } else {
                            if (Utility.isEmpty(this.Prefixion) == false) {
                                throw new NumberFormatException(value);
                            }
                        }
                        break;

                }
            }
            this.isPrefix = false;
        } else {
            this.isPrefix = true;
        }
    }

    String paramPfx;

    boolean check(StringBuilder sb, String value) {
        if (this.keys.size() == 0) {

            if ("pfx".equalsIgnoreCase(value) == false) {
                try {
                    int i = Integer.parseInt(value);
                    int dindex = sb.length() - 1 - value.length();
                    sb.delete(dindex, dindex + value.length() + 1);
                    this.Append(sb, this.paramers[i], paramPfx + value);
                    return true;
                } catch (NumberFormatException e) {

                }
            }
        }
        if (this.autoPfx == false) {
            if ("pfx".equalsIgnoreCase(value)) {
                int dindex = sb.length() - 1 - value.length();
                sb.delete(dindex, dindex + value.length() + 1);
                return true;

            }
        }
        if (this.keys.size() > 0) {
            if ("pfx".equalsIgnoreCase(value) == false) {
                int c = keyIndexOf(value);

                if (c > -1) {
                    int dindex = sb.length() - 1 - value.length();
                    sb.delete(dindex, dindex + value.length() + 1);

                    this.Append(sb, this.paramers[c], paramPfx + c);
                    return true;
                }
            }
        }
        return false;
    }

    int keyIndexOf(String name) {
        for (int i = 0, l = this.keys.size(); i < l; i++)// (String k in works)
        {
            if (keys.get(i).equalsIgnoreCase(name)) {

                return i;
            }
        }
        return -1;

    }

    StringBuilder Do(String strSql) {
        this.sqlText = strSql;
        StringBuilder sql = new StringBuilder();
        int i = 0, l = sqlText.length(), start = 0, end = 0, nstart = 0;


        while (i < l) {
            char k = sqlText.charAt(i);
            switch (k) {
                case '{':
                    end = nstart = i;
                    break;
                case '}':
                    if (nstart < end) {
                        if (this.check(sql, sqlText.substring(nstart + 1, end + 1))) {

                            end = i;
                            i++;
                            continue;
                        }
                    }
                    end = i;
                    break;
                case '(':

                    if (this.autoPfx) {
                        this.check(start, end, sql);
                        if (this.isFrom) {
                            this.isPrefix = this.isFrom = false;

                        }
                    }
                    end = start = i;
                    break;
                case ' ':
                case '\t':
                case '\b':
                case '\n':
                case '\r':
                case '*':
                case ')':
                    if (this.autoPfx) {
                        this.check(start, end, sql);
                    }
                    end = start = i;
                    break;
                case ',':
                    if (this.autoPfx) {
                        if (this.isFrom) {
                            this.check(start, end, sql);
                            this.isPrefix = true;
                            end = start = i;
                        } else {
                            end = i;
                        }
                    }
                    break;
                default:
                    end = i;
                    break;
            }
            i++;
            sql.append(k);
        }

        this.check(start, end, sql);
        return sql;

    }
}
