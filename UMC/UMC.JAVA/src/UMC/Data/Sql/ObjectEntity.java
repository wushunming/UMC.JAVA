package UMC.Data.Sql;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;

class ObjectEntity<T> implements IObjectEntity<T> {

    ConditionsWhere<T> cond;
    DbProvider db;
    Sequencer2<T> seq;
    EntityHelper SqlHelper;
    ISqler sqler;
    Class<T> tClass;

    public ObjectEntity(ISqler sqler, DbProvider dbCommonProvider, Class<T> tClass) {

        this.tClass = tClass;

        this.db = dbCommonProvider;
        this.cond = new ConditionsWhere<T>(this);
        this.seq = new Sequencer2<T>(this);
        this.SqlHelper = new EntityHelper(this.db, tClass);
        this.sqler = sqler;
    }

    public Script script(T field) {
        StringBuilder sb = new StringBuilder(this.SqlHelper.CreateSelectText(field));
        List<Object> lp = cond.FormatSqlText(sb, new ArrayList<>());
        seq.FormatSqlText(sb);
        return Script.create(sb.toString(), lp.toArray());
    }

    public Script script(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(field);
        sb.append(" FROM ");
        sb.append(this.SqlHelper.TableInfo);
        sb.append(" ");

        List<Object> lp = cond.FormatSqlText(sb, new ArrayList<>());
        seq.FormatSqlText(sb);
        return Script.create(sb.toString(), lp.toArray());
    }


    public IGrouper<T> groupBy(String... fields) {
        return new Grouper<T>((Sqler) this.sqler, this.SqlHelper, this.cond, fields);
    }

    public IGrouper<T> groupBy(T field) {
        Map<String, Object> dic = SqlUtils.fieldMap(field);
        Iterator<String> em = dic.keySet().iterator();
        List<String> fields = new LinkedList<>();
        while (em.hasNext()) {
            fields.add(em.next());


        }
        String[] arrs = new String[fields.size()];
        fields.toArray(arrs);

        return new Grouper<T>((Sqler) this.sqler, this.SqlHelper, this.cond, arrs);
    }


    public int insert(T... items) {

        if (items.length > 0) {
            Script[] scripts = new Script[items.length];
            for (int i = 0; i < items.length; i++) {
                String sqlText = this.SqlHelper.CreateInsertText(items[i]);
                scripts[i] = Script.create(sqlText, this.SqlHelper.Arguments.toArray());
            }


            this.sqler.execute(scripts);
        }

        return items.length;
    }


    public int update(T item, String... fields) {
        StringBuilder sb = new StringBuilder(this.SqlHelper.CreateUpdateText("", item, fields));
        List<Object> list = new LinkedList<>();

        list.addAll(this.SqlHelper.Arguments);

        cond.FormatSqlText(sb, list);

        return this.sqler.executeNonQuery(sb.toString(), list.toArray());
    }

    public int update(String format, T item, String... fields) {
        StringBuilder sb = new StringBuilder(this.SqlHelper.CreateUpdateText(format, item, fields));
        List<Object> list = new LinkedList<>();

        list.addAll(this.SqlHelper.Arguments);

        cond.FormatSqlText(sb, list);

        return this.sqler.executeNonQuery(sb.toString(), list.toArray());

    }


    /// <summary>
    /// 采用字典更新实体
    /// </summary>
    /// <param name="fieldValues">字段字典对</param>
    /// <returns></returns>
    public int update(Map fieldValues) {
        StringBuilder sb = new StringBuilder(this.SqlHelper.CreateUpdateText("", fieldValues));
        List<Object> list = new LinkedList<>();

        list.addAll(this.SqlHelper.Arguments);

        cond.FormatSqlText(sb, list);

        return this.sqler.executeNonQuery(sb.toString(), list.toArray());

    }

