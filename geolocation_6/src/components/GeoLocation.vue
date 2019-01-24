<template>
	<div class="geolocation">
		<h1>Current location is:</h1>
		<h2>Longitude:{{location.longitude}}</h2>
		<h2>Latitude:{{location.latitude}}</h2>
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
		height: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
		flex-wrap: wrap;
	}

	.geolocation h1,
	.geolocation h2 {
		width: 100%;
	}
</style>
