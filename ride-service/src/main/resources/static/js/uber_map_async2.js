const map = L.map('map').setView([48.919293, 24.712843], 13);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

let taxiMarker;
let destinationCoords;
let updateInterval;

async function getDistanceBackend(coord1, coord2) {
    console.log("getDistanceBackend:: coord1:", coord1);
    console.log("getDistanceBackend:: coord2:", coord2);
    const dto = {
        departure: {
            latitude: coord1.lat,
            longitude: coord1.lng
        },
        destination: {
            latitude: coord2.lat,
            longitude: coord2.lng
        }
    };
    console.log("DTO на бекенд:", JSON.stringify(dto, null, 2));
    const res = await fetch('/rides/distanceForMap', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(dto)
    });
    if (!res.ok) {
        throw new Error(`HTTP error! Status: ${res.status}`);
    }
    const data = await res.json();
    console.log('getDistanceBackend:: Відстань:', data);
    return data;
}

async function initializeMap() {
  try {
    const res = await fetch('/rides/map?rideId=2');
    const data = await res.json();
    const {
      vehicleCurrentPosition,
      departure,
      destination,
      toPAXroute,
      route
    } = data;

    departureCoords = {
      lat: departure[0],
      lng: departure[1]
    };
    destinationCoords = {
      lat: destination[0],
      lng: destination[1]
    };

    taxiMarker = L.marker(vehicleCurrentPosition, {
      icon: L.icon({
        iconUrl: 'ico/vehicle.png',
        iconSize: [50, 50]
      })
    }).addTo(map).bindPopup("🚕 Таксі");

    L.marker(departure).addTo(map).bindPopup("📍 Клієнт");
    L.marker(destination).addTo(map).bindPopup("🏁 Призначення");
    L.polyline(toPAXroute, { color: 'red', weight: 4 }).addTo(map);
    L.polyline(route, { color: 'blue', weight: 4 }).addTo(map);
    const bounds = L.latLngBounds([...toPAXroute, ...route]);
    map.fitBounds(bounds);

    // 🚕 Рух до пасажира
    await startTracking(departureCoords);

    // 🕓 Коли прибули — чекаємо на зміну статусу
    pollRideStatus(2, destinationCoords);

  } catch (err) {
    console.error('Помилка ініціалізації карти:', err);
  }
}

function pollRideStatus(rideId, arrivalCoords) {
  const statusCheckInterval = setInterval(async () => {
    try {
      const res = await fetch(`/rides/getStatus?rideId=${rideId}`);
      const status = await res.text();
      console.log("📦 Поточний статус поїздки:", status);

      if (status === 'PAX_ONBOARD') {
        console.log("✅ Пасажир на борту, стартуємо другу частину маршруту");
        clearInterval(statusCheckInterval);
        startTracking(arrivalCoords);
      }
    } catch (err) {
      console.error('❌ Помилка при перевірці статусу:', err);
    }
  }, 2000);
}

function startTracking(arrivalCoords) {
  return new Promise((resolve) => {
    updateInterval = setInterval(() => {
      (async () => {
        try {
          const currentPosition = await fetch('/rides/getPosition?vehicleId=1')
            .then(res => res.json());
          const currentLatLng = {
            lat: currentPosition[0],
            lng: currentPosition[1]
          };
          console.log("📍 Отримано позицію:", currentLatLng);
          if (taxiMarker) {
            taxiMarker.setLatLng(currentLatLng);
            const distance = await getDistanceBackend(currentLatLng, arrivalCoords);
            console.log("📏 Відстань до призначення:", distance + " км");
            if (distance < 0.01) {
              console.log("🚕 Таксі прибуло, зупиняємо оновлення");
              clearInterval(updateInterval);
              resolve(); // 🔁 коли доїхав — резолвимо проміс
            }
          }
        } catch (err) {
          console.error('❌ Помилка отримання позиції або обробки:', err);
        }
      })();
    }, 2000);
  });
}

// Запускаємо ініціалізацію карти
initializeMap();