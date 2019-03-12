<template>
	<div class="geolocation">
		<div class="location-title">Current location is:</div>
		<div class="location-content">Longitude:{{location.longitude}}</div>
		<div class="location-content">Latitude:{{location.latitude}}</div>
	</div>
</template>

<script>
	import LocationSdk from "../commons/location-sdk";
	export default {
		name: "GeoLocation",
		data() {
			return {
				location: {
					longitude: "-1",
					latitude: "-1"
				}
			};
		},
		mounted: function() {
			console.log("start to get location now.");
			let self = this;
			LocationSdk.getLocation({
				success: res => {
					console.log("current location is:" + res);
					self.location = res;
				},
				error: res => {
					console.log("failed to get location:" + res);
					self.location = {
						longitude: "-1",
						latitude: "-1"
					};
				}
			});
		}
	};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
	.geolocation {
		width: 100%;
		height: 80px;
		display: flex;
		justify-content: center;
		align-items: center;
		flex-wrap: wrap;
	}

	.location-title {
		width: 100%;
		height: 30px;
		font-weight: bold;
	}

	.location-content {
		width: 100%;
		height: 25px;
		font-weight: bold;
	}
</style>
