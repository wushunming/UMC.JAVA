package UMC.Data;

import UMC.Data.Sql.IOperator;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Utility {

    public final static UUID uuidEmpty;

    static {
        uuidEmpty = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }

    public static Map<String, Object> fieldMap(Object obj) {
        Map<String, Object> dic = new HashMap<>();
        if (obj != null) {
            Field[] fields = obj.getClass().getFields();
            for (int i = 0; i < fields.length; i++) {
                Object object = null;
                try {
                    object = fields[i].get(obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (object != null) {
                    dic.put(fields[i].getName(), object);
                }

            }

        }

        return dic;
    }


    /** 获取注册的类实例
     * @param providerName 注册名，注册的文件在App_Data/WebADNuke/assembly.xml
     * @return
     */
    public static Object createObject(String providerName) {
        ProviderConfiguration pc = ProviderConfiguration.configuration("assembly");
        if (pc == null) {
            return null;
        } else {
            Provider provider = pc.get(providerName);

            if (provider != null) {
                return createInstance(provider);
            } else {
                return null;
            }
        }
    }

    public static String trim(String str, Character... cs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        while (stringBuilder.length() > 0 && Utility.exists(cs, c -> stringBuilder.substring(0, 1).charAt(0) == c.charValue())) {

            stringBuilder.deleteCharAt(0);
        }
        while (stringBuilder.length() > 0 && Utility.exists(cs, c -> stringBuilder.substring(stringBuilder.length() - 1).charAt(0) == c.charValue())) {

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();


    }

    public static String mapPath(String path) {

        return Utility.class.getClassLoader().getResource("../").getPath() + trim(path, '~', '/');

    }

    public static String reader(String file) {

        StringBuilder stringBuilder = new StringBuilder();
        try {

            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf8");

            char[] cha = new char[1024];
            int len = 0;
            while ((len = isr.read(cha)) != -1) {
                stringBuilder.append(cha, 0, len);
            }
            isr.close();


        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        return stringBuilder.toString();
    }

    public static Object createInstance(Provider provider) {
        try {
            Class aClass = Class.forName(provider.type());
            Object object = null;
            try {
                object = aClass.newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }

            if (object instanceof DataProvider) {
                DataProvider dprovider = (DataProvider) object;// aClass.provider
                dprovider.provider = provider;
            }
            return object;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object createInstance(Class cls, Provider provider) {

        Object object = null;
        try {
            object = cls.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        if (object instanceof DataProvider) {
            DataProvider dprovider = (DataProvider) object;// aClass.provider
            dprovider.provider = provider;
        }
        return object;
    }


    private static Date strToDateLong(String strDate) {
        String[] sdate = strDate.split(" |T|t");
        int year = 1900, month = 0, day = 1;
        if (sdate.length > 0) {
            String[] sd = sdate[0].split("-|\\.");
            switch (sd.length) {
                case 1:
                    int index = Integer.parseInt(strDate);
                    return new Date(index * 1000l);
                case 2:
                    year = Integer.parseInt(sd[0]);
                    month = Integer.parseInt(sd[1]) - 1;//, 1);
                    break;
                case 3:

                    year = Integer.parseInt(sd[0]);
                    month = Integer.parseInt(sd[1]) - 1;//, 1);
                    day = Integer.parseInt(sd[2]);
                    break;

                default:
                    return null;
            }
        }
        if (sdate.length == 2) {

            String[] sd = sdate[1].split(":|\\.");
            switch (sd.length) {
                case 1:
                    return new Date(year, month, day, Integer.parseInt(sd[0]), 0);
                case 2:
                    return new Date(year, month, day, Integer.parseInt(sd[0]), Integer.parseInt(sd[1]));
                case 3:
                    return new Date(year, month, day, Integer.parseInt(sd[0]), Integer.parseInt(sd[1]), Integer.parseInt(sd[2]));
            }
        }

        return new Date(year, month, day);
    }

    public static void setField(Object obj, Map<String, Object> dic) {
        if (obj == null) {
            throw new IllegalArgumentException("obj");
        }
        if (dic == null) {
            throw new IllegalArgumentException("dic");
        }
        Field[] pros = obj.getClass().getFields();
        for (int i = 0; i < pros.length; i++) {
            Field field = pros[i];

            Object value = dic.get(field.getName());
            setField(obj, field, value);
        }

    }

    public static void setField(Object obj, String prototype, Object propertyValue) {
        try {
            Field field = obj.getClass().getField(prototype);
            setField(obj, field, propertyValue);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public static void setField(Object obj, Field prototype, Object propertyValue) {
        Class cls = prototype.getType();
        prototype.setAccessible(true);
        try {
            if (propertyValue instanceof String) {

                if (cls.equals(String.class)) {
                    prototype.set(obj, propertyValue);

                } else {
                    prototype.set(obj, Utility.parse((String) propertyValue, cls));
                }
            } else {

                prototype.set(obj, propertyValue);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static UUID byte2uuid(byte[] b) {
        if (b.length != 16) {
            throw new IllegalArgumentException("Invalid UUID byte[]");
        }


        int _a = ((((b[3] & 0xFF) << 0x18) | ((b[2] & 0xFF) << 0x10)) | ((b[1] & 0xFF) << 8)) | (b[0] & 0xff);
        int _b = (short) (((b[5] & 0xFF) << 8) | (b[4] & 0xFF));
        int _c = (short) (((b[7] & 0xFF) << 8) | (b[6] & 0xFF));


        long msb = _a + 1;
        msb = (msb << 16) + _b;//(_b + 0l);
        msb = (msb << 16) + _c;//(_c + 0l);
        long lsb = 0;

        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (b[i] & 0xff);

        return new UUID(msb, lsb);

    }

    public static int authCode(UUID uuid) {
        String hex = uuid.toString().replace("-", "");

        byte[] b = new byte[16];


        for (int i = 0; i < hex.length(); i = i + 2) {
            String subStr = hex.substring(i, i + 2);
            b[i / 2] = (byte) Integer.parseInt(subStr, 16);

        }

        int _a =
                b[3] & 0xFF |
                        (b[2] & 0xFF) << 8 |
                        (b[1] & 0xFF) << 16 |
                        (b[0] & 0xFF) << 24;

        int _b =
                b[5] & 0xFF |
                        (b[4] & 0xFF) << 8;
        int _c =
                b[7] & 0xFF |
                        (b[6] & 0xFF) << 8;


        return ((_a ^ ((_b << 0x10) | Short.toUnsignedInt((short) _c))) ^ ((b[10] << 0x18) | b[15]));
    }


    private static byte[] uuidByte(UUID uuid) {


        String hex = uuid.toString().replace("-", "");

        byte[] bytes = new byte[16];


        for (int i = 0; i < hex.length(); i = i + 2) {
            String subStr = hex.substring(i, i + 2);
            bytes[i / 2] = (byte) Integer.parseInt(subStr, 16);

        }
        byte[] b = bytes;
        int _a =
                b[3] & 0xFF |
                        (b[2] & 0xFF) << 8 |
                        (b[1] & 0xFF) << 16 |
                        (b[0] & 0xFF) << 24;

        int _b =
                b[5] & 0xFF |
                        (b[4] & 0xFF) << 8;
        int _c =
                b[7] & 0xFF |
                        (b[6] & 0xFF) << 8;


        byte[] header = new byte[]{((byte) _a), ((byte) (_a >> 8)), ((byte) (_a >> 0x10)), ((byte) (_a >> 0x18)), ((byte) _b), ((byte) (_b >> 8)), ((byte) _c), ((byte) (_c >> 8))};

        for (int i = 0; i < 8; i++) {
            b[i] = header[i];
        }
        return b;

    }

    public static UUID uuid(String str) {

        return uuid(str, false);
    }

    public static String qrUrl(String chl) {
        return qrUrl(chl, 300);
    }

    public static String qrUrl(String chl, int width) {
        try {
            if (width <= 0) {
                return String.format("http://oss.365lu.cn/QR/%s?chl=%s", parseEncode(chl.hashCode(), 62), URLEncoder.encode(chl, "utf-8"));
            }
            return String.format("http://oss.365lu.cn/QR/%s?w=%d&chl=%s", parseEncode(chl.hashCode(), 62), width, URLEncoder.encode(chl, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String uuid(UUID id) {


        BASE64Encoder encoder = new BASE64Encoder();
        String config = encoder.encode(uuidByte(id));//Convert.ToBase64String(id.ToByteArray());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < config.length(); i++) {

            char v = config.charAt(i);
            switch (v) {
                case '+':
                    sb.append('-');
                    break;
                case '/':
                    sb.append('_');
                    break;
                case '=':
                    break;
                default:
                    sb.append(v);
                    break;
            }
        }
        return sb.toString();
    }


    public static UUID uuid(String str, boolean ismd5) {
        if (UMC.Data.Utility.isEmpty(str)) {
            return null;
        }
        try {
            switch (str.length()) {
                case 23:
                case 22:
                    StringBuilder sb = new StringBuilder();
                    for (int c = 0; c < str.length(); c++) {

                        char v = str.charAt(c);
                        switch (v) {
                            case '-':
                            case '.':
                                sb.append('+');
                                break;
                            case '_':
                                sb.append('/');
                                break;
                            default:
                                sb.append(v);
                                break;
                        }
                    }
                    switch (sb.length() % 3) {
                        case 1:
                            sb.append("==");
                            break;
                        case 2:
                            sb.append('=');
                            break;
                    }

                    BASE64Decoder decoder = new BASE64Decoder();
                    return byte2uuid(decoder.decodeBuffer(sb.toString()));

                case 38:
                    switch (str.charAt(0)) {
                        case '(':
                        case '{':
                            return UUID.fromString(str);

                    }
                    return null;
                case 36:
                case 32:
                    return UUID.fromString(str);
                default:
                    if (ismd5) {
                        try {
                            MessageDigest m = MessageDigest.getInstance("MD5");
                            m.update(str.getBytes());


                            return byte2uuid(m.digest());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
            }
        } catch (Exception e) {
            if (ismd5) {
                try {
                    MessageDigest m = MessageDigest.getInstance("MD5");
                    m.update(str.getBytes());
                    return byte2uuid(m.digest());
                } catch (Exception e2) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public static <T> T parse(String str, T defautValue) {
        return isNull((T) parse(str, defautValue.getClass()), defautValue);
    }

    private static int _Conver(char c) {
        int d = 0;

        if (c >= 'a') {
            d = (c - 'a') + 10;
        } else if (c >= 'A') {
            d = (c - 'A') + 36;
        } else if (c >= '0') {
            d = (c - '0');
        } else {
            return -1;
        }
        return d;
    }

    private static final String STR_DE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String parseEncode(int value, int p) {
        if (p > 1 && p < 63) {
            long i = Integer.toUnsignedLong(value);
            StringBuilder sb = new StringBuilder();
            long j = 0, p2 = p;
            while (i > p - 1) {
                j = i % p2;
                sb.insert(0, STR_DE62.charAt((int) j));
                i = i / p2;
            }
            sb.insert(0, STR_DE62.charAt((int) i));

            return sb.toString();
        }
        throw new IllegalArgumentException("只支持62以下进制转化");
    }

    public static int parseDecode(String value, int p) {
        long v = 0;
        int l = value.length(), l2 = l;
        while (l > 1) {
            int d = _Conver(value.charAt(l2 - l));
            if (d < 0) {
                return 0;
            }
            double v2 = Math.pow(p, l - 1);
            if (v2 > Integer.MAX_VALUE) {
                return 0;
            }

            v += d * (int) (v2);
            l--;
        }
        int c = _Conver(value.charAt(l2 - l));
        if (c < 0) {
            return 0;
        }
        v += c;//Convert.ToUInt32(c);
//        Long.
        return (int) v;//BitConverter.ToInt32(BitConverter.GetBytes(v), 0);
    }


    public static Map<String, String> queryString(String queryString) {

        Map<String, String> map = new HashMap<>();
        if (isEmpty(queryString) == false) {
            String[] qs = queryString.split("&");
            for (String c : qs) {
                String[] vs = c.split("=");
                if (vs.length > 1) {
                    map.put(vs[0], vs[1]);
                } else {
                    map.put(c, null);
                }
            }
        }
        return map;
    }

    public static Object parse(String str, Class type) {
        if (type.equals(String.class)) {
            return str;
        }
        if (UMC.Data.Utility.isEmpty(str)) {

            return null;
        }
        try {


            switch (type.getName()) {
                case "java.lang.Byte":
                    return Byte.parseByte(str);// Convert.ToSByte(str);
                case "java.lang.Short":
                    return Short.parseShort(str);// Convert.ToByte(str);
                case "java.lang.Integer":
                    return Integer.parseInt(str);
                case "java.lang.Long":
                    return Long.parseLong(str);//.ToUInt16(str);
                case "java.lang.Float":
                    return Float.parseFloat(str);//.ToUInt16(str);
                case "java.lang.Double":
                    return Double.parseDouble(str);
                case "java.util.Date":
                    return strToDateLong(str);
                case "java.util.UUID":
                    if (str.length() == 22) {
                        return uuid(str);
                    } else {
                        return UUID.fromString(str);
                    }
                case "java.util.String":
                    return str;
            }
            if (type.isEnum()) {
                return Enum.valueOf(type, str);
            }
        } catch (NumberFormatException ex) {

        }
        return null;

    }

    /**
     * 加密
     *
     * @param data byte[]
     * @param sn   UUID
     * @return byte[]
     */
    public static byte[] des(String data, UUID sn) {

        byte[] btys = uuidByte(sn);
        byte[] byKey = new byte[8];
        byte[] byIV = new byte[8];
        for (int i = 0; i < 8; i++) {
            byKey[i] = btys[i];
            byIV[i] = btys[i + 8];
        }
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(byKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(byIV);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            return cipher.doFinal(data.getBytes());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String des(byte[] data, UUID sn) {

        byte[] btys = uuidByte(sn);
        byte[] byKey = new byte[8];
        byte[] byIV = new byte[8];
        for (int i = 0; i < 8; i++) {
            byKey[i] = btys[i];
            byIV[i] = btys[i + 8];
        }

        try {

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(byKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(byIV);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] retByte = cipher.doFinal(data);

            return new String(retByte);

        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isNull(Boolean b, boolean defaultValue) {
        if (b == null) {
            return defaultValue;
        }
        return b;
    }

    public static int isNull(Integer b, int defaultValue) {
        if (b == null) {
            return defaultValue;
        }
        return b;
    }

    public static <T> T isNull(T b, T defaultValue) {
        if (b == null) {
            return defaultValue;
        }
        return b;
    }

    public static float isNull(Float b, float defaultValue) {
        if (b == null) {
            return defaultValue;
        }
        return b;
    }

    public static double isNull(Double b, double defaultValue) {
        if (b == null) {
            return defaultValue;
        }
        return b;
    }

    public static boolean IsEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return Pattern.matches("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$", email);

    }

    public static boolean IsPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        Regex f = new Regex("^(1[3-8])\\d{9}$");
//        f.compile();f.

        if (Pattern.matches("^(1[3-8])\\d{9}$", phone)) {
            return true;
        } else if (Pattern.matches("^\\d{7,12}$", phone)) {
            switch (phone.length()) {
                case 7:
                case 8:
                    return phone.startsWith("0") == false;
                case 11:
                case 12:
                    return phone.startsWith("0");
            }
        }
        return false;
    }

    public static byte isNull(Byte b, byte defaultValue) {
        if (b == null) {
            return defaultValue;
        }
        return b;
    }

    public static String isNull(String b, String defaultValue) {
        if (isEmpty(b)) {
            return defaultValue;
        }
        return b;
    }

    public static short isNull(Short b, short defaultValue) {
        if (b == null) {
            return defaultValue;
        }
        return b;
    }

    public static long isNull(Long b, long defaultValue) {
        if (b == null) {
            return defaultValue;

        }
        return b;
    }

    //private  Byte []
    public static String md5(String dataStr) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes("UTF8"));
            byte s[] = m.digest();
            String md5code = new BigInteger(1, s).toString(16);

            for (int i = 0; i < 32 - md5code.length(); i++) {
                md5code = "0" + md5code;
            }
            return md5code;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static <T> int findIndex(Collection<T> collection, Predicate<T> predicate) {
        int index = -1;
        for (T e : collection) {
            index++;
            if (predicate.test(e)) {
                return index;
            }
        }
        return index;
    }

    public static <T> int findIndex(T[] collection, Predicate<T> predicate) {
        int index = -1;
        for (T e : collection) {
            index++;
            if (predicate.test(e)) {
                return index;
            }
        }
        return index;
    }

    public static <T> T find(T[] collection, Predicate<T> predicate) {
        // int index = -1;
        for (T e : collection) {
            // index++;
            if (predicate.test(e)) {
                return e;
            }
        }
        return null;
    }

    public static <T> T find(Collection<T> collection, Predicate<T> predicate) {
        // int index = -1;
        for (T e : collection) {
            // index++;
            if (predicate.test(e)) {
                return e;
            }
        }
        return null;
    }

    public static <T> List<T> findAll(Collection<T> collection, Predicate<T> predicate) {
        List<T> list = new LinkedList<>();
        for (T e : collection) {
            // index++;
            if (predicate.test(e)) {
                list.add(e);
            }
        }
        return list;
    }

    public static <T> List<T> findAll(T[] collection, Predicate<T> predicate) {
        List<T> list = new LinkedList<>();
        for (T e : collection) {
            // index++;
            if (predicate.test(e)) {
                list.add(e);
            }
        }
        return list;
    }

    public static <T> boolean exists(Collection<T> collection, Predicate<T> predicate) {
        for (T e : collection) {
            // index++;
            if (predicate.test(e)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean exists(T[] collection, Predicate<T> predicate) {
        for (T e : collection) {
            if (predicate.test(e)) {
                return true;
            }
        }
        return false;
    }

    public static String format(String format, Map map) {
        return format(format, map, null);
    }

    public static String format(String format, Map map, String empty) {
        if (isEmpty(format)) {
            return "";
        }

        Set set = map.keySet();
        int start = 0, end = 0, l = format.length(), i = 0;
        boolean isStart = true;
        StringBuilder sb = new StringBuilder();
        while (i < l) {
            char k = format.charAt(i);//[i];
            switch (k) {
                case '{':
                    isStart = true;
                    start = end = i;
                    break;
                case '}':
                    if (isStart && start < end) {
                        String key = format.substring(start + 1, end);
                        Object vKey = find(set, g -> key.equalsIgnoreCase(g.toString()));
                        if (vKey != null) {
                            int start2 = sb.length() - 1 - key.length();
                            sb.replace(start2, start2 + key.length() + 1, "");

                            sb.append(map.get(vKey));
                            start = end = i;
                            i++;
                            continue;
                        } else if (empty != null) {
                            int start2 = sb.length() - 1 - key.length();
                            sb.replace(start2, start2 + key.length() + 1, "");

                            sb.append(empty);
                            start = end = i;
                            i++;
                            continue;
                        }
                    }
                    start = end = i;

                    isStart = false;
                    break;
                case ' ':
                case '\t':
                case '\b':
                case '\n':
                case '\r':
                    isStart = false;
                    start = end = i;
                    break;
                default:
                    end = i;
                    break;
            }
            i++;
            sb.append(k);
        }
        return sb.toString();

    }

}
