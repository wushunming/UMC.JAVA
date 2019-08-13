package UMC.Data.Sql;


import UMC.Data.Utility;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/// <summary>
/// 查询条件
/// </summary>
class Conditions {
    public boolean ContainsKey(String field) {
        for (int i = 0, l = this.Arguments.size(); i < l; i++) {
            if (this.Arguments.get(i).PropertyName.equals(field)) {
                return true;
            }
        }
        return false;
    }


    public class FieldArgument {

        public double ForId;// { get; set; }
        public double GroubId;//{ get; set; }
        public String PropertyName;// { get; set; }
        public Object Value;// { get; set; }
        public DbOperator ConstraintVocable;//{ get; set; }
        public JoinVocable JoinVocable;//{ get; set; }
        public JoinVocable FristJoin;//{ get; set; }

    }

    public JoinVocable FristJoin;// { get; set; }

    public enum JoinVocable {
        Empty,
        And,
        Or
    }

    public double GroubId;

    public List<FieldArgument> Arguments;

    public Conditions(String field, DbOperator co, Object value) {
        GroubId = Math.random();
        Arguments = new LinkedList<>();
        FieldArgument pr = new FieldArgument();
        pr.PropertyName = field;
        pr.ConstraintVocable = co;
        pr.Value = value;
        pr.JoinVocable = JoinVocable.Empty;
        pr.GroubId = GroubId;
        Arguments.add(pr);
    }

    public Conditions(JoinVocable json)

    {
        this();
        FristJoin = json;
    }

    public Conditions() {
        GroubId = Math.random();
        Arguments = new LinkedList<>();
    }

    public Conditions Reset() {
        Arguments.clear();
        return this;
    }

    public Conditions Or(String expression, Object... objs) {
        FieldArgument pr = new FieldArgument();

        pr.ForId = GroubId;
        pr.GroubId = Math.random();
        pr.PropertyName = expression;
        pr.ConstraintVocable = DbOperator.Expression;
        pr.Value = objs;
        pr.JoinVocable = JoinVocable.Or;

        this.CheckGroup(pr);

        return this;
    }

    public Conditions And(String expression, Object... objs) {
        FieldArgument pr = new FieldArgument();

        pr.ForId = GroubId;
        pr.GroubId = Math.random();
        pr.PropertyName = expression;
        pr.ConstraintVocable = DbOperator.Expression;
        pr.Value = objs;
        pr.JoinVocable = JoinVocable.And;

        this.CheckGroup(pr);

        return this;
    }

    public Conditions And(String pName, DbOperator co, Object value) {
        FieldArgument pr = new FieldArgument();
        pr.ForId = GroubId;
        pr.GroubId = Math.random();
        pr.PropertyName = pName;
        pr.ConstraintVocable = co;
        pr.Value = value;
        pr.JoinVocable = JoinVocable.And;
        this.CheckGroup(pr);

        return this;
    }

    public Conditions Or(String pName, DbOperator co, Object value) {
        FieldArgument pr = new FieldArgument();
        pr.ForId = this.GroubId;
        pr.GroubId = Math.random();
        pr.PropertyName = pName;
        pr.ConstraintVocable = co;
        pr.Value = value;
        pr.JoinVocable = JoinVocable.Or;
        this.CheckGroup(pr);
        return this;
    }

    void CheckGroup(FieldArgument pr) {
        if (this.IsContainFrist) {
            this.GroubId = pr.GroubId;
            pr.FristJoin = FristJoin;
            this.IsContainFrist = false;
        }
        this.Arguments.add(pr);
    }

    public boolean IsContainFrist = false;


