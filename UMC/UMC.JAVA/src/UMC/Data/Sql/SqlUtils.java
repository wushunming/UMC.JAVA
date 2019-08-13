package UMC.Data.Sql;

import UMC.Data.Database;
import UMC.Data.Utility;

import java.util.Map;

public class SqlUtils {
    public static ISqler sqler(Database.IProgress progress, DbProvider provider, boolean autoPfx) {
        return new Sqler(progress, provider, autoPfx);
    }

    public static <T> IObjectEntity objectEntity(ISqler sqler, DbProvider provider, Class<T> tClass) {
        return new ObjectEntity<T>(sqler, provider, tClass);
    }

    public static Map<String, Object> fieldMap(Object obj) {
        return Utility.fieldMap(obj);
    }

}

