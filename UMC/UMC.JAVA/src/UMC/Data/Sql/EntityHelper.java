package UMC.Data.Sql;

import UMC.Data.Utility;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

class EntityHelper {
    public List<Object> Arguments;

    public class FieldInfo {
        public Field Field;
        public String Name;
        public int FieldIndex;
    }

    /// <summary>
    /// 类型的所有属性
    /// </summary>
    public List<FieldInfo> Fields;
    public Class ObjType;
    DbProvider Provider;

    /// <summary>
    /// 对应的表字段
    /// </summary>
    public EntityHelper(DbProvider dbProvider, Class type, String tableName) {
        this.Provider = dbProvider;
        this.Arguments = new LinkedList<>();
        this.Fields = new LinkedList<>();
        this.ObjType = type;
        if (UMC.Data.Utility.isEmpty(tableName)) {
            tableName = type.getSimpleName();
        }

        Bind(type, tableName);
    }

    String TableInfo;

    public EntityHelper(DbProvider dbProvider, Class type)

    {
        this(dbProvider, type, "");
    }

    void Bind(Class type, String tableName) {

        TableInfo = tableName;
        Field[] fields = type.getFields();

        for (int i = 0; i < fields.length; i++) {

            Field field = fields[i];
            field.setAccessible(true);
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.Field = field;
            fieldInfo.Name = field.getName();
            fieldInfo.FieldIndex = -1;
            this.Fields.add(fieldInfo);
        }


    }