    //}
    String SqlFormat(String sqlText, Object[] paramers, List<Object> sqlParams) {
        int count = sqlParams.size();

        Pattern p = Pattern.compile("\\{(\\d+)\\}");

        Matcher m = p.matcher(sqlText);
//        sqlText.re
        StringBuffer operatorStr = new StringBuffer();
        int startIndex = 0;
        while (m.find()) {
            int index = Integer.parseInt(m.group(1));
            if (index >= paramers.length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            operatorStr.append(sqlText.substring(startIndex, m.start()));
            operatorStr.append("{");
            operatorStr.append(count + index);
            operatorStr.append("}");
            startIndex = m.end();

//            operatorStr.replace(m.start(), m.end(), "{" + (count + index) + "}");
//            m = p.matcher(operatorStr);
        }
        operatorStr.append(sqlText.substring(startIndex));


        sqlParams.addAll(Arrays.asList(paramers));
        return operatorStr.toString();

    }

    void AppendInParameter1(StringBuilder sb, List<Object> sqlParams, Object values, int level) {
        boolean isWrite = false;
        level++;

        for (int i = 0, l = Array.getLength(values); i < l; i++) {
            if (isWrite) {
                sb.append(',');
            } else {
                isWrite = true;
            }
            Object va = Array.get(values, i);//values.GetValue(i);
            if (level < 2) {
                if (va != null && va.getClass().isArray()) {
                    // var vas = va as Array;
                    AppendInParameter1(sb, sqlParams, va, level);
                    if (Array.getLength(va) == 0) {
                        isWrite = false;
                    }
                    continue;
                }
            }

            sb.append('{');
            sb.append(sqlParams.size());
            sb.append('}');

            sqlParams.add(va);

        }
    }

    void AppendInParameter(StringBuilder sb, List<Object> sqlParams, Object value) {
        if (value.getClass().isArray()) {

            if (value instanceof Object[]) {
                this.AppendInParameters(sb, sqlParams, (Object[]) value);
            } else {
                ArrayList arrayList = new ArrayList();
                for (int i = 0, l = Array.getLength(value); i < l; i++) {
                    arrayList.add(Array.get(value, i));

                }
                this.AppendInParameters(sb, sqlParams, arrayList.toArray());

            }
        } else if (value instanceof Collection) {
            ArrayList arrayList = new ArrayList();
            Collection collection = (Collection) value;
            arrayList.addAll(collection);

            this.AppendInParameters(sb, sqlParams, arrayList.toArray());

        } else {
            this.AppendInParameters(sb, sqlParams, new Object[]{value});
        }
    }

    void AppendInParameters(StringBuilder sb, List<Object> sqlParams, Object[] values) {
        switch (values.length) {
            case 1:
                Object v = values[0];

                if (v instanceof Script) {
                    Script sciipt = (Script) v;//as script;
                    sb.append(this.SqlFormat(sciipt.text(), sciipt.arguments(), sqlParams));

                    break;
                }
                AppendInParameter1(sb, sqlParams, values, 0);

                break;
            default:
                AppendInParameter1(sb, sqlParams, values, 0);

                break;
        }
    }

    private void SetParameter(FieldArgument param, StringBuilder sb, List<Object> sqlParams) {
        if (param.ConstraintVocable == DbOperator.Expression) {
            sb.append(this.SqlFormat(param.PropertyName, (Object[]) param.Value, sqlParams));
            return;
        }

        String proKey = param.PropertyName;
        sb.append(proKey);
        switch (param.ConstraintVocable) {
            case Unequal:
                sb.append(" <>  ");
                break;
            case Equal:
                sb.append(" =  ");
                break;
            case Greater:
                sb.append(" >  ");
                break;
            case GreaterEqual:
                sb.append(" >=  ");
                break;
            case NotLike:
                sb.append(" NOT LIKE ");
                break;
            case Like:
                sb.append(" LIKE  ");
                break;
            case Smaller:
                sb.append(" <  ");
                break;
            case SmallerEqual:
                sb.append(" <=  ");
                break;
            case In:
                sb.append(" IN(");
                AppendInParameter(sb, sqlParams, param.Value);
                sb.append(')');
                return;
            case NotIn:
                sb.append(" NOT IN(");
                AppendInParameter(sb, sqlParams, param.Value);
                sb.append(')');
                return;
            default:
                throw new ArrayIndexOutOfBoundsException();
        }
        sb.append("{");
        sb.append(sqlParams.size());
        sb.append("}");

        sqlParams.add(param.Value);

    }

    public boolean ContainsForId(double field) {
        for (int i = 0, l = this.Arguments.size(); i < l; i++) {
            if (this.Arguments.get(i).ForId == field) {
                return true;
            }
        }
        return false;
    }

    private void SetGroup(int destLength, double forid, StringBuilder sb, List<Object> sqlParams) {
        List<FieldArgument> parms = new LinkedList<>();
        for (int i = 0, l = this.Arguments.size(); i < l; i++) {
            FieldArgument arg = this.Arguments.get(i);
            if (arg.ForId == forid) {
                parms.add(arg);
            }
        }
        for (int i = 0; i < parms.size(); i++) {
            FieldArgument fieldArgument = parms.get(i);
            if (ContainsForId(GroubId)) {
                if (sb.length() == destLength) {
                    sb.append(" WHERE ( ");
                } else {
                    sb.append(" ");

                    JoinVocable joinVocable = parms.get(i).JoinVocable;
                    if (joinVocable == JoinVocable.Empty) {
                        FieldArgument pjs = Arguments.get(Arguments.indexOf(parms.get(i)) - 1);

                        joinVocable = pjs.JoinVocable;
                    }
                    switch (joinVocable) {
                        case Or:
                            sb.append("or");
                            break;
                        case And:
                            sb.append("and");
                            break;
                    }
                    sb.append(" ( ");
                }

                SetParameter(parms.get(i), sb, sqlParams);

                SetGroup(destLength, parms.get(i).GroubId, sb, sqlParams);
                sb.append(") ");
            } else {
                if (sb.length() == destLength) {
                    sb.append(" WHERE ");
                } else {
                    sb.append(" ");
                    sb.append(parms.get(i).JoinVocable);
                    sb.append(" ");
                }


                SetParameter(parms.get(i), sb, sqlParams);

            }
        }
    }


    public List<Object> FormatSqlText(StringBuilder sb, List<Object> lp) {
        if (Arguments.size() >= 0) {
            //  sb.Append(" Where 1=1 ");
            SetGroup(sb.length(), this.GroubId, sb, lp);
        }
        return lp;
    }

    public Operator Or() {
        return new Operator(this, true);
    }

    public Operator And() {
        return new Operator(this, false);
    }


    public Object get(String name)

    {
        for (int i = 0; i < this.Arguments.size(); i++) {
            FieldArgument p = Arguments.get(i);
            if (p.ConstraintVocable != DbOperator.Expression && p.ForId == this.GroubId && p.PropertyName.equals(name)) {

                return p.Value;
            }
        }
        return null;

    }

    public void set(String name, Object value) {

        for (int i = 0; i < this.Arguments.size(); i++) {
            FieldArgument p = Arguments.get(i);
            if (p.ConstraintVocable != DbOperator.Expression && p.ForId == this.GroubId && p.PropertyName.equals(name)) {

                p.Value = value;
                return;
            }
        }

    }


    public int Remove(String name) {
        int size = this.Arguments.size();
        for (int i = 0; i < this.Arguments.size(); i++) {
            FieldArgument p = Arguments.get(i);
            if (p.ConstraintVocable != DbOperator.Expression && p.ForId == this.GroubId && p.PropertyName.equals(name)) {
                this.Arguments.remove(i);
                i--;
            }
        }
        return size - this.Arguments.size();

    }


    public int getCount() {
        int count = 0;
        for (int i = 0; i < this.Arguments.size(); i++) {
            FieldArgument p = Arguments.get(i);
            if (p.ForId == this.GroubId) {
                count++;
            }
        }
        return count;//this.Arguments.FindAll(p = > p.ForId == this.GroubId).count;

    }

    public Conditions Contains() {
        Conditions query = new Conditions();
        query.GroubId = this.GroubId;
        query.IsContainFrist = true;
        query.Arguments = this.Arguments;
        return query;
    }

}

class Operator {
    public boolean IsOr;
    public Conditions condit;

