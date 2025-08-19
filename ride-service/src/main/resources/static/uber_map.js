const map = L.map('map').setView([48.919293, 24.712843], 13);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

let taxiMarker;

// Отримання повного стану таксі і побудова карти
fetch('/rides/map?rideId=2')
    .then(res => res.json())
    .then(data => {
        const {
        vehicleCurrentPosition,
        departure,
        destination,
        toPAXroute,
        route } = data;

        // Додавання маркерів
        taxiMarker = L.marker(vehicleCurrentPosition, {
            icon: L.icon({
                iconUrl: 'ico/vehicle.png',
                iconSize: [50, 50]
            })
        }).addTo(map).bindPopup("🚖 Таксі");

        L.marker(departure).addTo(map).bindPopup("📍 Клієнт");
        L.marker(destination).addTo(map).bindPopup("🏁 Призначення");

        // Відображення маршрутів
        L.polyline(toPAXroute, { color: 'red', weight: 4 }).addTo(map);
        L.polyline(route, { color: 'blue', weight: 4 }).addTo(map);

        // Центрування карти
        const bounds = L.latLngBounds([...toPAXroute, ...route]);
        map.fitBounds(bounds);
    });

// Оновлення позиції таксі
/*fetch('/rides/getPosition?vehicleId=1')
    .then(res => res.json())
    .then(currentPosition => {
        if (taxiMarker) {
            taxiMarker.slideTo(currentPosition.currentPosition, {
                duration: 1000,  // плавний перехід за 1 секунду
                keepAtCenter: false
            });
        }
    });*/

    setInterval(() => {
        fetch('/rides/getPosition?vehicleId=1')
            .then(res => res.json())
            .then(currentPosition => {
                if (taxiMarker) {
                    // ТУТ! Витягуємо масив [lat, lng]
                    taxiMarker.setLatLng(currentPosition);
                }
            });
    }, 3000);


