package UMC.Data.Sql;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class Sequencer {
    private List<SequenceValue> list;

    public Sequencer() {
        list = new LinkedList<>();

    }

    Sequencer Order(String protortypeName, Sequence view) {
        SequenceValue compost = new SequenceValue(protortypeName, view);
        list.add(compost);
        return this;
    }

    public void FormatSqlText(StringBuilder sb) {
        if (list.size() > 0) {
            sb.append(" ORDER BY ");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    sb.append(",");

                }
                SequenceValue value = list.get(i);

                sb.append(" ");
                sb.append(value.Name);
                sb.append(" ");
                sb.append(value.Value.toString());

            }
        }
    }


    class SequenceValue {
        public SequenceValue(String name, Sequence sequence) {
            this.Value = sequence;
            this.Name = name;
        }

        public String Name;
        public Sequence Value;
    }


    public Sequencer Desc(String fieldName) {
        return this.Order(fieldName, Sequence.Desc);
    }

    public Sequencer Asc(String fieldName) {
        return this.Order(fieldName, Sequence.Asc);
    }

    public Sequencer Clear() {
        list.clear();
        return this;
    }

    enum Sequence {
        Desc,
        Asc,
    }
}

class GSequencer<T> implements IGroupOrder<T> {
    IGrouper<T> entity;
    Sequencer sequencer;

    public GSequencer(IGrouper<T> entity) {
        this.sequencer = new Sequencer();
        ;
        this.entity = entity;
    }

    public void FormatSqlText(StringBuilder sb) {
        sequencer.FormatSqlText(sb);
    }


    public IGroupOrder<T> desc(String fieldName) {
        sequencer.Desc(fieldName);
        return this;
    }

    public IGroupOrder<T> asc(String fieldName) {
        sequencer.Asc(fieldName);
        return this;
    }

    public IGroupOrder<T> clear() {
        sequencer.Clear();
        return this;
    }

    public IGroupOrder<T> asc(T field) {
        Map<String, Object> dic = SqlUtils.fieldMap(field);
        Iterator<String> em = dic.keySet().iterator();
        while (em.hasNext()) {

            sequencer.Asc(em.next());

        }
        return this;

    }

    public IGroupOrder<T> desc(T field) {

        Map<String, Object> dic = SqlUtils.fieldMap(field);
        Iterator<String> em = dic.keySet().iterator();
        while (em.hasNext()) {
            sequencer.Desc(em.next());

        }
        return this;
    }


    @Override
    public IGrouper<T> entities() {
        return entity;
    }
}

class Sequencer2<T> implements IOrder<T> {
    IObjectEntity<T> entity;
    Sequencer sequencer;

    public void FormatSqlText(StringBuilder sb) {
        sequencer.FormatSqlText(sb);
    }

    public Sequencer2(IObjectEntity<T> entity) {
        this.sequencer = new Sequencer();
        this.entity = entity;
    }


    public IOrder<T> desc(String fieldName) {
        sequencer.Desc(fieldName);
        return this;
    }

    public IOrder<T> asc(String fieldName) {
        sequencer.Asc(fieldName);
        return this;
    }

    public IOrder<T> clear() {
        sequencer.Clear();
        return this;
    }

    public IOrder<T> asc(T field) {
        Map<String, Object> dic = SqlUtils.fieldMap(field);
        Iterator<String> em = dic.keySet().iterator();
        while (em.hasNext()) {
            sequencer.Asc(em.next());

        }
        return this;
    }

    public IOrder<T> desc(T field) {
        Map<String, Object> dic = SqlUtils.fieldMap(field);
        Iterator<String> em = dic.keySet().iterator();
        while (em.hasNext()) {
            sequencer.Desc(em.next());

        }
        return this;
    }

    @Override
    public IObjectEntity<T> entities() {
        return entity;
    }


}