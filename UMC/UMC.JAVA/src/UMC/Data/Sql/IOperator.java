package UMC.Data.Sql;

public interface IOperator<T> {

//     interface IField<E> {
//        E get();
//    }


    /// <summary>
    /// 实体不等于 &gt;&gt;
    /// </summary>
    /// <param name="field">非空属性实体</param>
    /// <returns></returns>
    IWhere<T> unEqual(T field);
//    IWhere<T> unEqual(IField<T> field);

    /// <summary>
    /// 实体等于 =
    /// </summary>
    /// <param name="field">非空属性实体</param>
    IWhere<T> equal(T field);
//    IWhere<T> equal(IField<T> field);

    /// <summary>
    /// 实体大于 &gt;
    /// </summary>
    /// <param name="field">非空属性实体</param>
    IWhere<T> greater(T field);
//    IWhere<T> greater(IField<T> field);

    /// <summary>
    /// 实体小于&lt;
    /// </summary>
    /// <param name="field">非空属性实体</param>
    IWhere<T> smaller(T field);
//    IWhere<T> smaller(IField<T> field);

    /// <summary>
    /// 实体大于等于 &gt;=
    /// </summary>
    /// <param name="field">非空属性实体</param>
    IWhere<T> greaterEqual(T field);
//    IWhere<T> greaterEqual(IField<T> field);

    /// <summary>
    /// 实体小于等于 &lt;=
    /// </summary>
    /// <param name="field">非空属性实体</param>
    IWhere<T> smallerEqual(T field);
//    IWhere<T> smallerEqual(IField<T> field);

    /// <summary>
    /// 不等于&lt;&gt;
    /// </summary>
    IWhere<T> unEqual(String field, Object value);

    /// <summary>
    /// 等于 =
    /// </summary>
    IWhere<T> equal(String field, Object value);

    /// <summary>
    /// 大于 &gt;
    /// </summary>
    IWhere<T> greater(String field, Object value);

    /// <summary>
    /// 小于&lt;
    /// </summary>
    IWhere<T> smaller(String field, Object value);

    /// <summary>
    /// 大于等于 &gt;=
    /// </summary>
    IWhere<T> greaterEqual(String field, Object value);

    /// <summary>
    /// 小于等于 &lt;=
    /// </summary>
    IWhere<T> smallerEqual(String field, Object value);

    /// <summary>
    /// 不等于
    /// </summary>
    IWhere<T> notLike(String field, String value);

    /// <summary>
    /// like
    /// </summary>
    IWhere<T> like(String field, String value);

    /// <summary>
    /// like
    /// </summary>
    IWhere<T> like(T field, boolean schar);
//    IWhere<T> like(IField<T> field, boolean schar);

    /// <summary>
    /// like
    /// </summary>
    IWhere<T> like(T field);
//    IWhere<T> like(IField<T> field);

    /// <summary>
    /// in
    /// </summary>
    IWhere<T> in(String field, Object... values);

    /// <summary>
    /// in
    /// </summary>
    /// <param name="field">只能一个非空字段的值</param>
    IWhere<T> in(T field, Object... values);
//    IWhere<T> in(IField<T> field, Object... values);

    /// <summary>
    /// in
    /// </summary>
    IWhere<T> in(String field, Script script);

    /// <summary>
    /// Not in
    /// </summary>
    IWhere<T> notIn(String field, Object... values);

    /// <summary>
    /// Not in
    /// </summary>
    /// <param name="field">只能一个非空字段的值</param>
    IWhere<T> notIn(T field, Object... values);
//    IWhere<T> notIn(IField<T> field, Object... values);

    /// <summary>
    /// Not in
    /// </summary>
    IWhere<T> notIn(String field, Script script);

    /// <summary>
    /// 创建带小括号 SQL WHERE条件，例如 ：(field1=1)
    /// </summary>
    /// <returns></returns>
    IWhere<T> contains();

}
