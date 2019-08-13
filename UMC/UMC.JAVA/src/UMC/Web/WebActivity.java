package UMC.Web;

public abstract class WebActivity extends WebHandler {
    /// <summary>
    /// 终止流程的活动
    /// </summary>
    public static WebActivity Empty = new WebActivity() {
        @Override
        public void processActivity(WebRequest request, WebResponse response) {

        }
    };


    /// <summary>
    /// 活动Id
    /// </summary>
    public String Id() {

        return this.getClass().getName();

    }


    /// <summary>
    /// 活动节点处理方法
    /// </summary>
    /// <param name="request">请求</param>
    /// <param name="response">响应</param>
    /// <returns></returns>
    public abstract void processActivity(WebRequest request, WebResponse response);
}
