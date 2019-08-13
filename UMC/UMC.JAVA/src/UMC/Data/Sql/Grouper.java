package UMC.Data.Sql;

import UMC.Data.Utility;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Grouper<T> implements IGrouper<T> {
    class GroupKey {
        public GroupKey(String field, String formula) {
            this.Field = field;
            this.Formula = formula;
        }

        public GroupKey(String field, String formula, String name) {
            this.Field = field;
            this.Formula = formula;
            this.Name = name;
        }

        public String Field;
        public String Formula;
        public String Name;
    }

    String[] fields;

    public Grouper(Sqler sqler, EntityHelper helper, ConditionsWhere<T> query, String... fields) {
        this.sqler = sqler;
        this.query = query;
        this.helper = helper;
        this.fields = fields;
        this.seq = new GSequencer<T>(this);
        this.GroupKeys = new LinkedList<>();
    }

    GSequencer<T> seq;
    Sqler sqler;
    EntityHelper helper;
    ConditionsWhere<T> query;

    List<GroupKey> GroupKeys;// = new List<GroupKey>();

    Object[] Format(StringBuilder sb) {
        DbProvider provider = this.sqler.provider;
        sb.append("SELECT ");
        boolean IsB = false;
        for (int i = 0; i < this.fields.length; i++) {
            if (IsB) {
                sb.append(',');
            } else {
                IsB = true;
            }
            sb.append(provider.quotePrefix());
            sb.append(this.fields[i]);
            sb.append(provider.quoteSuffix());

        }
        int ctime = 1;
        for (int i = 0; i < this.GroupKeys.size(); i++) {
            GroupKey group = this.GroupKeys.get(i);
            if (IsB) {
                sb.append(',');
            } else {
                IsB = true;
            }
            if (Utility.isEmpty(group.Name)) {
                switch (group.Field) {
                    case "*":
                        sb.append(group.Formula);
                        sb.append("(*) AS G");
                        sb.append(ctime);
                        ctime++;
                        break;
                    default:
                          sb.append(group.Formula);
                        sb.append("(");
                        sb.append(provider.quotePrefix());
                        sb.append(group.Field);
                        sb.append(provider.quoteSuffix());
                        sb.append(") AS G");
                        sb.append(ctime);
                        ctime++;
                        break;
                }
            } else {
                switch (group.Field) {
                    case "*":
                        sb.append(group.Formula);
                        sb.append("(*) AS ");

                        sb.append(provider.quotePrefix());
                        sb.append(group.Name);
                        sb.append(provider.quoteSuffix());
                        break;
                    default:
                        sb.append(group.Formula);
                        sb.append("(");
                        sb.append(provider.quotePrefix());
                        sb.append(group.Field);
                        sb.append(provider.quoteSuffix());
                        sb.append(") AS ");
                        sb.append(provider.quotePrefix());
                        sb.append(group.Name);
                        sb.append(provider.quoteSuffix());
                        break;
                }
            }

        }
        sb.append(" FROM ");
        sb.append(this.helper.TableInfo);
        sb.append(" ");

        List<Object> li = this.query.FormatSqlText(sb, new LinkedList<>());
        if (this.fields.length > 0) {
            sb.append(" GROUP BY ");
            for (int i = 0; i < this.fields.length; i++) {
                if (i != 0) {
                    sb.append(',');
                }

                sb.append(provider.quotePrefix());
                sb.append(this.fields[i]);
                sb.append(provider.quoteSuffix());
            }
        }
        return li.toArray();
    }

    public IGrouper<T> count() {
        Group("*", "COUNT");
        return this;
    }

    public IGrouper<T> sum(String field) {
        Group(field, "SUM");
        return this;
    }

    public IGrouper<T> avg(String field) {
        Group(field, "AVG");
        return this;
    }

    public IGrouper<T> max(String field) {
        Group(field, "MAX");
        return this;
    }

    public IGrouper<T> min(String field) {
        Group(field, "MIN");
        return this;
    }

    void Group(T field, String Formula) {
        Map dic = SqlUtils.fieldMap(field);
        Iterator<String> em = dic.keySet().iterator();
        while (em.hasNext()) {
            String key = em.next();
            this.GroupKeys.add(new GroupKey(key, Formula, key));
        }

    }


    void Group(String field, String Formula, String asName) {
        this.GroupKeys.add(new GroupKey(field, Formula, asName));

    }

    void Group(String field, String Formula) {

        this.GroupKeys.add(new GroupKey(field, Formula));

    }

    public IGrouper<T> sum(T field) {
        Group(field, "SUM");
        return this;
    }

    public IGrouper<T> avg(T field) {
        Group(field, "AVG");
        return this;
    }

    public IGrouper<T> max(T field) {
        Group(field, "MAX");
        return this;

    }

    public IGrouper<T> min(T field) {
        Group(field, "MIN");
        return this;
    }

    public List<Map> query() {
        StringBuilder sb = new StringBuilder();
        Object[] agrs = this.Format(sb);
        this.seq.FormatSqlText(sb);

        ISqler sqer = this.sqler;
        return sqer.execute(sb.toString(), agrs);
    }

    public IGroupOrder<T> order()

    {

        return this.seq;

    }


    public Script SQL()

    {
        return this.sqler.SQL();

    }


    public T single() {
        StringBuilder sb = new StringBuilder();
        Object[] agrs = this.Format(sb);
        this.seq.FormatSqlText(sb);


        return (T) this.sqler.executeSingle(this.helper.ObjType, sb.toString(), agrs);
    }

    public void query(IDataReader<T> reader) {
        StringBuilder sb = new StringBuilder();
        Object[] agrs = this.Format(sb);
        this.seq.FormatSqlText(sb);

        this.sqler.execute(helper.ObjType, sb.toString(), reader, agrs);

    }


    public IGrouper<T> count(T field) {
        Map dic = SqlUtils.fieldMap(field);
        Iterator<String> em = dic.keySet().iterator();
        while (em.hasNext()) {
            String key = em.next();
            this.GroupKeys.add(new GroupKey("*", "COUNT", key));
        }

        return this;
    }

    public IGrouper<T> count(String asName) {
        Group("*", "COUNT", asName);
        return this;
    }

    public IGrouper<T> sum(String field, String asName) {
        Group(field, "SUM", asName);
        return this;
    }

    public IGrouper<T> avg(String field, String asName) {
        Group(field, "AVG", asName);
        return this;
    }

    public IGrouper<T> max(String field, String asName) {
        Group(field, "MAX", asName);
        return this;
    }


    public IGrouper<T> min(String field, String asName) {
        Group(field, "MIN", asName);
        return this;
    }


}