    public Operator(Conditions q, boolean IsOr) {
        this.condit = q;
        this.IsOr = IsOr;
    }


    public Conditions Unequal(String field, Object value) {
        if (Utility.isEmpty(field)) {
            throw new NullPointerException("field");
        }
        if (IsOr) {
            return this.condit.Or(field, DbOperator.Unequal, value);
        } else {
            return this.condit.And(field, DbOperator.Unequal, value);
        }
    }

    public Conditions Equal(String field, Object value) {
        if (Utility.isEmpty(field)) {
            throw new NullPointerException("field");
        }
        if (IsOr) {
            return this.condit.Or(field, DbOperator.Equal, value);
        } else {
            return this.condit.And(field, DbOperator.Equal, value);
        }
    }

    public Conditions Greater(String field, Object value) {
        if (Utility.isEmpty(field)) {
            throw new NullPointerException("field");
        }
        if (IsOr) {
            return this.condit.Or(field, DbOperator.Greater, value);
        } else {
            return this.condit.And(field, DbOperator.Greater, value);
        }
    }

    public Conditions Smaller(String field, Object value) {
        if (Utility.isEmpty(field)) {
            throw new NullPointerException("field");
        }
        if (IsOr) {
            return this.condit.Or(field, DbOperator.Smaller, value);
        } else {
            return this.condit.And(field, DbOperator.Smaller, value);
        }
    }

