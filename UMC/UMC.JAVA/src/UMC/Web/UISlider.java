package UMC.Web;


import java.io.Writer;

public class UISlider implements UMC.Data.IJSON {
    public static UICell create(UISlider... sliders) {
        return UICell.create("Slider", new WebMeta().put("data", sliders));
    }

    WebMeta meta = new WebMeta();

    public String src() {
        return meta.get("src");
    }

    public UISlider src(String src) {
        meta.put("src", src);
        return this;
    }

    public UIClick click() {
        return (UIClick) meta.map().get("click");
    }

    public UISlider click(UIClick src) {
        meta.put("click", src);
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