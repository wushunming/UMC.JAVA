# 什么是UMC

| 编写人员 |  吴顺明  |  编写时间 |2019年6月14日 |
|:--------:| ----- |:----: |---- |
| 审核     |   |   审核时间    |   |
| 文档版本        |  v 0.1   |       |


[login2]: http://oss.365lu.cn/UserResources/11wpkim/1560587017765/login2.jpg
![login2]

UMC的全名是 `UI Model Command`，意思是让UI响应服务羰的模型指令,实现上就是设计了一套客户端与服务端交互的标准协议，此标准能让就是客户端用标准请求服务端、服务端用标准JSON格式响应客户端。达到操控和绘制客户端模型，现在服务端我们已经用java和.net实现了此标准协议（如何大家有需要，也可以是其他计算机语言实现哦），现在.java后端工程师和.net后端工程师只要用此架构开发，就能快速实现了原生Android和原生IOS端和H5端和小程序（嵌套H5，后期会用原生小程序实现)，客户端都已经开源，此协议用一套标准，统一实现PC和H5端，小程序端，安卓和苹果端的界面实现，和后台逻辑。

这也意为做，一个统一的后台就可以管理各个终端，用此实现的业务不但开发维护简单，业务维护也更简单，对公司而言，一套后端工程师就有各终端的团队班子，对于后端工程师更是赋能，对于Android和IOS工程师，此模式下的设计和开发，让终端开发更简单，太太减少APP版本发布次数，对业务管理来说不需要多套系统维护，我也相信每个后台工程师和Android和IOS工程师都愿意掌握此技能。

UMC是在数据通信上是采用`Http GET`方式请求，为什么采用GET方式呢，客户端与服务端做为非文件交互，整体来说交互所要的数据是相当少，`GET`方式足可满足我们应用需要，如果需要文件交互，则采用先把资源上传到文件服务器，再来由UMC处理，这样就简单高效了，还不用占用带宽，又起服务器分流等做用，此设计为我分布式和API高性能网关提供了前期基础。

再来说交互路由吧，我们用`GET`用`QueryString`来交互传参，我们保留`jsonp`参数做跨域使用，还有`_model`和`_cmd`就是我们的`Model`与`Command`，再除去`_`开头的参数就是这次有效请求的参数，就这样根据`Model`和`Command`带着参数去路由，跑一遍，把再结果返回给客户端，就这样完成这次请求了，他的原理就是这样回事。
吹了这么多文字，还不上干货，直接说，怎么干才能出来，好吧，我最听话了，下面用java讲解如何实现。

## UMC的IWebFactory、WebFlow、WebActivity

请求路由路线是由model和cmd来确认，他对应的后台路由类分别为 `IWebFactory`接口，WebFlow虚类`WebActivity`虚类
由`IWebFactory`来确认接收那此`model`，接收就返回`WebFlow`类，再就`WebFlow`类路径cmd确认那个`WebActivity`处理，整个处理路由就是这样，下面我们就来讲解此逻辑
### IWebFactory

``` java
//IWebFactory
public class FlowFactory implements IWebFactory {
    @Override
    public void init(WebContext context) {

    }

    @Override
    public WebFlow flowHandler(String mode) {
        switch (mode) {
            case "Account":
                return new AccountFlow();
        }
        return WebFlow.Empty;
    }
}
``` 

### WebFlow
上例代码展示，此IWebFactory只接收 `Account` 的Model，此模块的cmd路由由AccountFlow处理，下面我们就查看AccountFlow代码
``` java

public class AccountFlow extends WebFlow {
    @Override
    public WebActivity firstActivity() {
        switch (this.context().request().cmd()) {
            case "Login":
                return new AccountLoginActivity();
            case "Register":
                return new AccountRegisterActivity();
            case "Forget":
                return new AccountForgetActivity();
            case "Password":
                return new AccountPasswordActivity();
        }

        return WebActivity.Empty;
    }
}


```

从上列代码可以就看到`AccountFlow`，可以处理`Login`，`Register`，`Forget`，`Password`指令,每个指令都有自己的`WebActivity`,
`WebActivity`就是我们的业务实现了，原则不同意我们直接到`AccountFlow`，处理业务业务逻辑全部`WebActivity`，下面我就用`AccountLoginActivity`来说明`WebActivity`的处理方式

### WebActivity

