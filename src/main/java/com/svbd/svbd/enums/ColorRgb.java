package com.svbd.svbd.enums;

public enum ColorRgb {

    BAHAMA_BLUE((byte) 31, (byte) 73, (byte) 125),
    LIGHT_GRAYISH_BLUE((byte) 184, (byte) 204, (byte) 228),
    VERY_SOFT_MAGENTA((byte) 242, (byte) 138, (byte) 237),
    BRIGHT_YELLOW((byte) 254, (byte) 221, (byte) 62),
    WHITE((byte) 255,(byte) 255,(byte) 255);

    private final byte red;
    private final byte green;
    private final byte blue;

    ColorRgb(byte red, byte green, byte blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }


    public byte[] getRgbColor() {
        var rgb = new byte[3];
        rgb[0] = this.red;
        rgb[1] = this.green;
        rgb[2] = this.blue;
        return rgb;
    }
}
