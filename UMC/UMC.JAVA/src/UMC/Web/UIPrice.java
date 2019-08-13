package UMC.Web;


import java.io.Writer;

public class UIPrice implements UMC.Data.IJSON {
    public UIPrice(Object id, String src) {

        meta.put("id", id);
        meta.put("src", src);
    }

    WebMeta meta = new WebMeta();

    public UIPrice price(Float price) {
        meta.put("price", price);
        return this;

    }

    public UIPrice click(UIClick click) {
        meta.put("click", click);
        return this;
    }

    public UIPrice origin(Float price) {
        meta.put("origin", price);
        return this;

    }

    public UIPrice name(String name) {
        meta.put("name", name);
        return this;

    }
         
    @Override
    public void write(Writer writer) {
        UMC.Data.JSON.serialize(this.meta, writer);

    }

    @Override
    public void read(String key, Object value) {

    }
}