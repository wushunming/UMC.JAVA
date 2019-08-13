package UMC.Data.Sql;

import javax.swing.*;
import java.util.Map;
import java.util.function.Predicate;

public interface IObjectEntity<T> extends IScript {
    /// <summary>
    /// 创建查询脚本
    /// </summary>
    /// <param name="field"></param>
    /// <returns></returns>
    Script script(T field);

    /// <summary>
    /// 创建查询脚本
    /// </summary>
    /// <param name="field"></param>
    /// <returns></returns>
    Script script(String field);

    /// <summary>
    /// 分组查询
    /// </summary>
    /// <param name="fields">分组字段名</param>
    /// <returns></returns>
    IGrouper<T> groupBy(String... fields);

    /// <summary>
    /// 分组查询
    /// </summary>
    /// <param name="field">分组字段实体</param>
    /// <returns></returns>
    IGrouper<T> groupBy(T field);

    /// <summary>
    /// 插入
    /// </summary>
    /// <param name="items"></param>
    /// <returns></returns>
    int insert(T... items);

    /// <summary>
    /// 更新实体，如果字段“fields”的长度为0，则采用非空属性值规则更新对应的字段，否则更新指定的的字段
    /// </summary>
    /// <param name="item">实体</param>
    /// <param name="fields">更新的字段</param>
    /// <returns>返回受影响的行数</returns>
    int update(T item, String... fields);

    /// <summary>
    /// 更新实体，如果字段“fields”的长度为0，则采用非空属性值规则更新对应的字段，否则更新指定的的字段
    /// </summary>
    /// <param name="format">更新值格式：其中{0}表示字段，{1}表示参数值</param>
    /// <param name="item">实体</param>
    /// <param name="fields">更新的字段</param>
    /// <returns>返回受影响的行数</returns>
    int update(String format, T item, String... fields);

    /// <summary>
    /// 采用字典更新实体
    /// </summary>
    /// <param name="fieldValues">字段字典对</param>
    /// <returns></returns>
    int update(Map fieldValues);

    /// <summary>
    /// 采用字典更新实体
    /// </summary>
    /// <param name="format">更新值格式：其中{0}表示字段，{1}表示参数值</param>
    /// <param name="fieldValues">字段字典对</param>
    /// <returns></returns>
    int update(String format, Map fieldValues);

    /// <summary>
    /// 删除
    /// </summary>
    /// <returns></returns>
    int delete();

    /// <summary>
    /// 排序
    /// </summary>
    IOrder<T> order();

    /// <summary>
    /// 查询一个字段，如果是“*”，必返回DataRow[]数据
    /// </summary>
    /// <returns></returns>
    Object[] query(String field);

    /// <summary>
    /// 自定义处理一个字段查询的只读结果集
    /// </summary>
    void query(String field, IResultReader dr);

    /// <summary>
    /// 查询
    /// </summary>
    /// <returns></returns>
    void query(IDataReader<T> dr);

    /// <summary>
    /// 查询
    /// </summary>
    /// <param name="field">字段实例</param>
    /// <returns></returns>
    void query(T field, IDataReader<T> dr);

    /// <summary>
    /// 除重查询
    /// </summary>
    /// <param name="field">字段实例</param>
    /// <returns></returns>
    //void QueryDistinct(T field, DataReader<T> dr);
    /// <summary>
    /// 查询
    /// </summary>
    /// <returns></returns>
    T[] query();

    /// <summary>
    /// 查询实段实例
    /// </summary>
    /// <param name="field">字段实例</param>
    /// <returns></returns>
    T[] query(T field);

    /// <summary>
    /// 查询实体集分页
    /// </summary>
    /// <param name="start">开始记录</param>
    /// <param name="limit">记录数</param>
    void query(int start, int limit, IDataReader<T> dr);

    /// <summary>
    /// 查询实体集分页
    /// </summary>
    /// <param name="start">开始记录</param>
    /// <param name="limit">记录数</param>
    T[] query(int start, int limit);

    /// <summary>
    /// 查询实体集分页
    /// </summary>
    /// <param name="field">字段实例</param>
    /// <param name="start">开始记录</param>
    /// <param name="limit">记录数</param>
    /// <returns></returns>
    T[] query(T field, int start, int limit);

    /// <summary>
    /// 查询实体集分页
    /// </summary>
    /// <param name="field">字段实例</param>
    /// <param name="start">开始记录</param>
    /// <param name="limit">记录数</param>
    /// <returns></returns>
    void query(T field, int start, int limit, IDataReader<T> dr);

    //void QueryDistinct(T field, int start, int limit, DataReader<T> dr);
    /// <summary>
    /// 查询头一个实体
    /// </summary>
    /// <returns></returns>
    T single();        /// <summary>

    /// 查询头一个实体
    /// </summary>
    /// <param name="field">字段实例</param>
    /// <returns></returns>
    T single(T field);

    /// <summary>
    /// 查询一个字段，如果是“*”则返回一行记录的字典对
    /// </summary>
    /// <returns></returns>
    Object single(String field);

    /// <summary>
    /// 查询一个字段，返回一行记录的字典对
    /// </summary>
    /// <param name="field"></param>
    /// <returns></returns>
    Map single(String... field);

    /// <summary>
    /// 求记录的个数
    /// </summary>
    /// <returns></returns>
    int count();

    /// <summary>
    /// 求和
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    Object sum(String field);

    /// <summary>
    /// 求和
    /// </summary>
    /// <param name="field"></param>
    /// <returns></returns>
    T sum(T field);

    /// <summary>
    /// 求平均
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    Object avg(String field);

    /// <summary>
    /// 求平均
    /// </summary>
    T avg(T field);

    /// <summary>
    /// 求最大值
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    Object max(String field);

    /// <summary>
    /// 求最大值
    /// </summary>
    T max(T field);

    /// <summary>
    /// 求最小值
    /// </summary>
    /// <param name="field">字段</param>
    /// <returns></returns>
    Object min(String field);

    /// <summary>
    /// 求最小值
    /// </summary>
    T min(T field);

    /// <summary>
    /// 查询条件
    /// </summary>
    IWhere<T> where();

    /// <summary>
    /// 如果where返回值，则运行@true
    /// </summary>
    /// <param name="where"></param>
    /// <param name="true"></param>
    /// <returns></returns>
    IObjectEntity<T> iff(Predicate<IObjectEntity<T>> where,  IAction<IObjectEntity<T>> True);

    /// <summary>
    /// 如果where返回值，则运行@true，否则的运行@false
    /// </summary>
    IObjectEntity<T> iff(Predicate<IObjectEntity<T>> where, IAction<IObjectEntity<T>> True, IAction<IObjectEntity<T>> False);

}
