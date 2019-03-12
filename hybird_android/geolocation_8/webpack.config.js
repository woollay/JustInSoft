const path = require("path"); //node路径处理模块
const webpack = require('webpack'); //webpack必要条件   
const HtmlWebpackPlugin = require('html-webpack-plugin'); //多页面生成视图插件
const CleanWebpackPlugin = require('clean-webpack-plugin'); //清除工程目录插件
const CompressionPlugin = require('compression-webpack-plugin'); //压缩插件

module.exports = {
    //对象形式entry
    entry: {
        index: './src/js/index.js'
    },
    output: {
        path: path.resolve(__dirname, './dist'), //打包之后工程根目录
        publicPath: '/static/', //html资源对应的server目录
        filename: '[name].[hash].js', //每个页面对应的js文件
    },
    module: {
        rules: [{
                test: /\.css$/,
                use: ["vue-style-loader", "css-loader"]
            },
            {
                test: /\.vue$/, //.vue文件处理
                loader: 'vue-loader',
                options: {
                    loaders: {}
                }
            },
            {
                test: /\.js$/, //es6转es5处理
                loader: 'babel-loader',
                exclude: /node_modules/
            },
            {
                test: /\.(png|jpg|gif|svg)$/,
                loader: "file-loader",
                options: {
                    name: "[name].[ext]"
                }
            },
            {
                test: /\.html$/, //html打包，可有可无
                loader: 'html-loader',
                options: {}
            }
        ]
    },
    resolve: {
        alias: {
            vue$: "vue/dist/vue.esm.js"
        },
        extensions: ["*", ".js", ".vue", ".json"]
    },
    plugins: [
        new CleanWebpackPlugin(["dist"]), //构建dist之前清除老版目录
        new HtmlWebpackPlugin({ //一个html文件，有多个复写多个HtmlWebpackPlugin
            filename: "index.html",
            template: "./src/pages/index.html",
            inject: true,
            chunks: ["index"]
        })
    ],
    devServer: { //开发模式下使用的配置参数
        historyApiFallback: true,
        noInfo: true,
        overlay: true,
        contentBase: path.resolve(__dirname, "./src/assets") //本地调试时，图片等资源对应的根路径
    },
    devtool: "#eval-source-map"
};

if (process.env.NODE_ENV === "production") { //生产环境，启用兼容和js压缩处理
    module.exports.devtool = "#source-map";
    // http://vue-loader.vuejs.org/en/workflow/production.html
    module.exports.plugins = (module.exports.plugins || []).concat([
        new webpack.DefinePlugin({
            "process.env": {
                NODE_ENV: '"production"'
            }
        }),
        new webpack.optimize.UglifyJsPlugin({
            sourceMap: true,
            compress: {
                warnings: false
            }
        }),
        new webpack.LoaderOptionsPlugin({
            minimize: true
        }),
        new CompressionPlugin({
            filename: "[path].gz[query]",
            algorithm: "gzip",
            test: /\.js$/,
            threshold: 1024,
            minRatio: 0.8,
        })
    ]);
};

if (process.env.PLATFORM == "APP") {
    module.exports.output = {
        path: path.resolve(__dirname, "./AppDist"),
        publicPath: "",
        filename: "[name].js"
    };

    module.exports.plugins.unshift(new CleanWebpackPlugin(["AppDist"]));
}

//DEV模式时，指定公共路径为当前路径
if (process.env.PLATFORM == "DEV") {
    module.exports.output.publicPath = "";
}