    /// <summary>
    /// 创建插入实体SQL脚本
    /// </summary>
    /// <returns></returns>
    public String CreateInsertText(Object entity) {

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(this.TableInfo);
        sb.append("(");
        StringBuilder sb2 = new StringBuilder();
        sb2.append("VALUES(");
        boolean IsMush = false;
        this.Arguments.clear();

        for (int i = 0; i < Fields.size(); i++) {
            Field field = Fields.get(i).Field;


            Object value = null;
            try {
                value = field.get(entity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value == null) {
                continue;
            }

            if (IsMush) {
                sb.append(",");
                sb2.append(",");
            } else {
                IsMush = true;
            }

            sb.append(Provider.quotePrefix());
            sb.append(field.getName());
            sb.append(Provider.quoteSuffix());

            sb2.append('{');
            sb2.append(this.Arguments.size());
            sb2.append('}');

            this.Arguments.add(this.GetArgumentValue(value, field));

        }

        sb.append(")");
        sb2.append(")");
        return sb.toString() + sb2.toString();

    }

    /// <summary>
    /// 创建实体ＳＱＬ删除脚本
    /// </summary>
    /// <returns></returns>
    public String CreateDeleteText() {
        return "DELETE FROM  " + TableInfo + " ";

    }

    /// <summary>
    /// 创建实体查询SQL脚本
    /// </summary>
    /// <returns></returns>
    public String CreateSelectText(Object entity) {
        this.IsClearIndex = false;
        boolean IsEntity = false;
        if (entity != null) {
            IsEntity = this.ObjType.equals(entity.getClass());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        boolean IsMush = false;
        int index = 0;


        for (int i = 0; i < Fields.size(); i++) {

            Field field = Fields.get(i).Field;
            if (IsEntity) {
                try {
                    if (field.get(entity) == null) {
                        continue;
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            if (IsMush) {
                sb.append(",");
            } else {
                IsMush = true;
            }
            sb.append(Provider.quotePrefix());
            sb.append(field.getName());
            sb.append(Provider.quoteSuffix());
        }
        sb.append(" FROM ");
        sb.append(TableInfo);
        sb.append(" ");

        return sb.toString();
    }

    public boolean IsClearIndex;

    void SetIndex(ResultSet rs) throws SQLException {
        for (int i = 0; i < Fields.size(); i++) {
            Fields.get(i).FieldIndex = -1;
        }
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int c = 0, l = rsmd.getColumnCount(); c < l; c++) {
            String name = rsmd.getColumnName(c + 1);

            for (int i = 0; i < Fields.size(); i++) {
                FieldInfo info = Fields.get(i);
                if (info.Name.equalsIgnoreCase(name)) {
                    info.FieldIndex = c + 1;
                }
            }
        }

        IsClearIndex = true;
    }

    /// <summary>
    /// 创建实体更新脚本
    /// </summary>
    /// <returns></returns>
    String GetUpdateText(Object entity, String format) {
        if (UMC.Data.Utility.isEmpty(format)) {
            format = "{1}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" UPDATE ");
        sb.append(TableInfo);
        sb.append(" SET  ");
//        sb.AppendFormat(" UPDATE {0} SET  ", TableInfo.name);

        boolean IsMush = false;

        for (int i = 0; i < Fields.size(); i++) {
            Field fd = Fields.get(i).Field;

            Object value = null;
            try {
                value = fd.get(entity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value == null) {
                continue;
            }
            if (IsMush) {
                sb.append(",");
            } else {
                IsMush = true;
            }
            String Field = Provider.quotePrefix() + fd.getName() + Provider.quoteSuffix();
            String Value = "{" + this.Arguments.size() + "}";
            sb.append(Field);
            sb.append('=');

            sb.append(format.replace("{0}", Field).replace("{1}", Value));


            this.Arguments.add(this.GetArgumentValue(value, fd));


        }
        if (IsMush == false) {
            throw new IllegalArgumentException("实体更新无有效字段");
        }
        return sb.toString();
    }


    public String CreateUpdateText(String format, Map<String, Object> fieldValues) {
        if (UMC.Data.Utility.isEmpty(format)) {
            format = "{1}";
        }

        this.Arguments.clear();
        StringBuilder sb = new StringBuilder();

        sb.append(" UPDATE ");
        sb.append(TableInfo);
        sb.append(" SET  ");


        Iterator<String> em = fieldValues.keySet().iterator();
        boolean IsMush = false;
        while (em.hasNext()) {
            if (IsMush) {
                sb.append(",");
            } else {
                IsMush = true;
            }
            String key = em.next();
            String Field = Provider.quotePrefix() + key + Provider.quoteSuffix();
            String Value = "{" + this.Arguments.size() + "}";

            sb.append(Field);
            sb.append('=');
            sb.append(format.replace("{0}", Field).replace("{1}", Value));

            this.Arguments.add(fieldValues.get(key));
        }

        return sb.toString();
    }

    Object GetArgumentValue(Object value, Field protype) {

        return value;
    }

    public String CreateUpdateText(String format, Object entity, String... proNames) {
        if (UMC.Data.Utility.isEmpty(format)) {
            format = "{1}";
        }

        this.Arguments.clear();
        if (proNames.length == 0) {
            return GetUpdateText(entity, format);
        }
        StringBuilder sb = new StringBuilder();

        sb.append(" UPDATE ");
        sb.append(TableInfo);
        sb.append(" SET  ");

        boolean IsMush = false;

        for (int i = 0; i < Fields.size(); i++) {
            Field fd = Fields.get(i).Field;

            Object value = null;
            try {
                value = fd.get(entity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value == null) {
                continue;
            }
            boolean isOk = false;
            String name = fd.getName();
            for (int c = 0; c < proNames.length; c++) {
                if (proNames[c].equalsIgnoreCase(name)) {
                    isOk = true;
                    break;
                }
            }
            if (isOk == false) {
                continue;
            }
            if (IsMush) {
                sb.append(",");
            } else {
                IsMush = true;
            }
            String Field = Provider.quotePrefix() + fd.getName() + Provider.quoteSuffix();
            String Value = "{" + this.Arguments.size() + "}";
            sb.append(Field);
            sb.append('=');

            sb.append(format.replace("{0}", Field).replace("{1}", Value));


            this.Arguments.add(this.GetArgumentValue(value, fd));


        }
        return sb.toString();

    }


    public Object CreateObject(Object obvalue, ResultSet dr) throws SQLException {
        if (IsClearIndex == false) {
            SetIndex(dr);
        }


        for (int i = 0; i < Fields.size(); i++) {
            FieldInfo fd = Fields.get(i);
            if (fd.FieldIndex == -1) {
                continue;
            }
            Object drObj = dr.getObject(fd.FieldIndex);
            if (drObj != null) {
                Class cls = fd.Field.getType();
                try {
                    if (drObj instanceof String) {

                        if (cls.equals(String.class)) {
                            fd.Field.set(obvalue, drObj);

                        } else {
                            fd.Field.set(obvalue, Utility.parse((String) drObj, cls));
                        }
                    } else if (cls.equals(Date.class)) {
                        if (drObj instanceof Date) {
                            fd.Field.set(obvalue, drObj);
                        } else {
                            if (drObj instanceof Long) {

                                fd.Field.set(obvalue, new Date((Long) drObj));
                            } else if (drObj instanceof Integer) {

                                fd.Field.set(obvalue, new Date(((Integer) drObj) * 1000));
                            } else {

                                fd.Field.set(obvalue, Utility.parse(drObj + "", cls));
                            }
                        }
                    } else if (cls.equals(Boolean.class)) {
                        if (drObj instanceof Boolean) {
                            fd.Field.set(obvalue, drObj);
                        } else if (drObj instanceof Integer) {
                            fd.Field.set(obvalue, ((Integer) drObj) != 0);

                        } else {
                            fd.Field.set(obvalue, Utility.parse(drObj + "", cls));
                        }
                    } else {
                        fd.Field.set(obvalue, drObj);
                    }

                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        return obvalue;
    }

}