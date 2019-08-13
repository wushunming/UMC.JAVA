package UMC.Data.Sql;

public interface IGroupOrder<T> {
    IGroupOrder<T> desc(String fieldName);
    /// <summary>
    ///  asc
    /// </summary>
    /// <param name="fieldName">排序字段</param>
    /// <returns></returns>
    IGroupOrder<T> asc(String fieldName);

    /// <summary>
    /// 清空排序设置
    /// </summary>
    IGroupOrder<T> clear();
    IGroupOrder<T> asc(T field);
    IGroupOrder<T> desc(T field);
    IGrouper<T> entities();

}
