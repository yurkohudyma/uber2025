const map = L.map('map').setView([48.919293, 24.712843], 13);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

let taxiMarker;
let destinationCoords;
let updateInterval;

function getDistance(coord1, coord2) {
    const R = 6371000; // Радіус Землі в метрах
    const toRad = deg => deg * Math.PI / 180;

    const dLat = toRad(coord2.lat - coord1.lat);
    const dLng = toRad(coord2.lng - coord1.lng);
    const lat1 = toRad(coord1.lat);
    const lat2 = toRad(coord2.lat);

    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
              Math.cos(lat1) * Math.cos(lat2) *
              Math.sin(dLng / 2) * Math.sin(dLng / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
}

// Отримання повного стану таксі і побудова карти
fetch('/rides/map?rideId=2')
    .then(res => res.json())
    .then(data => {
        const {
            vehicleCurrentPosition,
            departure,
            destination,
            toPAXroute,
            route
        } = data;

        destinationCoords = {
            lat: departure[0],
            lng: departure[1]
        };

        // Додавання маркерів
        taxiMarker = L.marker(vehicleCurrentPosition, {
            icon: L.icon({
                iconUrl: 'ico/vehicle.png',
                iconSize: [50, 50]
            })
        }).addTo(map).bindPopup("🚕 Таксі");

        L.marker(departure).addTo(map).bindPopup("📍 Клієнт");
        L.marker(destination).addTo(map).bindPopup("🏁 Призначення");

        // Відображення маршрутів
        L.polyline(toPAXroute, { color: 'red', weight: 4 }).addTo(map);
        L.polyline(route, { color: 'blue', weight: 4 }).addTo(map);

        // Центрування карти
        const bounds = L.latLngBounds([...toPAXroute, ...route]);
        map.fitBounds(bounds);

        startTracking();
    });

function startTracking() {
    updateInterval = setInterval(() => {
        fetch('/rides/getPosition?vehicleId=1')
            .then(res => res.json())
            .then(currentPosition => {
                const currentLatLng = {
                    lat: currentPosition[0],
                    lng: currentPosition[1]
                };

                console.log("🚕 Отримано позицію:", currentLatLng);

                if (taxiMarker) {
                    taxiMarker.setLatLng(currentLatLng);

                    const distance = getDistance(currentLatLng, destinationCoords);
                    console.log("📏 Відстань до призначення:", distance);

                    if (distance < 10) {
                        console.log("🚕 Таксі прибуло, зупиняємо оновлення");
                        clearInterval(updateInterval);
                    }
                }
            })
            .catch(err => console.error('🏁❌Помилка отримання позиції або обробки:', err));
    }, 2000);
}
