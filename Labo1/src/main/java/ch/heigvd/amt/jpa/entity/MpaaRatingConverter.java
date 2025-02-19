package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MpaaRatingConverter implements AttributeConverter<MpaaRating, String> {

    // Converts Java enum to database column (String)
    @Override
    public String convertToDatabaseColumn(MpaaRating rating) {
        if (rating == null) {
            return null;
        }
        return rating.getName();  // Use name of the enum as the string value in the database
    }

    // Converts database column (String) back to Java enum
    @Override
    public MpaaRating convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        // Match the string to the correct enum value

        MpaaRating rating = MpaaRating.getFromName(dbData.replace("_", "-").trim().toUpperCase());
        if(rating != null){
            return rating;
        }else{
            throw new RuntimeException("Unknown value for MpaaRating: " + dbData);
        }
    }
}