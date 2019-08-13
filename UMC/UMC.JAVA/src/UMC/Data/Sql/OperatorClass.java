package UMC.Data.Sql;

import java.util.*;

class OperatorClass<T> implements IOperator<T> {

    IWhere<T> constr;
    Operator opor;

    public OperatorClass(Operator c, IWhere<T> cons) {
        this.opor = c;
        this.constr = cons;
    }

    public IWhere<T> unEqual(String field, Object value) {
        this.opor.Unequal(field, value);
        return constr;
    }

    public IWhere<T> equal(String field, Object value) {
        this.opor.Equal(field, value);
        return constr;
    }

    public IWhere<T> greater(String field, Object value) {
        this.opor.Greater(field, value);
        return constr;
    }

    public IWhere<T> smaller(String field, Object value) {
        this.opor.Smaller(field, value);
        return constr;
    }

    public IWhere<T> greaterEqual(String field, Object value) {
        this.opor.GreaterEqual(field, value);
        return constr;
    }

    public IWhere<T> smallerEqual(String field, Object value) {
        this.opor.SmallerEqual(field, value);
        return constr;
    }

    public IWhere<T> like(String field, String value) {

        this.opor.Like(field, value);
        return constr;
    }

    public IWhere<T> in(String field, Object... values) {
        this.opor.In(field, values);
        return constr;
    }

    public IWhere<T> notIn(String field, Object... values) {
        this.opor.NotIn(field, values);
        return constr;
    }


    public IWhere<T> unEqual(T value) {
        Map<String, Object> dic = SqlUtils.fieldMap(value);

        Set<Map.Entry<String, Object>> em = dic.entrySet();//.iterator();
        for (Map.Entry<String, Object> entry : em) {

            this.opor.Unequal(entry.getKey(), entry.getValue());

        }
        return constr;
    }


    public IWhere<T> equal(T value) {
        Map<String, Object> dic = SqlUtils.fieldMap(value);
        Set<Map.Entry<String, Object>> em = dic.entrySet();//.iterator();
        for (Map.Entry<String, Object> entry : em) {

            this.opor.Equal(entry.getKey(), entry.getValue());

        }
        return constr;
    }

    public IWhere<T> greater(T value) {
        Map<String, Object> dic = SqlUtils.fieldMap(value);

        Set<Map.Entry<String, Object>> em = dic.entrySet();//.iterator();
        for (Map.Entry<String, Object> entry : em) {

            this.opor.Greater(entry.getKey(), entry.getValue());

        }
        return constr;
    }

    public IWhere<T> smaller(T value) {
        Map<String, Object> dic = SqlUtils.fieldMap(value);
        Set<Map.Entry<String, Object>> em = dic.entrySet();//.iterator();
        for (Map.Entry<String, Object> entry : em) {

            this.opor.Smaller(entry.getKey(), entry.getValue());

        }
        return constr;
    }

    public IWhere<T> greaterEqual(T value) {

        Map<String, Object> dic = SqlUtils.fieldMap(value);
        Set<Map.Entry<String, Object>> em = dic.entrySet();//.iterator();
        for (Map.Entry<String, Object> entry : em) {

            this.opor.GreaterEqual(entry.getKey(), entry.getValue());

        }
        return constr;
    }


    public IWhere<T> smallerEqual(T value) {
        Map<String, Object> dic = SqlUtils.fieldMap(value);
        Set<Map.Entry<String, Object>> em = dic.entrySet();//.iterator();
        for (Map.Entry<String, Object> entry : em) {

            this.opor.SmallerEqual(entry.getKey(), entry.getValue());

        }
        return constr;
    }


    public IWhere<T> in(String field, Script script) {
        this.opor.In(field, script);
        return constr;
    }


    public IWhere<T> in(T field, Object... values) {

        Map<String, Object> dic = SqlUtils.fieldMap(field);
        if (dic.size() > 1) {
            throw new ArithmeticException("实体In，只能有一个字段");
        }
        Map.Entry<String, Object> em = dic.entrySet().stream().findFirst().get();


        List<Object> list = new LinkedList<>();
        String key = em.getKey();
        list.add(em.getValue());
        list.addAll(Arrays.asList(values));
        this.opor.In(key, list.toArray());

        return constr;

    }

    public IWhere<T> notIn(T field, Object... values) {
        Map<String, Object> dic = SqlUtils.fieldMap(field);
        if (dic.size() > 1) {
            throw new ArithmeticException("实体Not In，只能有一个字段");
        }

        Map.Entry<String, Object> em = dic.entrySet().stream().findFirst().get();


        List<Object> list = new LinkedList<>();
        String key = em.getKey();
        list.add(em.getValue());
        list.addAll(Arrays.asList(values));
        this.opor.NotIn(key, list.toArray());

        return constr;

    }

    public IWhere<T> notIn(String field, Script script) {
        this.opor.NotIn(field, script);
        return constr;
    }


    public IWhere<T> notLike(String field, String value) {
        this.opor.NotLike(field, value);
        return constr;
    }


    public IWhere<T> like(T field, boolean schar) {

        Map<String, Object> dic = SqlUtils.fieldMap(field);

        Set<Map.Entry<String, Object>> em = dic.entrySet();//.iterator();
        for (Map.Entry<String, Object> entry : em) {
            this.opor.Like(entry.getKey(), String.format("%s%s", entry.getValue(), schar ? "%" : ""));

        }
        return constr;
    }


    public IWhere<T> like(T field) {

        Map<String, Object> dic = SqlUtils.fieldMap(field);
        Set<Map.Entry<String, Object>> em = dic.entrySet();//.iterator();
        for (Map.Entry<String, Object> entry : em) {

            this.opor.Like(entry.getKey(), "%" + entry.getValue() + "%");

        }
        return constr;
    }


    public IWhere<T> contains() {
        ConditionsWhere t = (ConditionsWhere) this.constr.contains();
        Operator op = this.opor;
        t.Wherer.FristJoin = op.IsOr ? UMC.Data.Sql.Conditions.JoinVocable.Or : UMC.Data.Sql.Conditions.JoinVocable.And;
        return t;
    }
}