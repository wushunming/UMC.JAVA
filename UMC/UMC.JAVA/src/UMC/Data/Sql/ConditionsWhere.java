package UMC.Data.Sql;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


 class ConditionsWhere<T> implements  IWhere<T>
{
    public Conditions Wherer;
    public List<Object> FormatSqlText(StringBuilder sb, List<Object> lp)
    {
        return Wherer.FormatSqlText(sb, lp);
    }
    public ConditionsWhere(IObjectEntity<T> entity)
    {
        this.Wherer = new Conditions();
        this.IWherer = this.Wherer; ;
        this.entity = entity;
    }
    IObjectEntity<T> entity;
    public ConditionsWhere(Conditions con, IObjectEntity<T> entity)
    {
        this.Wherer = con;
        this.IWherer = con;
        this.entity = entity;
    }
    Conditions IWherer;



    public IWhere<T> reset()
    {
        IWherer.Reset();
        return this;
    }

    @Override
    public Object get(String name) {
        return IWherer.get(name);
    }

    public   int remove(String name)
    {
        return IWherer.Remove(name);
    }

    public  IWhere<T> or(String expression, Object... paramers)
    {
        this.IWherer.Or(expression, paramers);
        return this;
    }

    public  IWhere<T> and(String expression, Object... paramers)
    {
        this.IWherer.And(expression, paramers);
        return this;
    }

    public   IOperator<T> or()
    {
        return new OperatorClass<T>(new Operator(this.Wherer, true), this);
    }

    public  IOperator<T> and()
    {
        return new OperatorClass<T>(new Operator(this.Wherer, false), this);
    }

    public IWhere<T> or(T item)
    {
        Map<String, Object> dic = SqlUtils.fieldMap(item);
        Iterator<String> em = dic.keySet().iterator();
        while (em.hasNext()) {
            String key = em.next();

            this.IWherer.Or().Equal(key, dic.get(key));

        }
//        return constr;
//        var dic = CBO.fieldMap(item);
//        var em = dic.GetEnumerator();
//        while (em.MoveNext())
//        {
//            this.IWherer.or().equal(em.current.key, em.current.Value);
//        }
        return this;
    }

    public IWhere<T> and(T item)
    {
        Map<String, Object> dic = SqlUtils.fieldMap(item);
        Iterator<String> em = dic.keySet().iterator();
        while (em.hasNext()) {
            String key = em.next();

            this.IWherer.And().Equal(key, dic.get(key));

        }
        return this;

    }

    public  IWhere<T> remove(T item)
    {
        Map<String, Object> dic = SqlUtils.fieldMap(item);
        Iterator<String> em = dic.keySet().iterator();
        while (em.hasNext()) {
            String key = em.next();

            this.IWherer.Remove(key);

        }
        return this;
    }



    public   IWhere<T> replace(T field)
    {
        Map<String, Object> dic = SqlUtils.fieldMap(field);
        Iterator<String> em = dic.keySet().iterator();
        while (em.hasNext()) {
            String key = em.next();
            if (this.Wherer.ContainsKey(key))
            {
                IWherer.set(key,dic.get(key));
            }
        }
        return this;

    }


    public   IObjectEntity<T> entities()
    {
        return this.entity;
    }






    //IWhere IWhere.reset()
    //{
    //    this.IWherer.reset();
    //    return this;
    //}

    //int IWhere.remove(String name)
    //{
    //    return this.IWherer.remove(name);
    //}

    public int size()
    {
        return this.IWherer.getCount();
    }



    public IWhere<T> contains()
    {
        Conditions p = this.IWherer.Contains();

        return new ConditionsWhere<T>(p, this.entity);

    }



}
