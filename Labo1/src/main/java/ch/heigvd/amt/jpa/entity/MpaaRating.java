package ch.heigvd.amt.jpa.entity;

import java.util.Objects;

public enum MpaaRating {
    G("G"), PG("PG"), PG_13("PG-13"), R("R"), NC_17("NC-17");
    private final String rate;

    MpaaRating(String rating) {
        rate = rating;
    }

    public String getName() {
        return rate;
    }

    @Override
    public String toString() {
        return rate;
    }

    public static MpaaRating getFromName(String name){
        for(MpaaRating rating : MpaaRating.values()){
            if(Objects.equals(name, rating.rate)){
                return rating;
            }
        }
        return null;
    }
}
