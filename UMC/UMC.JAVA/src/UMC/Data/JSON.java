package UMC.Data;

import UMC.Web.WebActivity;

import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public final class JSON {

    private static class Index {
        int index;

        public Index(int i) {
            this.index = i;
        }
    }

    private String _DateFormat;

    private static class JSONer implements IJSON

    {
        public String expression;


        @Override
        public void write(Writer writer) {

            try {
                writer.write(expression);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void read(String key, Object value) {

        }
    }


    public static Object expression(String expression) {
        JSONer json = new JSONer();
        json.expression = expression;
        return json;
    }

    public static <T> T deserialize(Class<T> tClass, String json) {
        if (UMC.Data.Utility.isEmpty(json)) {
            return null;
        }

        return (T) deserialize(json, new Index(-1), tClass);
    }

    public static <T> T deserialize(String json, Class<T> type) {
        if (UMC.Data.Utility.isEmpty(json)) {
            return null;
        }
        return (T) deserialize(json, new Index(-1), type);
    }

    public static Object deserialize(String json) {
        if (UMC.Data.Utility.isEmpty(json)) {
            return null;
        }
        return deserialize(json, new Index(-1));
    }

    static Object deserialize(String input, Index index, Class type) {
        boolean isArray = false;
        boolean isObject = false;
        boolean iGeneric = false;
        Class GenericType = null;
        List list = null;

        Object objValue = null;
        for (index.index++; index.index < input.length(); index.index++) {

            switch (input.charAt(index.index)) {
                case '[':
                    isArray = true;
                    Type ctype =
                            type.getGenericSuperclass();
                    if (ctype instanceof ParameterizedType) {
                        iGeneric = true;
                        try {

                            Constructor constructor = type.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            objValue = constructor.newInstance();
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        list = (List) objValue;

                        RemoveEmpty(input, index);
                        if (input.charAt(index.index + 1) == ']') {
                            index.index++;
                            return objValue;
                        }
                        GenericType = (Class)
                                ((ParameterizedType) ctype).getActualTypeArguments()[0];


                        list.add(deserialize(input, index, GenericType));

                    } else if (type.isArray()) {

                        objValue = list = new ArrayList();
                        Class gType = type.getComponentType();
                        RemoveEmpty(input, index);
                        if (input.charAt(index.index + 1) == ']') {
                            index.index++;
                            return Array.newInstance(gType, 0);
                        }
                        list.add(deserialize(input, index, gType));

                    } else {
                        throw new IllegalArgumentException("类型不对应");
                    }
                    break;
                case '{':
                    if (type.isInterface()) {
                        if (type == Map.class) {
                            index.index--;
                            return deserialize(input, index);
                        }
                    } else if (Utility.exists(type.getInterfaces(), t -> t == Map.class)) {
                        index.index--;
                        return deserialize(input, index);
                    } else if (type.isArray()) {
                        Class eType = type.getComponentType();
                        Object arr = Array.newInstance(eType, 1);
                        index.index--;
                        Array.set(arr, 0, deserialize(input, index, eType));
                        return arr;
                    }
                    isObject = true;
                    RemoveEmpty(input, index);
                    if (input.charAt(index.index + 1) == '}') {
                        index.index++;
                        try {

                            Constructor constructor = type.getDeclaredConstructor();//[0];
                            constructor.setAccessible(true);
                            return constructor.newInstance();
                        } catch (Throwable e) {
                            throw new RuntimeException(e);

                        }
                    }
                    String key = deserialize(input, index).toString();

                    try {
                        Constructor constructor = type.getDeclaredConstructor();//[0];
                        constructor.setAccessible(true);
                        objValue = constructor.newInstance();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                    if (objValue instanceof IJSON) {
                        if (objValue instanceof IJSONType) {
                            ((IJSON) objValue).read(key, deserialize(input, index, ((IJSONType) objValue).jsonClass(key)));
                        } else {
                            ((IJSON) objValue).read(key, deserialize(input, index));
                        }
                    } else {
                        Field field = null;
                        try {
                            field = type.getField(key);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                        if (field != null) {
                            field.setAccessible(true);
                            try {
                                field.set(objValue, deserialize(input, index, field.getType()));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            deserialize(input, index);
                        }
                    }
                    break;
                case ',':
                    if (isArray) {
                        if (type.isArray()) {
                            list.add(deserialize(input, index, type.getComponentType()));
                        } else if (iGeneric) {
                            list.add(deserialize(input, index, GenericType));
                        } else {
                            deserialize(input, index);
                        }
                    } else if (isObject) {
                        String key2 = deserialize(input, index).toString();
                        if (objValue instanceof IJSON) {
                            if (objValue instanceof IJSONType) {
                                ((IJSON) objValue).read(key2, deserialize(input, index, ((IJSONType) objValue).jsonClass(key2)));
                            } else {
                                ((IJSON) objValue).read(key2, deserialize(input, index));
                            }
                        } else {
                            Field field = null;
                            try {
                                field = type.getField(key2);
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            }
                            if (field != null) {
                                field.setAccessible(true);
                                try {
                                    field.set(objValue, deserialize(input, index, field.getType()));
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                deserialize(input, index);
                            }


                        }
                    }
                    break;
                case ']':
                    if (isArray) {
                        if (type.isArray()) {

                            Object array = Array.newInstance(type.getComponentType(), list.size());
                            for (int c = 0; c < list.size(); c++) {
                                Array.set(array, c, list.get(c));
                            }
                            return array;
                        }
                        return objValue;
                    } else {
                        throw new IllegalArgumentException("非JSON格式");
                    }
                case '}':
                    if (isObject) {
                        return objValue;
                    } else {
                        throw new IllegalArgumentException("非JSON格式");
                    }
                case ':':
                    break;
                case '"':
                case '\'':
                    return Utility.parse(deserialize(input, index, input.charAt(index.index)), type);//, type);

                case '$':
                case '_':
                case '.':
                    return Utility.parse(Deserialize2(input, index), type);
                default:
                    char code = input.charAt(index.index);//[index];
                    if ((code > 64 && code < 91) || (code > 96 && code < 123) || (code > 47 && code < 58)) {
                        String c = Deserialize2(input, index);
                        switch (c) {
                            case "undefined":
                            case "null":
                                return null;
                            default:

                                return Utility.parse(c, type);
                        }
                    }
                    break;
            }

        }
        return objValue;

    }

    private static void RemoveEmpty(String input, Index index) {
        boolean isb = false;
        for (index.index++; index.index < input.length(); index.index++) {
            switch (input.charAt(index.index)) {
                case '\t':
                case ' ':
                case '\b':
                case '\r':
                case '\n':
                    break;
                default:
                    isb = true;
                    break;
            }
            if (isb) {
                index.index--;
                break;
            }
        }


    }


    static Object deserialize(String input, Index index) {
        boolean isArray = false;
        boolean isObject = false;
        ArrayList list = null;
        Map hask = null;
        for (index.index++; index.index < input.length(); index.index++) {

            switch (input.charAt(index.index)) {
                case '[':
                    isArray = true;
                    list = new ArrayList();
                    RemoveEmpty(input, index);
                    if (input.charAt(index.index + 1) == ']') {
                        index.index++;
                        return list.toArray();
                    }
                    list.add(deserialize(input, index));

                    break;
                case '{':
                    isObject = true;
                    RemoveEmpty(input, index);
                    if (input.charAt(index.index + 1) == '}') {
                        index.index++;
                        if (hask != null) {
                            return hask;
                        }
                        return new Hashtable();
                    }
                    String key = deserialize(input, index).toString();

                    hask = new Hashtable();
                    hask.put(key, deserialize(input, index));
                    break;
                case ',':
                    if (isArray) {
                        list.add(deserialize(input, index));
                    } else if (isObject) {
                        String key2 = deserialize(input, index).toString();
                        hask.put(key2, deserialize(input, index));
                    }
                    break;
                case ']':
                    if (isArray) {
                        return list.toArray();
                    } else {
                        throw new IllegalArgumentException("非JSON格式");
                    }
                case '}':
                    if (isObject) {
                        return hask;
                    } else {
                        throw new IllegalArgumentException("非JSON格式");
                    }
                case ':':
                    break;
                case '"':
                case '\'':
                    return deserialize(input, index, input.charAt(index.index));

                case '$':
                case '_':
                case '.':
                    return Deserialize2(input, index);
                default:
                    char code = input.charAt(index.index);

                    if (code == 45 || (code > 64 && code < 91) || (code > 96 && code < 123) || (code > 47 && code < 58)) {
                        String c = Deserialize2(input, index);
                        switch (c) {
                            case "undefined":
                            case "null":
                                return null;
                            default:
                                return c;
                        }
                    }
                    break;
            }

        }
        throw new IllegalArgumentException("非JSON格式");
    }

    private static String Deserialize2(String input, Index index) {
        int b = index.index;
        StringBuilder sb = new StringBuilder();
        sb.append(input.charAt(b));
        for (int i = index.index + 1; i < input.length(); i++) {
            switch (input.charAt(i)) {
                case ']':
                case '}':
                case ':':
                case ',':
                    index.index = i - 1;
                    return sb.toString().trim();
                default:
                    sb.append(input.charAt(i));
                    break;

            }
        }
        index.index = input.length() - 1;
        return sb.toString().trim();

    }

   private static String deserialize(String input, Index index, char cher) {
        index.index++;

        boolean isTo = false;
        StringBuilder sb = new StringBuilder();

        for (; index.index < input.length(); index.index++) {
            char ichar = input.charAt(index.index);//[index];
            if (ichar == '\\') {
                if (isTo) {
                    sb.append('\\');
                    isTo = false;
                } else {
                    isTo = true;
                }

            } else if (isTo) {
                isTo = false;
                switch (ichar) {
                    case 'u':
                        byte[] codes = new byte[2];
                        codes[1] = Byte.parseByte(input.substring(index.index + 1, index.index + 3), 16);
                        codes[0] = Byte.parseByte(input.substring(index.index + 3, index.index + 5), 16);
                        // String.valueOf(codes);
                        sb.append(new String(codes));
                        index.index += 4;
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    default:
                        sb.append(ichar);
                        break;
                }
            } else if (ichar == cher) {
                break;

            } else {
                sb.append(ichar);
                isTo = false;
            }
        }
        if (index.index != input.length()) {
            return sb.toString();
        }
        throw new IllegalArgumentException("非JSON格式");

    }

    public static String serialize(Object obj) {
        StringWriter writer = new StringWriter();
        serialize(obj, writer);
        return writer.toString();
    }


    public static void serialize(Object obj, Writer sb) {
        serialize(obj, sb,"yyyy.M.d HH:mm");
    }


    public static void serialize(Object obj, Writer writer, String dateFormat) {
        JSON json = new JSON();
//        json.IsSerializer = false;
        json._DateFormat = dateFormat;
        try {
            json.SerializeObject(obj, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void serialize(Object obj, Writer writer, boolean serializer) {
//        JSON json = new JSON();
//        json.IsSerializer = serializer;
//        try {
//            json.SerializeObject(obj, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    private void serialize(Map dic, Writer writer) throws IOException {
        Iterator em = dic.keySet().iterator();
        writer.write('{');
        boolean bo = false;
        while (em.hasNext()) {
            if (bo) {
                writer.write(',');
            } else {
                bo = true;
            }
            Object key = em.next();
            this.SerializeObject(key, writer);
            writer.write(':');
            this.SerializeObject(dic.get(key), writer);

        }
        writer.write('}');
    }


    private void SerializeObject(Object obj, Writer writer) throws IOException {
        if (obj != null) {


            Class type = obj.getClass();
            if (type.isPrimitive() || obj instanceof Boolean) {
                writer.write(obj.toString().toLowerCase());
            } else if (obj instanceof Character) {
                writer.write('"');
                writer.write(obj.toString());
                writer.write('"');
            } else if (obj instanceof Number) {
                writer.write('"');
                writer.write(obj.toString());
                writer.write('"');
            } else if (type.equals(String.class)) {
                writer.write('"');
                String strs = (String) obj;
                for (int i = 0; i < strs.length(); i++) {

                    char c = strs.charAt(i);//[i];
                    switch (c) {
                        case '"':
                            writer.write("\\\"");
                            break;
                        case '\\':
                            writer.write("\\\\");
                            break;
                        case '\b':
                            writer.write("\\b");
                            break;
                        case '\f':
                            writer.write("\\f");
                            break;
                        case '\n':
                            writer.write("\\n");
                            break;
                        case '\t':
                            writer.write("\\t");
                            break;
                        case '\r':
                            writer.write("\\r");
                            break;
                        default:
                            writer.write(c);
                            break;
                    }
                }
                writer.write('"');
            } else if (obj instanceof IJSON) {
                ((IJSON) obj).write(writer);

            } else if (type.isEnum()) {
                writer.write('"');
                writer.write(obj.toString());
                writer.write('"');
            } else if (obj instanceof UUID) {
                writer.write('"');
                writer.write(obj.toString());
                writer.write('"');
            } else if (obj instanceof Date) {
                Date date = (Date) obj;
                if (UMC.Data.Utility.isEmpty(_DateFormat) == false) {
                    switch (_DateFormat) {
                        case "ts":
                            writer.write((int) (date.getTime() / 1000));
                            break;
                        default:
                            SimpleDateFormat formatter = new SimpleDateFormat(_DateFormat);//"yyyy-MM-dd HH:mm:ss");
                            this.SerializeObject(formatter.format(date), writer);
                            break;
                    }

                } else if (date.getHours() == 0 && date.getMinutes() == 0 && date.getSeconds() == 0) {
                    writer.write('"');

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    writer.write(formatter.format(date));
                    writer.write('"');
                } else {
                    writer.write('"');

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy.M.d HH:mm");
                    writer.write(formatter.format(date));
                    writer.write('"');
                }
            } else if (type.isArray()) {

                writer.write('[');

                for (int i = 0, l = Array.getLength(obj); i < l; i++) {
                    if (i != 0) {
                        writer.write(',');
                    }
                    this.SerializeObject(Array.get(obj, i), writer);

                }
                writer.write(']');
            } else if (obj instanceof Map) {
                serialize((Map) obj, writer);
            } else if (obj instanceof URL) {
                this.SerializeObject(((URL) obj).toString(), writer);
            } else if (obj instanceof Collection) {
                writer.write('[');
                boolean IsNext = false;
                Collection collection = (Collection) obj;
                Iterator iterator = collection.iterator();
                while (iterator.hasNext()) {
                    if (IsNext) {
                        writer.write(',');
                    } else {
                        IsNext = true;
                    }
                    this.SerializeObject(iterator.next(), writer);
                }
                writer.write(']');
            } else {
                if (Utility.exists(type.getDeclaredMethods(), f -> f.getName().equals("toString"))) {
                    writer.write(obj.toString());
                } else {
                    writer.write('{');
                    Field[] propertys = type.getFields();
                    Boolean IsEcho = false;
                    for (int i = 0; i < propertys.length; i++) {
                        Field prop = propertys[i];

                        if (this.SerializeProperty(obj, prop, writer, IsEcho)) {
                            IsEcho = true;
                        }
                    }
                    writer.write('}');
                }

            }
        } else {
            writer.write("\"\"");
        }
    }

    private boolean SerializeProperty(Object obj, Field property, Writer writer, boolean IsEcho) throws IOException {

        Object ov = null;
        try {
            property.setAccessible(true);
            ov = property.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (ov != null) {
            if (IsEcho) writer.write(',');
            writer.write('"');
            writer.write(property.getName());
            writer.write('"');
            writer.write(':');
            SerializeObject(ov, writer);
            return true;
        }
        return false;
    }


}
