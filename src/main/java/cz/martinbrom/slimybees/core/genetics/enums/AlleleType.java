package cz.martinbrom.slimybees.core.genetics.enums;

import cz.martinbrom.slimybees.core.genetics.alleles.AlleleValue;

public class AlleleType {

    public enum Fertility implements AlleleValue<Integer> {
        LOW(1, true),
        NORMAL(2, true),
        HIGH(3),
        VERY_HIGH(4);

        private final int fertility;
        private final boolean dominant;

        Fertility(int fertility) {
            this(fertility, false);
        }

        Fertility(int fertility, boolean dominant) {
            this.fertility = fertility;
            this.dominant = dominant;
        }

        @Override
        public boolean isDominant() {
            return dominant;
        }

        @Override
        public Integer getValue() {
            return fertility;
        }
    }

    public enum Speed implements AlleleValue<Integer> {
        VERY_SLOW(1, true),
        SLOW(4, true),
        NORMAL(6),
        FAST(8, true),
        VERY_FAST(11);

        private final int speed;
        private final boolean dominant;

        Speed(int speed) {
            this(speed, false);
        }

        Speed(int speed, boolean dominant) {
            this.speed = speed;
            this.dominant = dominant;
        }

        @Override
        public boolean isDominant() {
            return dominant;
        }

        @Override
        public Integer getValue() {
            return speed;
        }
    }

}
