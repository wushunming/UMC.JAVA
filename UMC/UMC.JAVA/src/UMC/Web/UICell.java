package UMC.Web;


public abstract class UICell {

    private static class UICeller extends UICell {
        String _Type;
        Object _data;

        @Override
        public Object data() {
            return _data;
        }

        @Override
        public String type() {
            return _Type;
        }
    }

    public abstract Object data();


    public static UICell create(String type, WebMeta data) {
        UICeller celler = new UICeller();
        celler._data = data;
        celler._Type = type;
        return celler;
    }

    public abstract String type();

    private WebMeta _format = new WebMeta();

    public WebMeta format() {
        return _format;

    }

    public UICell format(String name, String value) {
        _format.put(name, value);
        return this;
    }

    private UIStyle _style = new UIStyle();


    public UIStyle style() {
        return _style;

    }
}