# [Vue.js 实战——彻底解决跨域问题](https://blog.csdn.net/dobuy/article/details/100852664)

## 一. 背景

在 Web 应用中，跨域是程序猿绕不过去的坎。

> 什么是跨域：
> 当一个请求 url 的协议、域名、端口三者之间任意一个与当前页面 url 不同即为跨域。

举例：在自己的应用 html 中，需要访问微信或者新浪接口，这时浏览器就会提示跨域了。如下代码所示：在业务代码中访问网络地址来查询天气信息。

```javascript
let url = 'http://t.weather.sojson.com/api/weather/city/101030100'
axios
  .get(url)
  .then(resp => {
    console.log('result:' + resp.data)
    this.content = resp.data
    alert('request success!')
  })
  .catch(e => {
    console.log('exception:' + e)
    alert('request failed!')
  })
```

运行结果如下：
![avatar](https://img-blog.csdnimg.cn/20190915133720298.png)

> 为什么会跨域:
> 出于浏览器的同源策略限制。同源策略（SameOriginPolicy）是一种约定，它是浏览器最核心也最基本的安全功能，如果缺少了同源策略，则浏览器的正常功能可能都会受到影响。可以说 Web 是构建在同源策略基础之上的，浏览器只是针对同源策略的一种实现。同源策略会阻止一个域的 javascript 脚本和另外一个域的内容进行交互。所谓同源（即指在同一个域）就是两个页面具有相同的协议（protocol），主机（host）和端口号（port）。

## 二. 目标

1. 在 Vue 开发过程中(dev 模式)，能够正常跨域访问；
2. 在生产环境中，能够正常跨域访问，同时支持所有 vue 访问的后台 url 自定义；

## 三. 步骤

1. 先确认 vue 版本，目前使用的是 2.5.11(参见 package.json)；
2. 先保证在通过 node.js（作为 web 容器或者说 web 服务器），在本地调测时不跨域。通过在 webpack.config.js 中配置 proxy 代码即可。
   在 webpack.config.js 中代码如下：

```javascript
let url = '/api/weather/city/101030100'
axios
  .get(url)
  .then(resp => {
    console.log('result:' + resp.data)
    this.content = resp.data
    alert('request success!')
  })
  .catch(e => {
    console.log('exception:' + e)
    alert('request failed!')
  })
```

在 webpack.config.js 中代码如下：

```javascript
  devServer: { //开发模式下使用的配置参数
    proxy: {
      '/api': {
        target: 'http://t.weather.sojson.com', // 接口域名
        changeOrigin: true, //是否跨域
        pathRewrite: {
          '^/api': '/api' //需要rewrite的,
        }
      }
    }
  }
```

运行结果如下：
![avatar](https://img-blog.csdnimg.cn/20190915134019217.png)

3. 为了统一管理 url，保证前端可访问任意位置后端和第三方（后端可能部署在本地，也有可能部署在远端），则需要把所有访问的 IP 和端口配置成全局共享，且不会被 webpack 打包压缩。
   业务场景举例如下：
   当用户在本地调试时，使用的默认 IP+端口为：127.0.0.1:8080（127.0.0.1 和 localhost 等价）；当访问的后端从本地改成内网其他地址（例如 IP 和端口为：192.168.1.100:8080）上时，则需要修改所有后端访问地址。
4. 在 src 目录下新增文件夹 config。在 config 目录下新增 url.js。url.js 代码如下：

```javascript
const urlConfig = {
  baseUrl: '',
  apiUrl: ''
}
```

5. 修改 webpack.config.js 中的配置。在 CopyWebpackPlugin 中新增 from/to 配置：

```javascript
{
  from: __dirname + "/src/config", //源目录
  to: "./config", //目标目录
};
```

6. 修改启动 html。在 html 中注入 vue 代码的 div 前面加入如下代码。这样可以保证在 vue 初始化过程中，url.js 已经加载。

```html
<script type="text/javascript" src="config/url.js"></script>
```

注意：上述 src 中的路径是以 webpack.config.js 中配置的相对路径关系而定。

7. 在 vue 的启动 js 中，加入如下代码：

```javascript
Vue.prototype.url = urlConfig
```

8. 在业务代码中，添加对应的 url 前缀：

```javascript
let url = this.url.apiUrl + '/api/weather/city/101030100'
```

这个第三方 url 为随意举例，只是告诉大家可以这样剥离所有的 url。实际上步骤 3 中的业务场景举例才是剥离 url 的主要原因。也就是说 baseUrl 抽出来的意义更大。baseUrl 的使用方法和上述 apiUrl 的使用方法相同。

9. 解决在生产环境中的跨域问题。在生产环境中，devServer 参数并不起作用。所以要通过其他的方式来实现代理。最简单的方式就是使用 nginx 作为反向代理服务器，来统一代理直接访问后端的 url 和访问第三方 url。例如：访问后台的 url 为 nginx 服务器的 80 端口，访问第三方的 url 也为 nginx 服务器 的 80 端口，这样就不存在跨域问题了。而 nginx 则只需要解决根据不同的 url 做不同的跳转即可。
10. 安装 nginx。macOS 执行命令如下。

```shell
brew install nginx
```

11. 编辑 nginx.conf 文件(例如：我本机的位置为：/usr/local/etc/nginx/nginx.conf，可考虑用 find 命令搜下)，在 Server 内新增如下内容：

```shell
        listen       80;
        server_name  127.0.0.1;

        charset utf-8;

        location / {
            proxy_pass http://127.0.0.1:8080;
        }
        location /api {
            proxy_pass http://t.weather.sojson.com/api;
        }
```

保存完毕后，执行 nginx 启动命令：

```shell
nginx -c /usr/local/etc/nginx/nginx.conf
```

12. 保持访问第三方 url 的代码如下：

```javascript
let url = '/api/weather/city/101030100'
axios
  .get(url)
  .then(resp => {
    console.log('result:' + resp.data)
    this.content = resp.data
    alert('request success!')
  })
  .catch(e => {
    console.log('exception:' + e)
    alert('request failed!')
  })
```

13. 删除 webpack.config.js 中的 proxy 配置。
14. 通过地址栏访问 nginx 的监听地址:http://localhost（http默认端口是80，可以省略），效果如下：
    ![avatar](https://img-blog.csdnimg.cn/20190915134113659.png)
15. 完整代码路径：[github 链接](https://github.com/woollay/JustInSoft/tree/master/proxy)

## 四. 总结

1. 任何的业务问题，都不仅仅是单纯的某一个知识点；
2. 本文写了 webpack 怎么配置代理；怎么把 url 独立可配置，且不受打包压缩影响；怎么使用 nginx 反向代理解决跨域；
3. 为了尽量简化业务代码，有些涉及后端的业务代码没写，阅读起来可能有些费解；
4. 这是第一次使用 markdown 来写博客，有什么好的建议请留言。

## 五. 参考

[1][什么是跨域？跨域解决方法](https://blog.csdn.net/qq_38128179/article/details/84956552)
