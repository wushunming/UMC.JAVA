package UMC.Data.Sql;

import java.util.List;
import java.util.Map;

public interface IGrouper<T> extends IScript {

    T single();
    /// <summary>
    /// 查询分组
    /// </summary>
    /// <returns></returns>
    List<Map> query();
    /// <summary>
    /// 查询分组
    /// </summary>
    void query(IDataReader<T> reader);
    /// <summary>
    /// 排序
    /// </summary>
    IGroupOrder order();

    IGrouper<T> count(String asName);
    /// <summary>
    /// 求记录数,对应的字段为"G"+(i+1)，i为统计次数,例如：e.groupBy("field'}).count(),则Count的列名为"G1"
    /// </summary>
    /// <returns></returns>
    IGrouper<T> count(T field);
    /// <summary>
    /// 求记录数,对应的字段为"G"+(i+1)，i为统计次数,例如：e.groupBy("field'}).count(),则Count的列名为"G1"
    /// </summary>
    /// <returns></returns>
    IGrouper<T> count();
    /// <summary>
    /// 求和,对应的字段为"G"+(i+1)，i为统计次数,例如：e.groupBy("field'}).sum(),则Sum的列名为"G1"
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    IGrouper<T> sum(String field);
    IGrouper<T> sum(String field, String asName);
    /// <summary>
    /// 求平均,对应的字段为"G"+(i+1)，i为统计次数,例如：e.groupBy("field'}).sum(field),则Sum的列名为"G1"
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    IGrouper<T> avg(String field);
    IGrouper<T> avg(String field, String asName);
    /// <summary>
    /// 求最大值,对应的字段为"G"+(i+1)，i为统计次数,例如：e.groupBy("field'}).max(field),则Max的列名为"G1"
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    IGrouper<T> max(String field);
    IGrouper<T> max(String field, String asName);
    /// <summary>
    /// 求最小值,,对应的字段为"G"+(i+1)，i为统计次数,例如：e.groupBy("field'}).min(field),则Min的列名为"G1"
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    IGrouper<T> min(String field);
    IGrouper<T> min(String field, String asName);
    /// <summary>
    /// 求和,对应的字段值为实体非空字段
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    IGrouper<T> sum(T field);
    /// <summary>
    /// 求平均,对应的字段值为实体非空字段
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    IGrouper<T> avg(T field);
    /// <summary>
    /// 求最大值,对应的字段值为实体非空字段
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    IGrouper<T> max(T field);
    /// <summary>
    /// 求最小值,对应的字段值为实体非空字段
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    IGrouper<T> min(T field);
}

