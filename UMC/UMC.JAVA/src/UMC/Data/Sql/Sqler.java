package UMC.Data.Sql;

import UMC.Data.Database;
import UMC.Data.JSON;

import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.*;

class Sqler implements ISqler {
    public DbProvider provider;
    private boolean autoPfx;
    Database.IProgress progress;

    public Sqler(Database.IProgress progress, DbProvider provider, boolean autoPfx) {
        this.provider = provider;
        this.autoPfx = autoPfx;
        this.progress = progress;
    }


    private Script script;

    private int Progress(String sqlText, Object... paramers) {
        ArrayList cmd = new ArrayList();

        this.script = Script.create(sqlText, paramers);
        String sql = null;
        switch (paramers.length) {
            case 1:
                if (paramers[0] instanceof Map) {

                    sql = SqlParamer.Format(this.provider, this.autoPfx, cmd, sqlText, (Map) paramers[0]);

                } else {
                    sql = SqlParamer.Format(this.provider, this.autoPfx, cmd, sqlText, paramers);
                }

                break;
            default:
                sql = SqlParamer.Format(this.provider, this.autoPfx, cmd, sqlText, paramers);
                break;
        }
        final int[] rows = {-1};
        String finalSql = sql;
        progress.execute(sql, (Connection connection) -> {
            PreparedStatement pstmt = null;
            try {
                pstmt = connection.prepareStatement(finalSql);

                appendParamers(pstmt, cmd);
                rows[0] = pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (pstmt != null)
                        pstmt.close();
                } catch (SQLException e) {
                }
            }

        });

