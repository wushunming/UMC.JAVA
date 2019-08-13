package UMC.Web;

import UMC.Data.IJSON;
import UMC.Data.Utility;

import java.io.IOException;
import java.io.Writer;

public class ListItem implements IJSON {
    public ListItem() {
    }

    public ListItem(String text)

    {
        this(text, text, false);
    }

    public ListItem(String text, String value)

    {
        this(text, value, false);
    }


    public ListItem(String text, String value, boolean selected) {
        this.Selected = selected;
        this.Text = text;
        this.Value = value;
    }

    /// <summary>
    /// 是否禁用
    /// </summary>
    public boolean Disabled;
    /// <summary>
    /// 是否选中
    /// </summary>
    public boolean Selected;
    /// <summary>
    /// 文本
    /// </summary>
    public String Text;
    /// <summary>
    /// 值
    /// </summary>
    public String Value;

    public String Title;

    /// <summary>
    /// 值
    /// </summary>

    @Override
    public void write(Writer writer) {
        try {
            writer.write("{");


            writer.write("\"Text\":");
            UMC.Data.JSON.serialize(this.Text, writer);
            writer.write(",\"Value\":");
            UMC.Data.JSON.serialize(this.Value, writer);
            if (Utility.isEmpty(Title) == false) {
                writer.write(",\"Title\":");
                UMC.Data.JSON.serialize(this.Title, writer);
            }
            if (this.Disabled) {
                writer.write(",\"Disabled\":");
                UMC.Data.JSON.serialize(this.Disabled, writer);
            }
            if (this.Selected) {
                writer.write(",\"Selected\":");
                UMC.Data.JSON.serialize(this.Selected, writer);
            }
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void read(String key, Object value) {
        switch (key) {
            case "Title":
                this.Title = String.valueOf(value);
                break;
            case "Text":
                this.Text = String.valueOf(value);
                break;
            case "Value":
                this.Value = String.valueOf(value);
                break;

            case "Disabled":
                this.Disabled = "true".equals(value);
                break;
            case "Selected":
                this.Selected = "true".equals(value);
                ;//String.Equals("true", value as String);
                break;


        }

    }
}
