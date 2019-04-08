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

			let dataJson = {
				timeline: {
					data: [
						"2002-01-01",
						"2003-01-01",
						"2004-01-01",
						"2005-01-01",
						"2006-01-01",
						"2007-01-01",
						"2008-01-01",
						"2009-01-01",
						"2010-01-01",
						"2011-01-01"
					],
					label: {
						formatter: function(s) {
							return new Date(s).getFullYear();
						}
					}
				}
			};

			let curPage = 2;
			let callback = {
				callback: (timeline, param) => {
					let index = param.currentIndex;
					console.log("curPage:" + curPage + ",curIndex:" + index);
				},
				last: (timeline, param) => {
					let index = param.currentIndex;
					if (curPage > 1) {
						curPage -= 1;
						dataJson.timeline.data = [
							"1992-01-01",
							"1993-01-01",
							"1994-01-01",
							"1995-01-01",
							"1996-01-01",
							"1997-01-01",
							"1998-01-01",
							"1999-01-01",
							"2000-01-01",
							"2001-01-01"
						];
						let maxIndex = dataJson.timeline.data.length - 1;
						dataJson.timeline.currentIndex = maxIndex;
						timeline.update(dataJson);
					}
					console.log("-curPage:" + curPage + ",curIndex:" + index);
				},
				next: (timeline, param) => {
					let index = param.currentIndex;
					if (curPage < 3) {
						curPage += 1;
						dataJson.timeline.data = [
							"2012-01-01",
							"2013-01-01",
							"2014-01-01",
							"2015-01-01",
							"2016-01-01",
							"2017-01-01",
							"2018-01-01",
							"2019-01-01",
							"2020-01-01",
							"2021-01-01"
						];
						dataJson.timeline.currentIndex = 0;
						timeline.update(dataJson);
					}
					console.log("+curPage:" + curPage + ",curIndex:" + index);
				}
			};

			Timeline.makeTimeline(node, dataJson, callback);
		},
		methods: {}
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
