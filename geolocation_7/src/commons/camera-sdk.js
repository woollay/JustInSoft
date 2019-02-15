export default class Camera {
    static takePictures(param) {
        let cameraId = "camera";
        let camera = document.getElementById(cameraId);
        if (!camera) {
            camera = document.createElement("input");
            camera.setAttribute("id", cameraId);
            camera.setAttribute("type", "file");
            camera.setAttribute("accept", "image/*");
            camera.setAttribute("capture", "camera");
            camera.style.display = "none";

            //给拍照对象添加内容改变的监听事件，注意后面不能使用ES6的箭头函数
            camera.addEventListener("change", function () {
                let file = this.files[0];
                //校验拍照文件是否存在
                if (!file || !file.type || !/image\/\w+/.test(file.type)) {
                    console.log("No picture error!");
                    param.error("No picture error!");
                } else {
                    //读取拍照文件的base64字符串（该base64和图片者等价）
                    let reader = new FileReader();
                    reader.readAsDataURL(file);
                    reader.onload = function (e) {
                        //this.result为当前拍照照片的base64字符串
                        console.log("current base64 picture is:" + this.result);
                        let index = this.result.indexOf(',');
                        let img = this.result;
                        if (index >= 0 && index < img.length) {
                            img = img.substring(index + 1);
                            img = "data:image/jpeg/png;base64," + img;
                        }
                        //获取图片成功后，回调外部方法返回对应的图片信息
                        param.success(img);
                    }
                }
            });
            document.body.appendChild(camera);
        }
        camera.click();
    }
}