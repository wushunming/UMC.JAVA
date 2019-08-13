package UMC.Data.Sql;

public interface IOrder<T> {
    /// <summary>
    /// desc
    /// </summary>
    /// <param name="fieldName">排序字段</param>
    /// <returns></returns>
    IOrder<T> desc(String fieldName);
    /// <summary>
    ///  asc
    /// </summary>
    /// <param name="fieldName">排序字段</param>
    /// <returns></returns>
    IOrder<T> asc(String fieldName);

    /// <summary>
    /// 清空排序设置
    /// </summary>
    IOrder<T> clear();
    IOrder<T> asc(T field);
    IOrder<T> desc(T field);
    IObjectEntity<T> entities();
}
