package UMC.Web;

import java.util.LinkedList;

public class ListItemCollection extends LinkedList<ListItem> {

    public void add(String text) {
        this.add(new ListItem(text));
    }

    public void add(String text, String value) {
        this.add(new ListItem(text, value));
    }

    public void add(String text, String value, boolean selected) {
        this.add(new ListItem(text, value, selected));
    }

    public ListItemCollection put(String text, String value) {
        this.add(text, value);
        return this;
    }  public ListItemCollection put(String text) {
        this.add(text);
        return this;
    }

    public ListItemCollection put(String text, String value, boolean selected) {
        this.add(text, value, selected);
        return this;
    }

}
