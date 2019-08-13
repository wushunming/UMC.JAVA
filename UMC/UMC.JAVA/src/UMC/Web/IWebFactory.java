package UMC.Web;

public interface IWebFactory {

    void init(WebContext context);

    WebFlow flowHandler(String mode);
}
