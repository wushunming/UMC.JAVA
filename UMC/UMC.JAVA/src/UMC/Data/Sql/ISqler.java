package UMC.Data.Sql;

import com.sun.javafx.collections.MappingChange;

import java.util.List;
import java.util.Map;

public interface ISqler extends IScript {
    /**执行 SQL 语句,并返回影响行数
     * @param sqlText 格式化的sql脚本
     * @param paramers 参数
     */
    int executeNonQuery(String sqlText, Object... paramers);


    /**返回的第一行第一列
     * @param sqlText 格式化的sql脚本
     * @param paramers 参数
     */
    Object executeScalar(String sqlText, Object... paramers);


    /**返回的数据集合
     * @param sqlText 格式化的sql脚本
     * @param paramers 参数
     */
    List<Map> execute(String sqlText, Object... paramers);



    /**返回分页的数据集合
     * @param sqlText 格式化的sql脚本
     * @param start 开始位置
     * @param limit 记录数
     * @param paramers 参数
     */
    List<Map> execute(String sqlText, int start, int limit, Object... paramers);



    /**返回查询的第一行并把转化为字典
     * @param sqlText 格式化的sql脚本
     * @param paramers 参数
     */
    Map executeSingle(String sqlText, Object... paramers);


    /**把查询到的字段转化对应的单个实体
     * @param tClass 泛型类
     * @param sqlText 格式化的sql脚本
     * @param paramers 参数
     * @param <T> 数据实体类
     */
    <T> T executeSingle(Class<T> tClass, String sqlText, Object... paramers);


    /**返回分页的数据表
     * @param tClass 泛型类
     * @param sqlText 格式化的sql脚本
     * @param paramers 参数
     * @param <T> 数据实体类
     */
    <T> List<T> execute(Class<T> tClass, String sqlText, Object... paramers);


    /**自定义处理一个查询只读的结果集
     * @param sqlText 格式化的sql脚本
     * @param reader 处理数据集代理
     * @param paramers 参数
     */
    void execute(String sqlText, IResultReader reader, Object... paramers);

    /**自定义处理一个查询只读的结果集
     * @param tClass 泛型类
     * @param sqlText 格式化的sql脚本
     * @param reader 处理数据集代理
     * @param paramers 参数
     * @param <T> 数据实体类
     */
    <T> void execute(Class<T> tClass, String sqlText, IDataReader<T> reader, Object... paramers);

    /**返回分页的数据表
     * @param tClass 泛型类
     * @param sqlText 格式化的sql脚本
     * @param start 开始位置
     * @param limit 记录数
     * @param paramers 参数
     * @param <T>
     */
    <T> List<T> execute(Class<T> tClass, String sqlText, int start, int limit, Object... paramers);
    /**返回分页的数据表
     * @param sqlText 格式化的sql脚本
     * @param start 开始位置
     * @param limit 记录数
     * @param reader 处理数据集代理
     * @param paramers 参数
     * @param <T>
     */
    <T> void execute(String sqlText, int start, int limit, IDataReader<T> reader, Object... paramers);
    /***
     * 处理分页的数据集，用ResultSet处理
     * @param sqlText 格式化的sql脚本
     * @param start 开始位置
     * @param limit 记录数
     * @param reader 处理数据集代理
     * @param paramers 参数
     */
    void execute(String sqlText, int start, int limit, IResultReader reader, Object... paramers);

    /**
     * 批量执行脚本
     * @param scripts 脚本参数
     */
    void execute(Script... scripts);


}
