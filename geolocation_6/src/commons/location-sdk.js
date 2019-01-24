export default class Location {
    static getLocation(callback) {
        if (navigator.geolocation) {
            let options = {
                enableHighAccuracy: true,
                maximumAge: 1000
            };

            navigator.geolocation.getCurrentPosition(
                (res) => {
                    console.log("get location successful:" + res);
                    let location = {};
                    location.longitude = res.coords.longitude;
                    location.latitude = res.coords.latitude;
                    callback.success(location);
                },
                (res) => {
                    console.log("get location failed:" + res);
                    callback.error(res);
                },
                options
            );
        } else {
            callback.error("Geo location not supported.");
        }
    }
}