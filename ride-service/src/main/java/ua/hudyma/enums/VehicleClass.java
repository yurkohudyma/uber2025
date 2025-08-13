package ua.hudyma.enums;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public enum VehicleClass {

    COMPACT (1.0),     // Невеликий міський автомобіль (2-4 місця), економний
    ECONOMY (1.0),     // Бюджетний варіант (UberX) — стандартне авто для щоденних поїздок
    EXECUTIVE (1.5),   // Бізнес-клас (Uber Black) — високий комфорт, для ділових поїздок
    LIMO (1.5),        // Лімузин — преміум-сегмент, для урочистих подій
    LUXURY(1.0),      // Люкс-авто — вищий комфорт, дорогі бренди
    LUXSEDAN(1.2),    // Люксовий седан — преміум комфорт у кузові седан
    LUXSUV (1.2),      // Люксовий SUV — преміум позашляховик
    PREMIUM (1.3),     // Покращений комфорт — між стандартом і бізнесом
    STANDARD (1.1),    // Середній клас авто (наприклад, Uber Comfort)
    SUV(1.2),         // Позашляховик — більше місця, підходить для родин чи багажу
    VAN(1.5),         // Мікроавтобус — до 6-7 пасажирів, зручний для груп
    XL(2.0),          // Збільшене авто (UberXL) — 6+ пасажирів
    XXL(2.0);          // Дуже велике авто або мінівен — для великих груп або вантажу
    private final Double priceCoefficient;
    public BigDecimal getPriceCoefficient() {
        return BigDecimal.valueOf(priceCoefficient);
    }



}

