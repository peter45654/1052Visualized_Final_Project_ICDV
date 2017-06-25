/**
 * Created by p5030 on 2017/6/18.
 */
function initMap() {
    var gm = {}; //儲存google map的所有物品
    var map = new google.maps.Map(document.getElementById("map"), {
        zoom: 8,
        center: {
            lat: 23.58,
            lng: 120.58
        }
    }); //新增一個google map
    gm.map = map;
    ArrayList = {};
    d3.csv("CompanyInfo-summary.csv", function(error, data) {
        var overlay = new google.maps.OverlayView();
        for (var i = 0; i < data.length; i++) {
            var county = data[i]["county"];
            var count = data[i]["count()"];
            ArrayList[county] = count
        }
        if (error) throw error;



    });
    d3.csv("")
    var color = d3.scale.linear().domain([0, 200]).range(["#ffffff", "#FE0000"]);
    //console.log(color)
    $.getJSON("topojson/counties.json", function(data) {
        //console.log(data);
        geoJsonObject = topojson.feature(data, data.objects.map)
        map.data.addGeoJson(geoJsonObject);
        map.data.setStyle({
            fillColor: '#000000'
        });
        map.data.setStyle(function(feature) {
            var ascii = feature["f"]["name"];

            var colors="#0C0A0A";
            if (typeof color(ArrayList[ascii]) != 'undefined') {
                //console.log(typeof ArrayList[ascii])
                colors = color(ArrayList[ascii]);
            }
            return {
                fillColor: colors,
                strokeWeight: 1,
                visible: true
            };
        });
    });


}