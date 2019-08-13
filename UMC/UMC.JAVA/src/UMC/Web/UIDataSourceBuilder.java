package UMC.Web;


public class UIDataSourceBuilder {

    WebMeta _data = new UMC.Web.WebMeta();

    public UIDataSourceBuilder menu(UIClick... clicks) {
        _data.put("menu", clicks);
        return this;
    }

    private String Title;

    public UIDataSourceBuilder title(String title) {
        this.Title = title;
        return this;
    }

    public UIDataSourceBuilder refreshEvent(String... eventName) {

        _data.put("RefreshEvent", String.join(",", eventName));
        return this;
    }

    public UIDataSourceBuilder closeEvent(String... eventName) {

        _data.put("CloseEvent", String.join(",", eventName));
        return this;
    }

    public UIDataSourceBuilder header(UIHeader header) {
        _data.put("Header", header);
        return this;
    }

    public UIDataSourceBuilder footer(UIHeader footer) {
        _data.put("Footer", footer);
        return this;
    }

    public WebMeta builder(UIDataSource... dataSources) {
        return new UMC.Web.WebMeta(_data.map()).put("type", "DataSource").put("DataSource", dataSources).put("title", this.Title);//.Put("model");
    }

    public void builder(WebContext context, UIDataSource... dataSources) {
        context.send(new UMC.Web.WebMeta(_data.map()).put("type", "DataSource")
                .put("DataSource", dataSources).put("title", this.Title), true);

    }


    public void binderCells(WebContext context, UIDataSource... dataSources) {

        context.send(new UMC.Web.WebMeta(_data.map())
                .put("type", "DataSource").put("DataSource", dataSources).put("model", "Cells").put("title", this.Title), true);
    }
}