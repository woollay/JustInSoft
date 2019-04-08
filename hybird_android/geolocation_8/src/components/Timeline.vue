<template>
	<div id="timeline">
		<div ref="timeline-table" class="timeline-table"></div>
	</div>
</template>
<script>
	import Timeline from "../commons/timeline";
	export default {
		name: "Timeline",
		data() {
			return {};
		},
		mounted: function() {
			console.log("start to init timeline.");

			let node = this.$refs["timeline-table"];
			console.log("timeline=" + JSON.stringify(node));
			let start = 2000;
			let len = 10;
			let self = this;
			let dataJson = {
				timeline: {
					data: self.getDataJson(start, len, 0),
					label: {
						formatter: function(s) {
							return new Date(s).getFullYear();
						}
					}
				}
			};
			let limitPage = [1, 20];
			let curPage = 10;
			let callback = {
				callback: (timeline, param) => {
					let index = param.currentIndex;
					console.log("curPage:" + curPage + ",curIndex:" + index);
				},
				last: (timeline, param) => {
					let index = param.currentIndex;
					if (curPage > limitPage[0]) {
						curPage -= 1;
						dataJson.timeline.data = self.getDataJson(start, len, -1);
						let maxIndex = dataJson.timeline.data.length - 1;
						dataJson.timeline.currentIndex = maxIndex;
						timeline.update(dataJson);

						start -= len;
					}
					console.log("-curPage:" + curPage + ",curIndex:" + index);
				},
				next: (timeline, param) => {
					let index = param.currentIndex;
					if (curPage < limitPage[1]) {
						curPage += 1;
						dataJson.timeline.data = self.getDataJson(start, len, 1);
						dataJson.timeline.currentIndex = 0;
						timeline.update(dataJson);

						start += len;
					}
					console.log("+curPage:" + curPage + ",curIndex:" + index);
				}
			};

			Timeline.makeTimeline(node, dataJson, callback);
		},
		methods: {
			getDataJson: function(start, len, delta) {
				let data = [];
				for (let i = 0; i < len; i++) {
					data[i] = start + i + delta * len + "-01-01";
				}
				console.log("current page:" + data);
				return data;
			}
		}
	};
</script>
<style scoped>
	.timeline-table {
		text-align: center;
		margin: 20px;
		width: 70px;
		height: 400px;
		background: #fff;
	}
</style>
