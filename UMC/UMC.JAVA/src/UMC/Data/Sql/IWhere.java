package UMC.Data.Sql;

public interface IWhere<T> {

    Object get(String name);
    /**
     * 移除查询参数
     *
     * @return
     */

    int remove(String name);


    /**
     * @return
     */
    int size();


    /**
     * @return
     */
    IObjectEntity<T> entities();

    /**
     * @return
     */
    IWhere<T> reset();
    /**
     * 安实体移除查询参数
     *
     * @return
     */
    IWhere<T> remove(T field);

    /**
     * 替换参数
     *
     * @return
     */
    IWhere<T> replace(T field);


    /** 配置Sql表达式的查询条件
     * @param expression sql表达式
     * @param paramers
     * @return
     */
    IWhere<T> or(String expression, Object... paramers);



    /** 配置Sql表达式的查询条件
     * @param expression sql表达式
     * @param paramers
     * @return
     */
    IWhere<T> and(String expression, Object... paramers);

    /**
     * 实体条件Or
     *
     * @return
     */
    IOperator<T> or();

    /**
     * @return
     */
    IOperator<T> and();


    /**
     * 实体条件Or
     *
     * @return
     */
    IWhere<T> or(T field);

    /**
     * 实体条件And
     *
     * @return
     */
    IWhere<T> and(T field);

    /**
     * 创建带小括号 SQL WHERE条件，例如 ：(field1=1 AND field2=2)
     *
     * @return
     */
    IWhere<T> contains();

}