    public int update(String format, Map fieldValues) {
        StringBuilder sb = new StringBuilder(this.SqlHelper.CreateUpdateText(format, fieldValues));
        List<Object> list = new LinkedList<>();

        list.addAll(this.SqlHelper.Arguments);

        cond.FormatSqlText(sb, list);

        return this.sqler.executeNonQuery(sb.toString(), list.toArray());
    }

    /// <summary>
    /// 删除
    /// </summary>
    /// <returns></returns>
    public int delete() {

        StringBuilder sb = new StringBuilder(this.SqlHelper.CreateDeleteText());
        List<Object> list = new LinkedList<>();

        list.addAll(this.SqlHelper.Arguments);

        cond.FormatSqlText(sb, list);
        return this.sqler.executeNonQuery(sb.toString(), list.toArray());

    }

    /// <summary>
    /// 排序
    /// </summary>
    public IOrder<T> order() {
        return seq;
    }

    /// <summary>
    /// 查询一个字段，如果是“*”，必返回DataRow[]数据
    /// </summary>
    /// <returns></returns>
    public Object[] query(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(field);
        sb.append(" FROM ");
        sb.append(this.SqlHelper.TableInfo);
        sb.append(" ");
        List<Object> args = cond.FormatSqlText(sb, new ArrayList<>());
        seq.FormatSqlText(sb);

        if (field.trim().equalsIgnoreCase("*")) {
            List<Map> rows = sqler.execute(sb.toString(), args.toArray());
            return rows.toArray();
        }
        List<Object> objs = new ArrayList<>();
        this.sqler.execute(sb.toString(), new IResultReader() {
            @Override
            public void reader(ResultSet resultSet) {
                try {
                    while (resultSet.next()) {

                        objs.add(resultSet.getObject(1));

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }, args.toArray());
        return objs.toArray();

    }

    public void query(String field, IResultReader dr) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(field);
        sb.append(" FROM ");
        sb.append(this.SqlHelper.TableInfo);
        sb.append(" ");
        List<Object> args = cond.FormatSqlText(sb, new ArrayList<>());
        seq.FormatSqlText(sb);

        this.sqler.execute(sb.toString(), dr, args.toArray());

    }

    /// <summary>
    /// 查询
    /// </summary>
    /// <returns></returns>
    public void query(IDataReader<T> dr) {
        this.query(null, dr);

    }

    /// <summary>
    /// 查询
    /// </summary>
    /// <param name="field">字段实例</param>
    /// <returns></returns>
    public void query(T field, IDataReader<T> dr) {

        StringBuilder sb = new StringBuilder();
        sb.append(this.SqlHelper.CreateSelectText(field));
        List<Object> args = cond.FormatSqlText(sb, new ArrayList<>());

        seq.FormatSqlText(sb);
        this.sqler.execute(this.tClass, sb.toString(), dr, args.toArray());

    }

    public T[] query() {
        List<T> items = new LinkedList<>();
        this.query(null, new IDataReader<T>() {
            @Override
            public void reader(T resultSet) {
                items.add(resultSet);

            }
        });
        T[] ts = (T[]) Array.newInstance(this.tClass, 0);
        return items.toArray(ts);
    }

    /// <summary>
    /// 查询实段实例
    /// </summary>
    /// <param name="field">字段实例</param>
    /// <returns></returns>
    public T[] query(T field) {
        List<T> items = new LinkedList<>();
        this.query(field, new IDataReader<T>() {
            @Override
            public void reader(T resultSet) {
                items.add(resultSet);

            }
        });
        T[] ts = (T[]) Array.newInstance(this.tClass, 0);
        return items.toArray(ts);
//        return (T[]) items.toArray();
    }

    /// <summary>
    /// 查询实体集分页
    /// </summary>
    /// <param name="start">开始记录</param>
    /// <param name="limit">记录数</param>
    public void query(int start, int limit, IDataReader<T> dr) {

        if (limit <= 1) {
            throw new IllegalArgumentException("limit必须>1");
        }
        if (start < 0) {
            throw new IllegalArgumentException("start必须不小于0");
        }
        StringBuilder sb = new StringBuilder(this.SqlHelper.CreateSelectText(null));
        List<Object> lp = cond.FormatSqlText(sb, new ArrayList<>());

        seq.FormatSqlText(sb);

        this.sqler.execute(sb.toString(), start, limit, dr, lp.toArray());

    }

    /// <summary>
    /// 查询实体集分页
    /// </summary>
    /// <param name="start">开始记录</param>
    /// <param name="limit">记录数</param>
    public T[] query(int start, int limit) {
        return query(null, start, limit);
    }

    /// <summary>
    /// 查询实体集分页
    /// </summary>
    /// <param name="field">字段实例</param>
    /// <param name="start">开始记录</param>
    /// <param name="limit">记录数</param>
    /// <returns></returns>
    public T[] query(T field, int start, int limit) {

        if (limit <= 1) {
            throw new IllegalArgumentException("limit必须>1");
        }
        if (start < 0) {
            throw new IllegalArgumentException("start必须不小于0");
        }
        StringBuilder sb = new StringBuilder(this.SqlHelper.CreateSelectText(field));
        List<Object> lp = cond.FormatSqlText(sb, new ArrayList<>());

        seq.FormatSqlText(sb);
        T[] ts = (T[]) Array.newInstance(this.tClass, 0);


        return this.sqler.execute(tClass, sb.toString(), start, limit, lp.toArray()).toArray(ts);
    }

    /// <summary>
    /// 查询实体集分页
    /// </summary>
    /// <param name="field">字段实例</param>
    /// <param name="start">开始记录</param>
    /// <param name="limit">记录数</param>
    /// <returns></returns>
    public void query(T field, int start, int limit, IDataReader<T> dr) {

        if (limit <= 1) {
            throw new IllegalArgumentException("limit必须>1");
        }
        if (start < 0) {
            throw new IllegalArgumentException("start必须不小于0");
        }
        StringBuilder sb = new StringBuilder(this.SqlHelper.CreateSelectText(field));
        List<Object> lp = cond.FormatSqlText(sb, new ArrayList<>());

        seq.FormatSqlText(sb);

        this.sqler.execute(sb.toString(), start, limit, dr, lp.toArray());

    }

    /// <summary>
    /// 查询头一个实体
    /// </summary>
    /// <returns></returns>
    public T single() {
        StringBuilder sb = new StringBuilder(this.SqlHelper.CreateSelectText(null));
        List<Object> lp = cond.FormatSqlText(sb, new ArrayList<>());

        seq.FormatSqlText(sb);
        //  this.script = script.create(sb.toString(), lp.toArray());
        return this.sqler.executeSingle(tClass, sb.toString(), lp.toArray());

    }
    public T single(T field) {
        StringBuilder sb = new StringBuilder(this.SqlHelper.CreateSelectText(field));
        List<Object> lp = cond.FormatSqlText(sb, new ArrayList<>());

        seq.FormatSqlText(sb);

        return this.sqler.executeSingle(tClass, sb.toString(), lp.toArray());

    }

    /// <summary>
    /// 查询一个字段，如果是“*”则返回一行记录的字典对
    /// </summary>
    /// <returns></returns>
    public Object single(String field) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(field);
        sb.append(" FROM ");
        sb.append(this.SqlHelper.TableInfo);
        sb.append(" ");
        List<Object> args = cond.FormatSqlText(sb, new ArrayList<>());
        seq.FormatSqlText(sb);

        if (field.trim().equalsIgnoreCase("*")) {
            return sqler.executeSingle(sb.toString(), args.toArray());
        }
        return sqler.executeScalar(sb.toString(), args.toArray());


    }

    /// <summary>
    /// 查询一个字段，返回一行记录的字典对
    /// </summary>
    /// <param name="field"></param>
    /// <returns></returns>
    public Map single(String... field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(String.join(",", field));
        sb.append(" FROM ");
        sb.append(this.SqlHelper.TableInfo);
        sb.append(" ");
        List<Object> args = cond.FormatSqlText(sb, new ArrayList<>());
        seq.FormatSqlText(sb);
        return sqler.executeSingle(sb.toString(), args.toArray());

    }

    /// <summary>
    /// 求记录的个数
    /// </summary>
    /// <returns></returns>
    public int count() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) FROM ");
        sb.append(this.SqlHelper.TableInfo);
        sb.append(" ");
        List<Object> args = cond.FormatSqlText(sb, new ArrayList<>());
//        seq.FormatSqlText(sb);

//        if (field.trim().equalsIgnoreCase("*")) {
//            return sqler.executeSingle(sb.toString(), args.toArray());
//        }
        return (int) sqler.executeScalar(sb.toString(), args.toArray());
    }

    /// <summary>
    /// 求和
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    public Object sum(String field) {

        return Run("SUM", field);
    }

    /// <summary>
    /// 求和
    /// </summary>
    /// <param name="field"></param>
    /// <returns></returns>
    public T sum(T field) {

        return Run("SUM", field);
    }

    /// <summary>
    /// 求平均
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    public Object avg(String field) {

        return Run("AVG", field);
    }

    /// <summary>
    /// 求平均
    /// </summary>
    public T avg(T field) {

        return Run("AVG", field);
    }

    /// <summary>
    /// 求最大值
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    public Object max(String field) {

        return Run("MAX", field);
    }

    /// <summary>
    /// 求最大值
    /// </summary>
    public T max(T field) {

        return Run("MAX", field);
    }

    /// <summary>
    /// 求最小值
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    public Object min(String field) {

        return Run("MIN", field);
    }

    /// <summary>
    /// 求最小值
    /// </summary>
    public T min(T field) {
        return Run("MIN", field);
    }

    /// <summary>
    /// 查询条件
    /// </summary>
    public IWhere<T> where() {
        return cond;
    }

    @Override
    public IObjectEntity<T> iff(Predicate<IObjectEntity<T>> where, IAction<IObjectEntity<T>> True) {
        if (where.test(this)) {
            True.action(this);
        }
        return this;
    }

    @Override
    public IObjectEntity<T> iff(Predicate<IObjectEntity<T>> where, IAction<IObjectEntity<T>> True, IAction<IObjectEntity<T>> False) {

        if (where.test(this)) {
            True.action(this);
        } else {
            False.action(this);
        }
        return this;
    }

    private Object Run(String fnName, String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(fnName);
        sb.append("(");
        sb.append(field);
        sb.append(") FROM ");
        sb.append(this.SqlHelper.TableInfo);
        sb.append(" ");
        List<Object> li = cond.Wherer.FormatSqlText(sb, new LinkedList<>());

        return this.sqler.executeScalar(sb.toString(), li.toArray());
    }

    private T Run(String fnName, T field) {
        Map dic = SqlUtils.fieldMap(field);
        Iterator em = dic.keySet().iterator();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        boolean v = false;

        while (em.hasNext()) {
            if (v) {
                sb.append(',');
            } else {
                v = true;
            }
            Object key = em.next();
            sb.append(fnName);
            sb.append("(");
            sb.append(this.db.quotePrefix());
            sb.append(key);
            sb.append(this.db.quoteSuffix());
            sb.append(") AS ");
            sb.append(this.db.quotePrefix());
            sb.append(key);
            sb.append(this.db.quoteSuffix());


        }
        sb.append(" FROM ");
        sb.append(this.SqlHelper.TableInfo);
        sb.append(" ");
        if (v) {
            List<Object> li = cond.Wherer.FormatSqlText(sb, new LinkedList<>());


            return this.sqler.executeSingle(tClass, sb.toString(), li.toArray());
        }
        return null;
    }

    @Override
    public Script SQL() {
        return this.sqler.SQL();
    }
}
