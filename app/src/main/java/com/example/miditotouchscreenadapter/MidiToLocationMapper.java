package com.example.miditotouchscreenadapter;

import android.graphics.PointF;

public class MidiToLocationMapper {
    private final int FIRST_KEY_VALUE = 21;
    private final int HEIGHT = 800;
    private final int DO_WIDTH = 230;
    private final int RE_WIDTH = 550;
    private final int MI_WIDTH = 870;
    private final int FA_WIDTH = 1160;
    private final int SOL_WIDTH = 1460;
    private final int LA_WIDTH = 1770;
    private final int SI_WIDTH = 2100;

    public PointF mapNoteToLocation(int note) {
        switch ((note - FIRST_KEY_VALUE) % 12) {
            case 0:
            case 1:
                return new PointF(LA_WIDTH,HEIGHT);
            case 2:
                return new PointF(SI_WIDTH,HEIGHT);
            case 3:
            case 4:
                return new PointF(DO_WIDTH,HEIGHT);
            case 5:
            case 6:
                return new PointF(RE_WIDTH,HEIGHT);
            case 7:
                return new PointF(MI_WIDTH,HEIGHT);
            case 8:
            case 9:
                return new PointF(FA_WIDTH,HEIGHT);
            case 10:
            case 11:
                return new PointF(SOL_WIDTH,HEIGHT);
            default:
                return new PointF(0,0);
        }
    }
}
