package UMC.Data.Sql;

public class Script {
    public String text() {
        return _text;
    }

    private String _text;
    private Object[] _Arguments;

    public Object[] arguments() {
        return _Arguments;
    }


    private Script() {
    }

    void reset(String text, Object[] args) {
        this._Arguments = args;
        this._text = text;
    }

    static Script create(String text, Object[] args) {
        Script sc = new Script();
        sc.reset(text, args);
        return sc;
    }
}
