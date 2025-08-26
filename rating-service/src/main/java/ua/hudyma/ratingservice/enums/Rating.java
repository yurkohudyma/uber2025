package ua.hudyma.ratingservice.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Rating {

    EXCELLENT (5),
    GOOD (4),
    FAIR (3),
    POOR (2),
    DISGUSTING (1);

    private final double stars;

    public double getStars() {
        return stars;
    }
}
