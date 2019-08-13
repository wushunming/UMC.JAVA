package UMC.Data;

import UMC.Data.Entities.Session;
import UMC.Data.Sql.IObjectEntity;
import UMC.Security.Identity;

import java.util.Date;
import java.util.UUID;

public class Configuration<T> {
    public T Value;
    public String Key;
    public String ContentType;
    private UUID _user_id;
    private Class<T> tClass;

    public Configuration(String sessionKey, Identity id, Class<T> tClass) {
        this.Key = sessionKey;

        this.tClass = tClass;
        Session se = GSession(sessionKey, id);
        if (se != null) {
            this.ContentType = se.ContentType;

            if (tClass.equals(String.class)) {
                Object obj = se.Content;
                this.Value = (T) obj;
            } else {
                this.Value = JSON.deserialize(se.Content, tClass);
            }
            this.ModifiedTime = se.UpdateTime;

            if (this.ModifiedTime == null) {
                this.ModifiedTime = new Date();
            }
            _user_id = se.user_id;
        }
    }

    public Configuration(String sessionKey, Class<T> tClass) {

        this.tClass = tClass;
        this.Key = sessionKey;
        Session se = GSession(sessionKey, null);
        if (se != null) {
            this.ContentType = se.ContentType;


            if (tClass.equals(String.class)) {
                Object obj = se.Content;
                this.Value = (T) obj;
            } else {
                this.Value = JSON.deserialize(se.Content, tClass);
            }
            this.ModifiedTime = se.UpdateTime;
            if (this.ModifiedTime == null) {
                this.ModifiedTime = new Date();
            }
        }
    }

    public Date ModifiedTime;


    private static Session GSession(String SessionKey, Identity user) {
        if (Utility.isEmpty(SessionKey) == false) {
            IObjectEntity<UMC.Data.Entities.Session> sessionEneity = Database.instance().objectEntity(UMC.Data.Entities.Session.class);
            Session iwh = new Session();
            iwh.SessionKey = SessionKey;

            if (user != null)
                iwh.user_id = user.id();
            sessionEneity.where().and().equal(iwh);


            Session[] sess = sessionEneity.query();
            switch (sess.length) {
                case 0:
                    return null;
                case 1:
                    return sess[0];
                default:
                    if (user == null) {
                        return sess[0];
                    } else {
                        if (sess[0].user_id == user.id()) {
                            return sess[0];
                        } else {
                            return sess[1];
                        }
                    }
            }
        }
        return null;
    }

    public Configuration(T value, String sessionKey) {
        this.Value = value;
        this.Key = sessionKey;
    }

    public void commit() {
        this.commit(this._user_id);
    }

    public void commit(Identity id) {
        this.commit(id.id());
    }

    public void commit(Identity id, String contentType) {
        this.ContentType = contentType;
        this.commit(id.id());
    }

    public void commit(T value, Identity id) {
        this.Value = value;
        this.commit(id, "app/json");
    }

    public void commit(T value, UUID... ids) {
        this.commit(ids);
    }

    public void commit(UUID... ids) {


        Session session = new Session();
        session.UpdateTime = new Date();
        session.user_id = ids[ids.length - 1];
        session.ContentType = this.ContentType;
        session.SessionKey = this.Key;
        if (Utility.isEmpty(session.ContentType)) {
            session.ContentType = "text/javascript";
        }


        if (this.Value instanceof String) {
            session.Content = ((String) this.Value);

        } else {
            session.Content = JSON.serialize(this.Value);
        }
        this.ModifiedTime = new Date();

        Database database = Database.instance();
        database.begin();
        try {
            IObjectEntity<Session> sessionEneity = database.objectEntity(Session.class);
            sessionEneity.where().and().equal("SessionKey", this.Key);

            sessionEneity.delete();

            sessionEneity.where().and().in("user_id", ids);

            sessionEneity.insert(session);

            database.commit();
        } catch (Exception e) {
            database.rollback();
        }
    }
}