        return rows[0];


    }

    private void Progress(IResultReader reader, String sqlText, Object... paramers) {
        ArrayList cmd = new ArrayList();
        this.script = Script.create(sqlText, paramers);
        String sql = null;
        switch (paramers.length) {
            case 1:
                if (paramers[0] instanceof Map) {

                    sql = SqlParamer.Format(this.provider, this.autoPfx, cmd, sqlText, (Map) paramers[0]);

                } else {
                    sql = SqlParamer.Format(this.provider, this.autoPfx, cmd, sqlText, paramers);
                }

                break;
            default:
                sql = SqlParamer.Format(this.provider, this.autoPfx, cmd, sqlText, paramers);
                break;
        }

        String finalSql = sql;
        progress.execute(sql, (Connection connection) -> {
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = connection.prepareStatement(finalSql);

                appendParamers(pstmt, cmd);
                rs = pstmt.executeQuery();
                reader.reader(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                    if (pstmt != null)
                        pstmt.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        });


    }

    private void appendParamers(PreparedStatement preparedStatement, ArrayList arrayList) throws SQLException {
        for (int i = 0; i < arrayList.size(); i++) {
            Object v = arrayList.get(i);
            if (v == null) {
                preparedStatement.setNull(i + 1, Types.NULL);
            } else if (v instanceof java.util.Date) {

                preparedStatement.setDate(i + 1, new java.sql.Date(((java.util.Date) v).getTime()));
            } else if (v instanceof UUID) {

                preparedStatement.setString(i + 1, v.toString());

            } else if (v instanceof Boolean) {

                preparedStatement.setBoolean(i + 1, (Boolean) v);

            } else if (v instanceof Map) {
                java.io.StringWriter writer = new StringWriter();
                JSON.serialize(v, writer);

                preparedStatement.setString(i + 1, writer.toString());
            } else {

                preparedStatement.setObject(i + 1, v);
            }
        }
    }

    /**
     * @param sqlText  格式化执行的SQL脚本
     * @param paramers SQL参数
     * @return 返回影响行数
     */
    @Override
    public int executeNonQuery(String sqlText, Object... paramers) {

        return Progress(sqlText, paramers);
    }


    /**
     * 执行SQL返回第一行第一列
     *
     * @param sqlText  格式化执行的SQL脚本
     * @param paramers 在
     * @return
     */
    @Override
    public Object executeScalar(String sqlText, Object... paramers) {
        Object[] value = {null};
        Progress((ResultSet resultSet) -> {
            try {
                if (resultSet.next()) {
                    value[0] = resultSet.getObject(1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }, sqlText, paramers);
        return value[0];
    }


    /**
     * 执行SQLMAP
     *
     * @param sqlText  格式化执行的SQL脚本
     * @param paramers 在
     * @return
     */
    @Override
    public List<Map> execute(String sqlText, Object... paramers) {

        List<Map> value = new LinkedList<>();
        Progress((ResultSet resultSet) -> {
            List<String> fields = new LinkedList<>();
            try {

                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                for (int i = 1, l = resultSetMetaData.getColumnCount(); i <= l; i++) {
                    fields.add(resultSetMetaData.getCatalogName(i));
                }

                while (resultSet.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    for (int i = 1, l = fields.size(); i <= l; i++) {

                        item.put(fields.get(i - 1), resultSet.getObject(i));
                    }
                    value.add(item);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }, sqlText, paramers);
        return value;
    }


    @Override
    public List<Map> execute(String sqlText, int start, int limit, Object... paramers) {

        sqlText = this.provider.paginationText(start, limit, sqlText);
        List<Map> value = new LinkedList<>();
        Progress((ResultSet resultSet) -> {
            List<String> fields = new LinkedList<>();
            try {

                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                for (int i = 1, l = resultSetMetaData.getColumnCount(); i <= l; i++) {
                    fields.add(resultSetMetaData.getCatalogName(i));
                }

                while (resultSet.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    for (int i = 1, l = fields.size(); i <= l; i++) {

                        item.put(fields.get(i - 1), resultSet.getObject(i));
                    }
                    value.add(item);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }, sqlText, paramers);
        return value;
    }


    @Override
    public Map executeSingle(String sqlText, Object... paramers) {

        Map[] value = new Map[]{null};
        Progress((ResultSet resultSet) -> {
            List<String> fields = new LinkedList<>();
            try {

                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                for (int i = 1, l = resultSetMetaData.getColumnCount(); i <= l; i++) {
                    fields.add(resultSetMetaData.getCatalogName(i));
                }

                if (resultSet.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    for (int i = 1, l = fields.size(); i <= l; i++) {

                        item.put(fields.get(i - 1), resultSet.getObject(i));
                    }
                    value[0] = item;

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }, sqlText, paramers);
        return value[0];
    }


    @Override
    public <T> T executeSingle(Class<T> tClass, String sqlText, Object... paramers) {

        List<T> value = new LinkedList<>();
        Progress((ResultSet resultSet) -> {
            try {


                if (resultSet.next()) {
                    EntityHelper helper = new EntityHelper(provider, tClass);


                    Constructor constructor = tClass.getDeclaredConstructor();//[0];
                    constructor.setAccessible(true);

                    T c = (T) constructor.newInstance();
                    helper.CreateObject(c, resultSet);

                    value.add(c);

                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }


        }, sqlText, paramers);

        return value.size() > 0 ? value.get(0) : null;
    }


    @Override
    public <T> List<T> execute(Class<T> tClass, String sqlText, Object... paramers) {

        List<T> value = new LinkedList<>();
        Progress((ResultSet resultSet) -> {
            try {

                EntityHelper helper = new EntityHelper(provider, tClass);

                while (resultSet.next()) {
                    Constructor constructor = tClass.getDeclaredConstructor();//[0];
                    constructor.setAccessible(true);

                    T c = (T) constructor.newInstance();
                    helper.CreateObject(c, resultSet);
                    value.add(c);


                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }

        }, sqlText, paramers);

        return value;
    }


    @Override
    public void execute(String sqlText, IResultReader reader, Object... paramers) {
        Progress(reader, sqlText, paramers);
    }

    @Override
    public <T> void execute(Class<T> tClass, String sqlText, IDataReader<T> reader, Object... paramers) {

        Progress((ResultSet resultSet) -> {
            try {

                EntityHelper helper = new EntityHelper(provider, tClass);

                while (resultSet.next()) {
                    Constructor constructor = tClass.getDeclaredConstructor();//[0];
                    constructor.setAccessible(true);
                    T c = (T) constructor.newInstance();
                    helper.CreateObject(c, resultSet);

                    reader.reader(c);

                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }


        }, sqlText, paramers);


    }

    @Override
    public void execute(String sqlText, int start, int limit, IResultReader reader, Object... paramers) {

        sqlText = this.provider.paginationText(start, limit, sqlText);
        Progress(reader, sqlText, paramers);
    }

    @Override
    public void execute(Script... scripts) {


        progress.execute("insert", (Connection connection) -> {

            boolean isAuto = true;
            try {

                isAuto = connection.getAutoCommit();
                if (isAuto)
                    connection.setAutoCommit(false);

                for (Script sc : scripts) {
                    ArrayList cmd = new ArrayList();
                    String sql = null;
                    Object[] args = sc.arguments();
                    switch (args.length) {
                        case 1:
                            if (args[0] instanceof Map) {

                                sql = SqlParamer.Format(provider, autoPfx, cmd, sc.text(), (Map) args[0]);

                            } else {
                                sql = SqlParamer.Format(provider, autoPfx, cmd, sc.text(), args);
                            }

                            break;
                        default:
                            sql = SqlParamer.Format(provider, autoPfx, cmd, sc.text(), args);
                            break;
                    }

                    PreparedStatement pstmt = connection.prepareStatement(sql);

                    appendParamers(pstmt, cmd);
                    pstmt.execute();

                    pstmt.close();

                }
                if (isAuto)
                    connection.commit();
            } catch (SQLException e) {

                try {
                    if (isAuto)
                        connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                throw new RuntimeException(e);
            }

        });

    }


    @Override
    public <T> List<T> execute(Class<T> tClass, String sqlText, int start, int limit, Object... paramers) {

        List<T> value = new LinkedList<>();

        sqlText = this.provider.paginationText(start, limit, sqlText);
        Progress((ResultSet resultSet) -> {
            try {

                EntityHelper helper = new EntityHelper(provider, tClass);

                while (resultSet.next()) {
                    Constructor constructor = tClass.getDeclaredConstructor();//[0];
                    constructor.setAccessible(true);
                    T c = (T) constructor.newInstance();
                    helper.CreateObject(c, resultSet);

                    value.add(c);

                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }


        }, sqlText, paramers);

        return value;//.size() > 0 ? value.get(0) : null;
    }

    @Override
    public <T> void execute(String sqlText, int start, int limit, IDataReader<T> reader, Object... paramers) {
        ParameterizedType ctype = (ParameterizedType) reader.getClass().getGenericSuperclass();

        Class tClass = (Class) ctype.getActualTypeArguments()[0];

        sqlText = this.provider.paginationText(start, limit, sqlText);
        Progress((ResultSet resultSet) -> {
            try {

                EntityHelper helper = new EntityHelper(provider, tClass);


                while (resultSet.next()) {

                    Constructor constructor = tClass.getDeclaredConstructor();//[0];
                    constructor.setAccessible(true);
                    T c = (T) constructor.newInstance();
                    helper.CreateObject(c, resultSet);
                    reader.reader(c);


                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }


        }, sqlText, paramers);
    }


    @Override
    public Script SQL() {
        return script;
    }
}