    public Conditions GreaterEqual(String field, Object value) {
        if (Utility.isEmpty(field)) {
            throw new NullPointerException("field");
        }
        if (IsOr) {
            return this.condit.Or(field, DbOperator.GreaterEqual, value);
        } else {
            return this.condit.And(field, DbOperator.GreaterEqual, value);
        }
    }

    public Conditions SmallerEqual(String field, Object value) {
        if (IsOr) {
            return this.condit.Or(field, DbOperator.SmallerEqual, value);
        } else {
            return this.condit.And(field, DbOperator.SmallerEqual, value);
        }
    }

    public Conditions Like(String field, String value) {
        if (Utility.isEmpty(field)) {
            throw new NullPointerException("field");
        }
        if (IsOr) {
            return this.condit.Or(field, DbOperator.Like, value);
        } else {
            return this.condit.And(field, DbOperator.Like, value);
        }
    }

    public Conditions In(String field, Object... values) {
        if (Utility.isEmpty(field)) {
            throw new NullPointerException("field");
        }
        if (values.length == 0) {
            throw new NullPointerException("values的长度不能为0");
        }

        if (IsOr) {
            return this.condit.Or(field, DbOperator.In, values);
        } else {
            return this.condit.And(field, DbOperator.In, values);
        }
    }


    public Conditions NotIn(String field, Object... values) {
        if (Utility.isEmpty(field)) {
            throw new NullPointerException("field");
        }
        if (values.length == 0) {
            throw new ArithmeticException("values的长度不能为0");
        }

        if (IsOr) {
            return this.condit.Or(field, DbOperator.NotIn, values);
        } else {
            return this.condit.And(field, DbOperator.NotIn, values);
        }
    }


    public Conditions NotLike(String field, String value) {
        if (Utility.isEmpty(field)) {
            throw new NullPointerException("field");
        }
        if (IsOr) {
            return this.condit.Or(field, DbOperator.NotLike, value);
        } else {
            return this.condit.And(field, DbOperator.NotLike, value);
        }
    }

}

enum DbOperator {
    /// <summary>
    /// 不等于&lt;&gt;
    /// </summary>
    Unequal,
    /// <summary>
    /// 等于 =
    /// </summary>
    Equal,
    /// <summary>
    /// 大于 &gt;
    /// </summary>
    Greater,
    /// <summary>
    /// 小于&lt;
    /// </summary>
    Smaller,
    /// <summary>
    /// 大于等于 &gt;=
    /// </summary>
    GreaterEqual,
    /// <summary>
    /// 小于等于 &lt;=
    /// </summary>
    SmallerEqual,
    /// <summary>
    /// like
    /// </summary>
    Like,
    /// <summary>
    /// like
    /// </summary>
    NotLike,
    /// <summary>
    /// 表达式
    /// </summary>
    Expression,
    In,
    NotIn

}


