package UMC.Web;


import UMC.Data.Utility;

import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

public class UIStyle implements UMC.Data.IJSON {


    WebMeta meta = new WebMeta();

    public UIStyle alignLeft() {
        return align(0);

    }

    public UIStyle(WebMeta meta) {
        this.meta = meta;

    }

    public UIStyle() {

    }

    public UIStyle alignCenter() {
        return align(1);

    }

    public UIStyle radius(int radius) {
        meta.put("border-radius", radius);
        return this;
    }

    public UIStyle alignRight() {
        return align(2);

    }

    /// <summary>
/// 文本对齐
/// </summary>
/// <param name="c">0为left,2为center,3为right</param>
/// <returns></returns>
    public UIStyle align(int c) {
        switch (c % 3) {
            default:
                meta.put("text-align", "left");
                break;
            case 1:
                meta.put("text-align", "center");
                break;
            case 2:
                meta.put("text-align", "right");
                break;
        }
        return this;
    }

    public UIStyle name(String key, String value) {
        meta.put(key, value);
        return this;

    }

    public UIStyle name(String key, int value) {

        meta.put(key, value);
        return this;
    }

    public UIStyle bold() {

        meta.put("font-weight", "bold");
        return this;
    }

    public UIStyle height(int height) {

        meta.put("height", height);
        return this;
    }

    public static int[] padding(WebMeta meta) {
        return padding(meta.get("Padding"));
    }

    public static int[] padding(String padding) {
        if (Utility.isEmpty(padding) == false) {
            List<Integer> ids = new LinkedList<>();
            String[] ps = padding.split(" ");
            switch (ps.length) {
                case 1:
                    int t = Utility.parse(ps[0], 0);
                    ids.add(t);
                    ids.add(t);
                    ids.add(t);
                    ids.add(t);
                    break;
                case 2:

                    int t21 = Utility.parse(ps[0], 0);
                    int t22 = Utility.parse(ps[1], 0);
                    ids.add(t21);
                    ids.add(t22);
                    ids.add(t21);
                    ids.add(t22);
                    break;
                case 3:
                    int t31 = Utility.parse(ps[0], 0);
                    int t32 = Utility.parse(ps[1], 0);
                    int t33 = Utility.parse(ps[1], 0);
                    ids.add(t31);
                    ids.add(t32);
                    ids.add(t33);
                    ids.add(0);
                    break;
                default:
                    ids.add(Utility.parse(ps[0], 0));
                    ids.add(Utility.parse(ps[1], 0));
                    ids.add(Utility.parse(ps[2], 0));
                    ids.add(Utility.parse(ps[3], 0));
//                    ids.Add(Data.Utility.IntParse(ps[1], 0));
//                    ids.Add(Data.Utility.IntParse(ps[2], 0));
//                    ids.Add(Data.Utility.IntParse(ps[3], 0));
                    break;
            }
            int[] pads = new int[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                pads[i] = ids.get(i);
            }
            return pads;// ids.toArray(new int[0]);
        }
        return new int[0];
    }

    public UIStyle padding(int... padding) {

        switch (padding.length) {
            case 0:
                break;
            case 1:
                meta.put("padding", String.format("%d %d %d %d", padding[0], padding[0], padding[0], padding[0]));
                break;
            case 2:
            case 3:
                meta.put("padding", String.format("%d %d %d %d", padding[0], padding[1], padding[0], padding[1]));
                break;
            case 4:
                meta.put("padding", String.format("%d %d %d %d", padding[0], padding[1], padding[2], padding[3]));
                break;
        }

        return this;
    }

    public UIStyle font(String c) {
        meta.put("font", c);
        return this;
    }

    public UIStyle name(String key, UIStyle style) {
        meta.put(key, style);
        return this;

    }

    public UIStyle name(String key) {
        UIStyle style = new UIStyle();
        meta.put(key, style);
        return style;

    }

    public UIStyle bgColor() {
        return bgColor(0xef4f4f);
    }

    public UIStyle bgColor(int color) {

        meta.put("background-color", intParseColor(color));

        return this;
    }

    public static String intParseColor(int color) {
        String scode = "000000" + Integer.valueOf(color).toHexString(color);
        if (color < 0x1000) {
//                String
            return "#" + scode.substring(scode.length() - 3);
        } else {
            return "#" + scode.substring(scode.length() - 6);
        }

    }

    public UIStyle copy(UIStyle style) {


        meta.map().putAll(style.meta.map());

        return this;
    }

    public UIStyle color(int color) {

        meta.put("color", intParseColor(color));

        return this;
    }

    public UIStyle borderColor(int color) {

        meta.put("border-color", intParseColor(color));

        return this;
    }


    public UIStyle underLine() {

        meta.put("text-decoration", "underline");
        return this;
    }

    public UIStyle delLine() {

        meta.put("text-decoration", "line-through");
        return this;
    }

    public UIStyle size(int size) {
        meta.put("font-size", size);
        return this;
    }

    public UIStyle click(UIClick click) {
        meta.put("click", click);
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