``` java
public class AccountLoginActivity extends WebActivity {
    @Override
    public void processActivity(WebRequest request, WebResponse response) {

        WebMeta user = this.asyncDialog(d ->
        {

            UIFormDialog dialog = new UIFormDialog();
            dialog.title("账户登录");
            dialog.addText("用户名", "Username", "").put("placeholder", "手机/邮箱");
            dialog.addPassword("用户密码", "Password", "");
            dialog.submit("登录", request, "User");//事件对话框
            dialog.addUIIcon('\uf1c6', "忘记密码").put("Model", request.model()).put("Command", "Forget");
            dialog.addUIIcon('\uf234', "注册新用户").put("Model", request.model()).put("Command", "Register");
            return dialog;

        }, "Login");
        String username = user.get("Username");

        Membership userManager = WebADNuke.Security.Membership.Instance();
        int times = userManager.Password(username, passwork, maxTimes);
        switch (times) {
            case 0:
                Identity iden = userManager.Identity(username);

                AccessToken.login(iden, AccessToken.token(), request.isApp() ? "App" : "Client", true);

                this.context().send("User", true);

                break;
            case -2:
                this.prompt("您的用户已经锁定，请过后登录");
                break;
            case -1:
                this.prompt("您的用户不存在，请确定用户名");

                break;
            default:
                this.prompt(String.format("您的用户和密码不正确，您还有%d次机会", maxTimes - times));

                break;
        }
    }
}
```

我这里先说说processActivity方法的两个参数吧，`request`,`response` 其中`request`是请求的所有参数信息都这里，包含客户端环境，是不是APP中，是不是微信中，客户的IP是多少，和QueryString参数，但`request`把QueryString规整化了，当请求中有model和cmd的时间，QueryString单值可以request.sendValue()获取，多值可以用 request.sendValues()得到；当请求无model和cmd的时，则此次请求是把QueryString规整化到了对话框中，以对话框的方式获取交互的值；`response`客户端响应对象，他可以完成数据输出，跳转，跳转到其他模块指令中，都可以参数。一句话就是`request`是获取客户端信息，`response` 是操作响应内容的；

从上面讲解中，我们明白，`QueryString`会规整到`request`请求参数中去，这里讲一讲请求参数与箭头函数的之间的关系。箭头函数在什么样的情况下执行呢，只有在请求参数或会话中找到这对话框的参数值，则执行箭头函数获得一个对话框给并返回到客户端，让终端输入；先声明一个对话框，对话框分为单值对话框和表单对话框，先说单值对话框与请求参数对应的关系吧，每个请求的对话框都有一个asyncId，也就是asyncDialog传入进去asyncId，确认asyncId是不是有值，先从会话参数中找，如果没有，从对话框参数池中找，不存在对话框参数池，检测`request.sendValue()`有没有值，没有值，再用`asyncId`检测`request.sendValues()`是不是有值，这此都没值的情况下，再去执行箭头函数获取对话框。这就是单值对话框获取值的逻辑，下面我们再说说表单对话框获取值的方式，第一步也是一样从会话参数中找，没有找到，再看对话框参数池是不是表单字典，如果不是或没有，再看`request.sendValue()`是不是能与此次表单asyncId对应，如果不是或没有，再去执行箭头函数获取表单对话框。这就是表单对话框获取值的逻辑，

在对话框中从获取值的方式上，可分为两种，一个会话对话框和事件对话框，会话对话框，提交之后就关闭，事件对话框是根据服务端返回的事件，进行关闭或者刷新的。在我们的`UIGridDialog`和`UIFormDialog`都支持这两种模式


从上面逻辑讲解，整个对话框都是为了让对话框的`asyncId`配对`QueryString`，这说明什么呢，这说明我们做准备好`WebActivity`所要的的参数，就能执行完`processActivity`逻辑，中途不会返回到客户端。

从上列代码中，一个表单对话框，这里我先说一说对话框与`QueryString`的关系吧，先声明一个对话框，对话框分为单值对话框和表单对话框，单值
现在我们讲解一下上面的`AccountLoginActivity`的`processActivity`，此`processActivity`有登录表单对话框，此对话框并启用了事件对话框，看`dialog.submit("登录", request, "User")`这块表示启用了事件对话框模式，

说明这么多，实事上是希望大家理解此模式，这也就UMC架构的核心思想，其他的解译都是丰富这模式下的应用场景。

回头我们再看看`AccountLoginActivity` 的`processActivity`方法，可从代码中可以看出，此Activity有绘出一个用户登录的`UIFormDialog`，还带了忘记密码和用户注册连接，这就是我们登录功能的全部代码，是不是简单，是不是掌握他是不是更有价值呢。



实事上UMC的java实现是从HttpServlet直接开始，所有的java web的源头都是这里，所以说他是一套高性能的精简Web架构，整个包才200K+,还包含了一套数据库访问机制和身份认证机制，与Spring MVC相比结构更精简，效率更高，他除去了MVC的标注路由方法和参数合成重写，所以比Spring MVC更快捷。


