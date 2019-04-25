export default class Record {
    startRecord(param) {
        try {
            console.log("Recording start now.");
            window.startVoice = () => {
                console.log("start voice now.");
                param.success();
                window.startVoice = undefined;
            };
            jsCallback.startVoice();
        } catch (e) {
            param.error("Error:" + e);
        }
    }

    stopRecord(param) {
        try {
            console.log("Recording stop now.");
            window.stopVoice = (maxDB) => {
                console.log("stop voice now:" + maxDB);
                param.success(maxDB);
                window.stopVoice = undefined;
            };
            jsCallback.stopVoice();
        } catch (e) {
            param.error("Error:" + e);
        }
    }
}