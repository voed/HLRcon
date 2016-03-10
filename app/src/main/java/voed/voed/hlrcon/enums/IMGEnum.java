package voed.voed.hlrcon.enums;

/**
 * Created by Roma on 11.01.2015.
 */
public enum IMGEnum {

    CS(0),
    HL(1);

    private int id;

    IMGEnum(int index) {
        id = index;
    }

    public int index() {
        return id;
    }
}
