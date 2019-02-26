import axios from 'axios';
import wx from 'weixin-js-sdk';

const IMAGE_HEADER = "data:image/jpeg/png;base64,";
const IMAGE_HEADER_SPLIT = ",";
export default class WeixinTester {
    constructor() {
        //
        axios.post("https://www.xxxx.xxxx/sign", document.URL)
            .then((response) => {
                console.log("initial weixin successful.", response);
                let jsApiList = [
                    'getLocation',
                    'chooseImage',
                    'previewImage',
                    'startRecord',
                    'stopRecord',
                    'playVoice',
                    'stopVoice',
                    'uploadVoice',
                    'onVoiceRecordEnd',
                    'downloadVoice',
                    'getLocalImgData',
                    'onMenuShareTimeline',
                    'onMenuShareAppMessage'
                ];

                console.log("start to init wx.");
                wx.ready(() => {
                    console.log("ws sdk ready.");
                    let locationParam = {
                        success: (location) => {
                            alert("current location:" + location);
                        },
                        error: (res) => {
                            alert("init wx error:" + res);
                        }
                    };

                    let checkParam = {
                        jsApiList: jsApiList,
                        success: (res) => {
                            _getLocation(locationParam);
                        },
                        error: (res) => {
                            alert("init wx error:" + res);
                        }
                    };
                    wx.checkJsApi(checkParam);
                });
                wx.error((res) => {
                    console.log("ws sdk error.", res);
                });
                wx.config({
                    debug: false,
                    appId: response.data.appId,
                    timestamp: response.data.timestamp,
                    nonceStr: response.data.nonceStr,
                    signature: response.data.signature,
                    jsApiList: jsApiList
                });
            })
            .catch((error) => {
                console.log("sign error:", error);
            });
    }

    takePicture(param) {
        wx.chooseImage({
            count: 1,
            sizeType: ['compressed'],
            sourceType: ['camera'],
            success: (res) => {
                console.log("wx photo localIds:", res.localIds);
                alert("successfully to take a picture.");
                let imgParam = {
                    localId: res.localIds[0],
                    success: (res) => {
                        alert("successfully to get picture.");
                        let base64 = IMAGE_HEADER;
                        if (res.localData.indexOf(IMAGE_HEADER_SPLIT) > -1) {
                            base64 = "";
                        }
                        let localData = base64 + res.localData;
                        param.success(localData);
                    },
                    error: (error) => {
                        param.error("failed to get photo:", error);
                    }
                };
                wx.getLocalImgData(imgParam);
            },
            error: (error) => {
                param.error("failed to get wx photo:", error);
            }
        });
    }
}


let _getLocation = (param) => {
    wx.getLocation({
        type: 'wgs84',
        success: (res) => {
            console.log("get wx location:", JSON.stringify(res));
            let location = {};
            if (typeof (res.longitude) === "string") {
                location.latitude = parseFloat(res.latitude);
                location.longitude = parseFloat(res.longitude);
            } else {
                location.latitude = res.latitude;
                location.longitude = res.longitude;
            }
            param.success(location);
        },
        error: (error) => {
            param.error("failed to get wx location:", error);
        }
    });
}