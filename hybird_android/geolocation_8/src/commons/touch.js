export default class Touch {
    constructor(node, continueTime) {
        this.node = node;
        this.continueTime = continueTime * 1000;
        this.touchPoint = {
            x: 0,
            y: 0
        };
        this.startTime = undefined;
        this.endTime = undefined;
        this.timerTag = undefined;
        this.progress = undefined;
    }

    registerTouchStart(param) {
        let self = this;
        self.node.addEventListener("touchstart", (e) => {
            console.log("start touch");
            clearInterval(self.timerTag);
            if (e.preventDefault) {
                e.preventDefault();
            }
            self.touchPoint = {
                x: e.changedTouches[0].pageX,
                y: e.changedTouches[0].pageY
            };
            console.log("start touch point:" + JSON.stringify(self.touchPoint));
            self.startTime = new Date().getTime();
            self.timerTag = setInterval(() => {
                self.endTime = new Date().getTime();
                let continueTime = (self.endTime - self.startTime) % self.continueTime;
                // console.log("continue time:" + continueTime);
                self.progress = (continueTime / self.continueTime).toFixed(2);
                if (self.progress == 0) {
                    self.progress = 1.00;
                }
                // console.log("current progress:" + self.progress);
                param.success(self.progress);
            }, 500);
            console.log("current timer:" + self.timerTag);
        });
    }

    registerTouchEnd(param) {
        let self = this;
        self.node.addEventListener("touchend", (e) => {
            console.log("end touch");
            clearInterval(self.timerTag);
            console.log("clear current timer:" + self.timerTag);
            if (e.preventDefault) {
                e.preventDefault();
            }

            self.endTime = new Date().getTime();
            let deltaX = e.changedTouches[0].pageX - self.touchPoint.x;
            let deltaY = e.changedTouches[0].pageY - self.touchPoint.y;
            console.log("start touch point:" + JSON.stringify(e.changedTouches[0]));

            if (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 100) {
                console.log("no long touch action.");
                return;
            }
            console.log("final progress:" + self.progress);
            param.success(self.progress == 1);
        });
    }
}