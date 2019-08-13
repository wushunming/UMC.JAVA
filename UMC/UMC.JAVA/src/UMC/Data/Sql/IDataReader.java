package UMC.Data.Sql;

import java.sql.ResultSet;



public interface IDataReader<T> {
    void  reader(T item);
}