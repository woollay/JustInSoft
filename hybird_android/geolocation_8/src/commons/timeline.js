import echarts from "../lib/echarts.min";

let _tlJson = {
    timeline: {
        axisType: 'category',
        lineStyle: {
            show: true
        },
        symbol: "circle",
        itemStyle: {
            normal: {
                color: "rgba(194,53,49, 0.5)"
            }
        },
        controlStyle: {
            showPrevBtn: true,
            showNextBtn: true,
            normal: {
                color: "rgba(194,53,49, 0.5)",
                borderColor: "rgba(194,53,49, 0.5)"
            }
        },
        orient: "vertical",
        inverse: true,
        x: null,
        x2: 0,
        y: 40,
        width: 55,
        height: "80%",
        loop: false,
        autoPlay: false,
        playInterval: 1000
    },
    baseOption: {
        tooltip: {
            'trigger': 'axis'
        },
    }
};

export default class Timeline {
    constructor() {
        this.chart = undefined;
        this.lastIndex = 0;
    }

    static makeTimeline(el, dataJson, callback) {
        let tl = new Timeline();
        let count = dataJson.timeline.data.length;
        let option = echarts.util.merge(_tlJson, dataJson, true);

        option = option || {};
        tl.chart = echarts.init(el, null, {});
        tl.chart.on("timelineChanged", (params) => {
            let lastIndex = tl.lastIndex;
            let curIndex = params.currentIndex;
            console.log("last index:" + lastIndex + ",current index:" + curIndex);
            if (count === 0) {
                console.log("no data.");
                return;
            } else if (lastIndex != curIndex) {
                tl.lastIndex = curIndex;
                console.log("move " + lastIndex + " to " + curIndex);
                callback.callback(tl, params);
            } else if (lastIndex === curIndex) {
                if (lastIndex === 0) {
                    callback.last(tl, params);
                } else {
                    callback.next(tl, params);
                }
            }
        });

        tl.chart.setOption(option);
    }

    update(dataJson) {
        let option = echarts.util.merge(_tlJson, dataJson, true);
        let lastIndex = option.timeline.currentIndex;
        this.chart.setOption(option);
        if (lastIndex != undefined) {
            this.lastIndex = lastIndex;
        } else {
            this.lastIndex = 0;
        }
    }
}