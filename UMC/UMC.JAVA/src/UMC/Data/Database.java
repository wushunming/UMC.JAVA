package UMC.Data;

import UMC.Data.Sql.IObjectEntity;
import UMC.Data.Sql.ISqler;
import UMC.Data.Sql.SqlUtils;

import java.sql.Connection;
import java.sql.SQLException;


public final class Database {

    public interface ITransaction {
        void execute(Connection connection);
    }

    public interface IProgress {
        void execute(String sql, ITransaction transaction);
    }

    private IProgress progress = new IProgress() {
        @Override
        public void execute(String sql, ITransaction transaction) {
            Connection connection = getConnection();
            try {
                transaction.execute(connection);
            } finally {
                if (!IsTran && autoClose) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
    };

    private boolean IsTran = false, autoClose = true;
    private Connection _cont;

    private UMC.Data.Sql.DbProvider DbProvider;

    /** 数据库差异配置器
     * @return
     */
    public UMC.Data.Sql.DbProvider provider() {
        return DbProvider;
    }

    /**
     *  如果是采用事务初始化，则提交事务
     */
    public void commit() {
        if (IsTran) {
            Connection connection = this.getConnection();
            IsTran = false;
            autoClose = true;
            try {
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {

                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }


    /**
     * 关闭数据库接连
     */
    public void close() {
        try {
            autoClose = true;
            _cont.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 如果是采用事务初始化，则回退事务
     */
    public void rollback() {
        if (IsTran) {
            try {
                IsTran = false;
                autoClose = true;
                _cont.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {

                try {
                    _cont.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Database(UMC.Data.Sql.DbProvider dbProvider) {
        this.DbProvider = dbProvider;


    }

    /**
     * 打开数据库，
     *
     * @return
     */
    public void open() {
        if (autoClose)
            if (!IsTran) {
                this._cont = getConnection();
                autoClose = false;
            }
    }

    private Connection getConnection() {
        if (!IsTran) {
            if (autoClose) {
                try {
                    this._cont = this.DbProvider.instance();// DriverManager.getConnection(this.DbProvider.conntionString(), this.DbProvider.user(), DbProvider.password());

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return _cont;
    }

    /**
     * 创建默认DbProvider实体访实例,默认配置节点是defaultDbProvider
     *
     * @return
     */
    public static Database instance() {
        return instance("defaultDbProvider");
    }


    /**
     * 创建DataBase
     *
     * @param dbProvider 数据差异提供者
     * @return
     */
    public static Database instance(UMC.Data.Sql.DbProvider dbProvider) {
        return new Database(dbProvider);
    }


    /**
     * 创建默认数据库实体访实例
     *
     * @param providerName 配置节点名,在App_Data/WebADNuke/database.xml中配置
     * @return
     */
    public static Database instance(String providerName) {
        Provider provider = ProviderConfiguration.configuration("database").get(providerName);
        UMC.Data.Sql.DbProvider provider1 = (UMC.Data.Sql.DbProvider) Utility.createInstance(provider);
        return new Database(provider1);
    }


    /**
     * 使用事务，如果已经使用了事务，则返回false，如果没有使用事务，则取用事务并返回true
     *
     * @return
     */
    public boolean begin() {

        if (IsTran == false) {
            Connection connection = getConnection();
            IsTran = true;
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return true;
        }

        return false;
    }


    /**
     * 创建的Sql语句的查询器
     *
     * @return
     */
    public ISqler sqler() {
        return SqlUtils.sqler(this.progress, this.DbProvider, true);
    }


    /**
     * 创建的Sql语句的查询器
     *
     * @param pfx 是否加前前缀
     * @return
     */
    public ISqler sqler(boolean pfx) {
        return SqlUtils.sqler(this.progress, this.DbProvider, pfx);
    }

    /**
     * 创建实体综合管理适配器
     *
     * @param tClass  实体类型
     * @param <T>实体类型
     * @return
     */
    public <T> IObjectEntity<T> objectEntity(Class<T> tClass)

    {
        return SqlUtils.objectEntity(this.sqler(), this.DbProvider, tClass);

    }


}
