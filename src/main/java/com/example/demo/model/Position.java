package com.example.demo.model;

public enum Position {
    LEFT_PROP("Pilier Gauche", 1),
    HOOKER("Talonneur", 2),
    RIGHT_PROP("Pilier Droit", 3),
    LEFT_LOCK("Deuxième Ligne Gauche", 4),
    RIGHT_LOCK("Deuxième Ligne Droite", 5),
    LEFT_FLANKER("Troisième Ligne Gauche", 6),
    RIGHT_FLANKER("Troisième Ligne Droite", 7),
    CENTER_FLANKER("Troisième Ligne Centre", 8),
    SCRUM_HALF("Demi de Mêlée", 9),
    FLY_HALF("Demi d'Ouverture", 10),
    LEFT_WINGER("Ailier Gauche", 11),
    FIRST_CENTRE_THREE_QUARTER("Premier centre", 12),
    SECOND_CENTRE_THREE_QUARTER("Second centre", 13),
    RIGHT_WINGER("Ailier Droit", 14),
    FULL_BACK("Arrière", 15),

    SUBSTITUTE_HOOKER("Remplaçant Talonneur", 16),
    SUBSTITUTE_LEFT_PROP("Remplaçant Pilier Gauche", 17),
    SUBSTITUTE_RIGHT_PROP("Remplaçant Pilier Droit", 18),
    SUBSTITUTE_LOCK("Remplaçant Deuxième Ligne", 19),
    SUBSTITUTE_BACK_ROW("Remplaçant Troisième Ligne", 20),
    SUBSTITUTE_SCRUM_HALF("Remplaçant Demi de Mêlée", 21),
    SUBSTITUTE_BACKS_1("Remplaçant Arrière 1", 22),
    SUBSTITUTE_BACKS_2("Remplaçant Arrière 2", 23);

    private final String label;
    private final int jerseyNumber;

    Position(String label, int jerseyNumber) {
        this.label = label;
        this.jerseyNumber = jerseyNumber;
    }

    public String getLabel() {
        return label;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }
}